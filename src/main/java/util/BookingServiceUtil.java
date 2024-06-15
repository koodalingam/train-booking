package util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

import dto.Ticket;
import dto.Train;
import dto.User;
import exception.CheckedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Database;
import model.IdGenerator;
import util.Constants.ErrorCode;

public class BookingServiceUtil {

	static {
		Train train = new Train(Constants.DEFAULT_TRAIN_ID, Constants.DEFAULT_TRAIN_SECTION, Constants.DEFAULT_TRAIN_SECTION_SEAT_COUNT);
		Database.addTrain(train);
	}

	public static String getSectionDisplayName(int sectionId) throws Exception{
		if(sectionId == 1) {
			return "A";
		}else if( sectionId == 2) {
			return "B";
		}else {
			throw new CheckedException(ErrorCode.INVALID_SECTION);
		}
	}

	public static int getSection(String section) throws Exception{
		if("A".equals(section)) {
			return 1;
		}else if("B".equals(section)) {
			return 2;
		}else {
			throw new CheckedException(ErrorCode.INVALID_SECTION);
		}
	}

	public static Train getTrain() {
		return Database.getTrain(Constants.DEFAULT_TRAIN_ID);
	}

	public static void writeResponse(HttpServletResponse response, JSONObject  json) throws IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
	}


	public static Ticket createTicket(HttpServletRequest request) throws Exception{

		String email = request.getParameter("email");
		String firstName = request.getParameter("first_name");
		String lastName = request.getParameter("last_name");

		Long userId = Database.getUserIdByEmail(email);

		if(userId != null) {
			Ticket ticket = Database.getTicketbyUserId(Long.valueOf(userId));
			if(ticket!=null) {
				throw new CheckedException(ErrorCode.ALREADY_BOOKED);
			}
		}else {
			userId = IdGenerator.getNextId();
			User user   = new User(userId, firstName, lastName, email);
			Database.addUser(user);
		}

		long ticketId = IdGenerator.getNextId();
		HashMap<String, Integer> seatMap =  getSectionAndSeatNo(ticketId);

		if(seatMap.isEmpty()) {
			throw new CheckedException(ErrorCode.NO_AVAILABLE_SEATS);
		}

		Ticket ticket = new Ticket(ticketId, userId, System.currentTimeMillis());
		ticket.setSeatNo(seatMap.get("seat_no"));
		ticket.setSection(seatMap.get("section"));

		Database.addTicket(ticket);
		return ticket;
	}



	/**
	 * This will will return the next available section and seat no
	 * @param ticketId
	 * @return
	 */
	public static HashMap<String, Integer> getSectionAndSeatNo(long ticketId) throws Exception{
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		try {
			Train trainObj = Database.getTrain(Constants.DEFAULT_TRAIN_ID);
			for(int section=1;section<trainObj.getNoOfSections();section++) {
				int seatNo = getSeatNo(trainObj, section, ticketId);
				if(seatNo>0) {
					map.put("seat_no", seatNo);
					map.put("section", section);
					break;
				}
			}
		}catch(Exception e) {
			System.out.println("Error while getting seat for the train " );
			e.printStackTrace();
		}
		return map;
	}


	/**
	 * This will return the next available seat no from the given section
	 * @param trainObj
	 * @param section
	 * @param ticketId
	 * @return
	 */
	private static int getSeatNo(Train trainObj, int section, long ticketId) throws Exception{
		int seat = 0;
		ConcurrentHashMap<Integer,Long> seatMap =  Database.getSeatMap(section);
		if(seatMap.isEmpty()) {
			seat = 1;
		}else {
			ArrayList<Integer> seatList = new ArrayList<Integer>(seatMap.keySet());
			Collections.sort( seatList);
			int lastSeat = seatList.get(seatList.size());
			if(lastSeat<trainObj.getNoOfSeatsPerSection()) {
				seat = lastSeat+1;
			}else {
				for(int i=1;i<trainObj.getNoOfSeatsPerSection();i++) {
					if(seatMap.get(i)==null) {
						seat = i;	
						break;
					}
				}
			}
		}

		if(seat>0) {
			seatMap.put(seat, ticketId);
			Database.setSeatMap(section, seatMap);
		}
		return seat;
	}

	/**
	 * 
	 * @param ticket
	 * @param seatNo
	 * @return
	 */
	public static Ticket modifySeat(Ticket ticket,  int seatNo) throws Exception{
		try {
			ConcurrentHashMap<Integer,Long> seatMap = Database.getSeatMap(ticket.getSection());
			if(!seatMap.containsKey(seatNo)) {
				seatMap.put(seatNo, ticket.getTicketId());
				seatMap.remove(ticket.getSeatNo());
				ticket.setSeatNo(seatNo);
				Database.addTicket(ticket);
				Database.setSeatMap(ticket.getSection(), seatMap);
				return ticket;
			}else {
				throw new CheckedException(ErrorCode.UNABLE_MODIFY_TICKET);
			}
		}catch(Exception e) {
			System.out.println("Error while modify seat for the user "+ticket.getUserId() );
			e.printStackTrace();
			throw e;
		}
	}


	/**
	 * 
	 * @param ticket
	 * @return
	 */
	public static void removeSeatAllocation(Ticket ticket) throws Exception{
		try {
			ConcurrentHashMap<Integer,Long> seatMap = Database.getSeatMap(ticket.getSection());
			seatMap.remove(ticket.getSeatNo());
			Database.setSeatMap(ticket.getSection(), seatMap);
		}catch(Exception e) {
			System.out.println("Error while remove Seat Allocation for the userId "+ticket.getUserId() + " ticketId: "+ticket.getTicketId() );
			e.printStackTrace();
			throw e;
		}
	}


	/**
	 * 
	 * @param userId
	 * @throws Exception
	 */
	public static void removeUserFromTrain(long userId) throws Exception{
		try {
			Ticket ticket = Database.getTicketbyUserId(Long.valueOf(userId));
			if(ticket == null) {
				throw new CheckedException(ErrorCode.NO_TICKET_FOUND);
			}
			removeSeatAllocation(ticket);
			Database.deleteTicket(ticket);
		}catch(Exception e) {
			System.out.println("Error while deleting the user "+userId );
			e.printStackTrace();
			throw e;
		}
	}

	public static JSONObject viewSeatDetailsOfSection(int section) {
		JSONObject json = new JSONObject();
		try {
			ConcurrentHashMap<Integer,Long> seatMap = Database.getSeatMap(section);

			ArrayList<Integer> seatList = new ArrayList<Integer>(seatMap.keySet());
			Collections.sort( seatList);
			for(Integer seatNo: seatList) {
				long ticketId = seatMap.get(seatNo);
				Ticket ticket = Database.getTicket(ticketId);
				User user = Database.getUser(ticket.getUserId());
				json.put(String.valueOf(seatNo), user.getDisplayName());
			}

		}catch(Exception e) {
			System.out.println("Error while viewing seating details of the section"+section );
			e.printStackTrace();
			throw e;
		}
		return json;
	}

}
