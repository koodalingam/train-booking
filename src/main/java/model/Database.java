package model;

import java.util.Map;

import dto.Ticket;
import dto.Train;
import dto.User;

/**
 * We are going with H2 Database and hash database also done in HashDatabase.java
 * After any time we can move to any database with just replace those methods
 */
public class Database {

	private static String inmemoryType= "h2";
	
	public static String getDatabaseType() {
		return inmemoryType;
	}

	private static AbstractDatabase getDatabase() {
		if("h2".equals(inmemoryType)) {
			return new H2Database();
		}else {
			return new HashDatabase();
		}
	}
	
	/**
	 * It will put the User Object for user Id
	 * @param user
	 */
	public static void addUser(User user)  throws Exception {
		getDatabase().addUser(user);
	}

	public static Long getUserIdByEmail(String email)  throws Exception {
		return getDatabase().getUserIdByEmail(email);
	}

	/**
	 * It will return the User Object  by user Id
	 * @param userId
	 */
	public static User getUser(long userId)  throws Exception {
		return getDatabase().getUser(userId);
	}


	/**
	 * 
	 * @param user
	 * @throws Exception
	 */
	public static void deleteUser(User user)  throws Exception {
		getDatabase().deleteUser(user);
	}

	/**
	 * It will put the Train Object for user Id
	 * @param ticket
	 */
	public static void addTicket(Ticket ticket)  throws Exception {
		getDatabase().addTicket(ticket);
	}

	/**
	 * It will return the Ticket Object  by user Id
	 * @param userId
	 * @return
	 */
	public static Ticket getTicketbyUserId(long userId) throws Exception {
		return getDatabase().getTicketbyUserId(userId);
	}

	/**
	 * 
	 * @param ticketId
	 * @return
	 */
	public static Ticket getTicket(long ticketId) throws Exception {
		return getDatabase().getTicket(ticketId);
	}

	/**
	 * Here we will delete user vs ticket mapping alone 
	 * @param ticket
	 */
	public static void deleteTicket(Ticket ticket)  throws Exception {
		getDatabase().deleteTicket(ticket);
	}

	/**
	 * It will return the Train Object
	 * @param train
	 */
	public static void addTrain(Train train)  throws Exception {
		getDatabase().addTrain(train);
	}

	/**
	 * It will return the Train Object  by train Id
	 * @param trainId
	 * @return
	 */
	public static Train getTrain(long trainId) throws Exception {
		return getDatabase().getTrain(trainId);
	}

	public static Map<Integer,Long> getSeatMap(int section)   throws Exception  {
		return getDatabase().getSeatMap(section);
	}
	
	public static Ticket modifySeat(Ticket ticket,  int newSeatNo) throws Exception{
		return getDatabase().modifySeat(ticket, newSeatNo);
	}

}
