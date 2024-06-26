package util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import dto.Ticket;
import dto.Train;
import dto.User;
import exception.CheckedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Database;
import model.H2Database;
import model.IdGenerator;
import util.Constants.ErrorCode;

public class BookingServiceUtil {

	static {
		try {
			if("h2".equals(Database.getDatabaseType())) {
				H2Database.initialise();
			}

			Train train = Database.getTrain(Constants.DEFAULT_TRAIN_ID);
			if(train==null) {
				train = new Train(Constants.DEFAULT_TRAIN_ID, Constants.DEFAULT_TRAIN_SECTION, Constants.DEFAULT_TRAIN_SECTION_SEAT_COUNT);
				Database.addTrain(train);
			}
		}catch(Exception e) {
			System.out.println("Error while adding train");
			e.printStackTrace();
		}
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

	public static Train getTrain() throws Exception{
		return Database.getTrain(Constants.DEFAULT_TRAIN_ID);
	}

	public static void writeResponse(HttpServletResponse response, JSONObject  json) throws IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
	}


	public static Ticket createTicket(HttpServletRequest request) throws Exception{
		try {
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
			Ticket ticket = new Ticket(ticketId, userId, System.currentTimeMillis());

			setSectionAndSeatNo(ticket);

			if(ticket.getSeatNo() ==0) {
				throw new CheckedException(ErrorCode.NO_AVAILABLE_SEATS);
			}

			Database.addTicket(ticket);
			return ticket;
		}catch(Exception e) {
			System.out.println("Error while ticket booking email");
			throw e;
		}
	}



	/**
	 * This will will return the next available section and seat no
	 * @param ticket
	 */
	public static void setSectionAndSeatNo(Ticket ticket) throws Exception{
		try {
			Train trainObj = Database.getTrain(Constants.DEFAULT_TRAIN_ID);
			for(int section=1;section<=trainObj.getNoOfSections();section++) {
				int seatNo = getSeatNo(trainObj, section, ticket);
				if(seatNo>0) {
					ticket.setSeatNo(seatNo);
					ticket.setSection(section);
					break;
				}
			}
		}catch(Exception e) {
			System.out.println("Error while getting seat for the train " );
			e.printStackTrace();
		}
	}


	/**
	 * This will return the next available seat no from the given section
	 * @param trainObj
	 * @param section
	 * @param ticket
	 * @return
	 */
	private static int getSeatNo(Train trainObj, int section, Ticket ticket) throws Exception{
		int seat = 0;
		Map<Integer,Long> seatMap =  Database.getSeatMap(section);
		if(seatMap.isEmpty()) {
			seat = 1;
		}else {
			ArrayList<Integer> seatList = new ArrayList<Integer>(seatMap.keySet());
			Collections.sort( seatList);
			int lastSeat = seatList.get(seatList.size()-1);
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
			ticket.setSeatNo(seat);
		}
		return seat;
	}

	/**
	 * 
	 * @param ticket
	 * @param newSeatNo
	 * @return
	 */
	public static Ticket modifySeat(Ticket ticket,  int newSeatNo) throws Exception{
		try {
			Map<Integer,Long> seatMap = Database.getSeatMap(ticket.getSection());
			if(!seatMap.containsKey(newSeatNo)) {
				return Database.modifySeat(ticket, newSeatNo);
			}else {
				throw new CheckedException(ErrorCode.UNABLE_MODIFY_TICKET);
			}
		}catch(Exception e) {
			System.out.println("Error while modify seat for the user "+ticket.getUserId() );
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
			Database.deleteTicket(ticket);
		}catch(Exception e) {
			System.out.println("Error while deleting the user "+userId );
			throw e;
		}
	}

	public static JSONArray viewSeatDetailsOfSection(int section) throws Exception{
		JSONArray array = new JSONArray();
		try {
			Map<Integer,Long> seatMap = Database.getSeatMap(section);
			ArrayList<Integer> seatList = new ArrayList<Integer>(seatMap.keySet());
			Collections.sort( seatList);
			for(Integer seatNo: seatList) {
				long ticketId = seatMap.get(seatNo);
				Ticket ticket = Database.getTicket(ticketId);
				User user = Database.getUser(ticket.getUserId());
				JSONObject temp = new JSONObject();
				temp.put("seat_no", String.valueOf(seatNo));
				temp.put("user_name", user.getDisplayName());
				temp.put("user_id", String.valueOf(user.getUserId()));

				array.put(temp);
			}

		}catch(Exception e) {
			System.out.println("Error while viewing seating details of the section"+section );
			throw e;
		}
		return array;
	}

	public static void validateUser(long userId) throws Exception{
		User user = Database.getUser(userId);
		if(user == null) {
			throw new CheckedException(ErrorCode.NO_USER_FOUND);
		}
	}

}
