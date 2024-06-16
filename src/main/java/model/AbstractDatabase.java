package model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dto.Ticket;
import dto.Train;
import dto.User;

/**
 * We are going with H2 Database and hash database also done in HashDatabase.java
 * After any time we can move to any database with just replacing  those methods
 */
public abstract class AbstractDatabase {


	/**
	 * It will put the User Object for user Id
	 * @param user
	 */
	public abstract void addUser(User user)  throws Exception;

	public abstract Long getUserIdByEmail(String email)  throws Exception;

	/**
	 * It will return the User Object  by user Id
	 * @param userId
	 */
	public abstract User getUser(long userId)  throws Exception;

	/**
	 * 
	 * @param user
	 * @throws Exception
	 */
	public abstract void deleteUser(User user)  throws Exception;

	/**
	 * It will put the Train Object for user Id
	 * @param ticket
	 */
	public abstract void addTicket(Ticket ticket)  throws Exception;

	/**
	 * It will return the Ticket Object  by user Id
	 * @param userId
	 * @return
	 */
	public abstract Ticket getTicketbyUserId(long userId) throws Exception;

	/**
	 * 
	 * @param ticketId
	 * @return
	 */
	public abstract Ticket getTicket(long ticketId) throws Exception;

	/**
	 * Here we will delete user vs ticket mapping alone 
	 * @param ticket
	 */
	public abstract void deleteTicket(Ticket ticket)  throws Exception ;

	/**
	 * It will return the Train Object
	 * @param train
	 */
	public abstract void addTrain(Train train)  throws Exception;

	/**
	 * It will return the Train Object  by train Id
	 * @param trainId
	 * @return
	 */
	public abstract Train getTrain(long trainId) throws Exception;

	public abstract Map<Integer,Long> getSeatMap(int section)  throws Exception ;

	public abstract Ticket modifySeat(Ticket ticket,  int newSeatNo) throws Exception;


}
