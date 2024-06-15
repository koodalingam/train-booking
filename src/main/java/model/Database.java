package model;

import java.util.concurrent.ConcurrentHashMap;

import dto.Ticket;
import dto.Train;
import dto.User;

public class Database {

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
	public static void addUser(User user)  throws Exception {
		emailVsUserId.put(user.getEmail(), user.getUserId());
		userIdVsUserMap.put(user.getUserId(), user);
	}

	public static Long getUserIdByEmail(String email) {
		return emailVsUserId.get(email);
	}

	/**
	 * It will return the User Object  by user Id
	 * @param userId
	 */
	public static User getUser(long userId) {
		return userIdVsUserMap.get(userId);
	}


	/**
	 * 
	 * @param user
	 * @throws Exception
	 */
	public static void deleteUser(User user)  throws Exception {
		emailVsUserId.remove(user.getEmail());
		userIdVsUserMap.remove(user.getUserId());
	}

	/**
	 * It will put the Train Object for user Id
	 * @param ticket
	 */
	public static void addTicket(Ticket ticket)  throws Exception {
		ticketIdVsTicketMap.put(ticket.getTicketId(), ticket);
		userIdVsTicketIdMap.put(ticket.getUserId(), ticket.getTicketId());
	}

	/**
	 * It will return the Ticket Object  by user Id
	 * @param userId
	 * @return
	 */
	public static Ticket getTicketbyUserId(long userId) {
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
	public static Ticket getTicket(long ticketId) {
		return ticketIdVsTicketMap.get(ticketId);
	}

	/**
	 * Here we will delete user vs ticket mapping alone 
	 * @param ticket
	 */
	public static void deleteTicket(Ticket ticket) {
		userIdVsTicketIdMap.remove(ticket.getUserId());
	}

	/**
	 * It will return the Train Object
	 * @param train
	 */
	public static void addTrain(Train train)  {
		trainIdVstrainMap.put(train.getTrainId(), train);
	}

	/**
	 * It will return the Train Object  by train Id
	 * @param trainId
	 * @return
	 */
	public static Train getTrain(long trainId) {
		return trainIdVstrainMap.get(trainId);
	}

	public static ConcurrentHashMap<Integer,Long> getSeatMap(int section)  {
		return sectionVsSeatMap.get(section);
	}

	public static void setSeatMap(int section, ConcurrentHashMap<Integer,Long> map) throws Exception {
		sectionVsSeatMap.put(section, map);
	}

}
