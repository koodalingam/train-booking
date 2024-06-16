package model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dto.Ticket;
import dto.Train;
import dto.User;

public class HashDatabase extends AbstractDatabase {

	private static ConcurrentHashMap<Long, Ticket> ticketIdVsTicketMap = new ConcurrentHashMap<Long, Ticket>();

	private static ConcurrentHashMap<Long, User> userIdVsUserMap = new ConcurrentHashMap<Long, User>();

	private static ConcurrentHashMap<Long, Long> userIdVsTicketIdMap = new ConcurrentHashMap<Long, Long>();

	private static ConcurrentHashMap<String, Long > emailVsUserId = new ConcurrentHashMap<String, Long>();

	private static ConcurrentHashMap<Long, Train> trainIdVstrainMap = new ConcurrentHashMap<Long, Train>();
	private static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer,Long> > sectionVsSeatMap = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer,Long>>();

	/**
	 * It will put the User Object for user Id
	 * @param user
	 */
	public  void addUser(User user)  throws Exception {
		emailVsUserId.put(user.getEmail(), user.getUserId());
		userIdVsUserMap.put(user.getUserId(), user);
	}

	public Long getUserIdByEmail(String email) {
		return emailVsUserId.get(email);
	}

	/**
	 * It will return the User Object  by user Id
	 * @param userId
	 */
	public User getUser(long userId) {
		return userIdVsUserMap.get(userId);
	}


	/**
	 * 
	 * @param user
	 * @throws Exception
	 */
	public void deleteUser(User user)  throws Exception {
		emailVsUserId.remove(user.getEmail());
		userIdVsUserMap.remove(user.getUserId());
	}

	/**
	 * It will put the Train Object for user Id
	 * @param ticket
	 */
	public void addTicket(Ticket ticket)  throws Exception {
		ticketIdVsTicketMap.put(ticket.getTicketId(), ticket);
		userIdVsTicketIdMap.put(ticket.getUserId(), ticket.getTicketId());
		addSeatAllocation(ticket);
	}

	/**
	 * It will return the Ticket Object  by user Id
	 * @param userId
	 * @return
	 */
	public Ticket getTicketbyUserId(long userId) {
		if(userIdVsTicketIdMap.containsKey(userId)) {
			return getTicket(userIdVsTicketIdMap.get(userId));
		}
		return null;

	}

	/**
	 * 
	 * @param ticketId
	 * @return
	 */
	public Ticket getTicket(long ticketId) {
		return ticketIdVsTicketMap.get(ticketId);
	}

	/**
	 * Here we will delete user vs ticket mapping alone 
	 * @param ticket
	 */
	public void deleteTicket(Ticket ticket) throws Exception {
		ticketIdVsTicketMap.remove(ticket.getTicketId());
		userIdVsTicketIdMap.remove(ticket.getUserId());
		removeSeatAllocation(ticket);
	}

	/**
	 * It will return the Train Object
	 * @param train
	 */
	public void addTrain(Train train)  {
		trainIdVstrainMap.put(train.getTrainId(), train);
	}

	/**
	 * It will return the Train Object  by train Id
	 * @param trainId
	 * @return
	 */
	public Train getTrain(long trainId) {
		return trainIdVstrainMap.get(trainId);
	}

	public Map<Integer,Long> getSeatMap(int section)  {
		return sectionVsSeatMap.get(section) != null?sectionVsSeatMap.get(section): new ConcurrentHashMap<Integer,Long>();
	}

	public void setSeatMap(int section, Map<Integer,Long> map) throws Exception {
		sectionVsSeatMap.put(section, (ConcurrentHashMap<Integer,Long> ) map);
	}
	
	public Ticket modifySeat(Ticket ticket,  int newSeatNo) throws Exception{

		Map<Integer,Long> seatMap = this.getSeatMap(ticket.getSection());
		seatMap.put(newSeatNo, ticket.getTicketId());
		seatMap.remove(ticket.getSeatNo());
		ticket.setSeatNo(newSeatNo);
		this.setSeatMap(ticket.getSection(), seatMap);
		return ticket;
		
	}

	/**
	 * 
	 * @param ticket
	 * @return
	 */
	public void removeSeatAllocation(Ticket ticket) throws Exception{
		try {
			Map<Integer,Long> seatMap = this.getSeatMap(ticket.getSection());
			seatMap.remove(ticket.getSeatNo());
			this.setSeatMap(ticket.getSection(), seatMap);
		}catch(Exception e) {
			System.out.println("Error while remove Seat Allocation for the userId "+ticket.getUserId() + " ticketId: "+ticket.getTicketId() );
			throw e;
		}
	}

	/**
	 * 
	 * @param ticket
	 * @return
	 */
	public void addSeatAllocation(Ticket ticket) throws Exception{
		try {
			Map<Integer,Long> seatMap = this.getSeatMap(ticket.getSection());
			seatMap.put(ticket.getSeatNo(), ticket.getTicketId());
			this.setSeatMap(ticket.getSection(), seatMap);
		}catch(Exception e) {
			System.out.println("Error while remove Seat Allocation for the userId "+ticket.getUserId() + " ticketId: "+ticket.getTicketId() );
			throw e;
		}
	}

}
