package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dto.Ticket;
import dto.Train;
import dto.User;

public class H2Database extends AbstractDatabase {

	private static String dbName="booking";
	private static Connection conn = null;

	/**
	 * Initialize DB and creating temp tables
	 */
	public static void initialise() throws Exception{
		Statement statement = null;
		try {
			Class.forName("org.h2.Driver");
			conn =  DriverManager.getConnection("jdbc:h2:mem:"+dbName); 
			System.out.println( "Creating Temp Tables");
			statement = conn.createStatement();

			ArrayList<String> list  = new ArrayList<String>();
			ResultSet showtableRs  = statement.executeQuery("show tables");
			while(showtableRs!=null && showtableRs.next()){
				String tempTableName = showtableRs.getString("table_name");
				System.out.println( "tempTableName : "+tempTableName);
				list.add(tempTableName);

			}

			if(!list.contains("USERS")) {
				String userTable = "create table Users ( user_id bigint, first_name char(250), last_name char(250), email char(250),  PRIMARY KEY(user_id))"; // no i18n
				statement.execute(userTable);
			}

			if(!list.contains("TRAINS")) {
				String trainTable = "create table Trains ( train_id bigint, no_of_section int, no_of_seat int,  PRIMARY KEY(train_id))"; // no i18n
				statement.execute(trainTable);
			}

			if(!list.contains("TICKETS")) {
				String ticketTable = "create table Tickets ( ticket_id bigint, user_id bigint, time bigint,  section int, seat_no int,  PRIMARY KEY(ticket_id))"; // no i18n
				statement.execute(ticketTable);
			}

		}catch(Exception e) {
			System.out.println("Error while creating temp tables ");
			e.printStackTrace();
		}finally {
			if(statement !=null) {
				statement.close();
			}
		}

	}


	private  Connection getConnection()  throws Exception{
		if(conn == null) {
			initialise();
		}
		return conn;
	}

	private  void close( Statement stmt, ResultSet rs) throws SQLException {
		if(stmt !=null) {
			stmt.close();
		}

		if(rs !=null) {
			rs.close();
		}
	}

	/**
	 * It will put the User Object for user Id
	 * @param user
	 */
	public void addUser(User user)  throws Exception {
		Connection conn =  null;
		PreparedStatement statement  = null;
		try {
			conn = getConnection();
			String insertSql = "insert into Users values ( ?, ?, ?, ?)";
			statement  = conn.prepareStatement(insertSql);
			statement.setLong(1, user.getUserId());
			statement.setString(2, user.getFirstName().trim());
			statement.setString(3, user.getLastName().trim());
			statement.setString(4, user.getEmail().trim());
			
			statement.execute();
		}catch(Exception e) {
			throw e;
		}finally {
			try { close( statement, null);}catch(Exception e) {};
		}
	}

	public Long getUserIdByEmail(String email)  throws Exception {

		Connection conn =  null;
		PreparedStatement statement  = null;
		ResultSet dataRs = null;
		try {
			conn = getConnection();
			String selectSql = "select * from Users where email= ? ";
			statement  = conn.prepareStatement(selectSql);
			statement.setString(1, email);
			dataRs = statement.executeQuery();
			while(dataRs!=null && dataRs.next()){
				long userId  = dataRs.getLong("user_id");
				return userId;
			}

		}catch(Exception e) {
			throw e;
		}finally {
			try { close(statement, dataRs);}catch(Exception e) {}
		}
		return null;
	}

	/**
	 * It will return the User Object  by user Id
	 * @param userId
	 */
	public User getUser(long userId)  throws Exception {
		Connection conn =  null;
		Statement statement  = null;
		ResultSet dataRs = null;
		try {
			conn = getConnection();
			String selectSql = "select * from Users where user_id= "+userId ;
			statement  = conn.createStatement();
			dataRs = statement.executeQuery(selectSql);
			while(dataRs!=null && dataRs.next()){
				String firstName  = dataRs.getString("first_name");
				String lastName  = dataRs.getString("last_name");
				String email  = dataRs.getString("email");
				return new User(userId, firstName.trim(), lastName.trim(), email);
			}

		}catch(Exception e) {
			throw e;
		}finally {
			try { close( statement, dataRs);}catch(Exception e) {}
		}
		return null;
	}


	/**
	 * 
	 * @param user
	 * @throws Exception
	 */
	public void deleteUser(User user)  throws Exception {
		Connection conn =  null;
		PreparedStatement statement  = null;
		try {
			conn = getConnection();
			String insertSql = "delete from Users where user_id="+user.getUserId();
			statement  = conn.prepareStatement(insertSql);
			statement.execute();
		}catch(Exception e) {
			throw e;
		}finally {
			try { close( statement, null);}catch(Exception e) {};
		}
	}

	/**
	 * It will put the Train Object for user Id
	 * @param ticket
	 */
	public void addTicket(Ticket ticket)  throws Exception {

		Connection conn =  null;
		PreparedStatement statement  = null;
		try {
			conn = getConnection();
			String insertSql = "insert into Tickets values ( "+ticket.getTicketId() + ","+ticket.getUserId() + ","+ticket.getTime() +","+ticket.getSection() +","+ticket.getSeatNo()+")";
			statement  = conn.prepareStatement(insertSql);
			statement.execute();
		}catch(Exception e) {
			throw e;
		}finally {
			try { close( statement, null);}catch(Exception e) {};
		}
	}

	/**
	 * It will return the Ticket Object  by user Id
	 * @param userId
	 * @return
	 */
	public Ticket getTicketbyUserId(long userId) throws Exception {

		Connection conn =  null;
		Statement statement  = null;
		ResultSet dataRs = null;
		try {
			conn = getConnection();
			String selectSql = "select * from Tickets where user_id= "+userId ;
			statement  = conn.createStatement();
			dataRs = statement.executeQuery(selectSql);
			while(dataRs!=null && dataRs.next()){
				long ticketId  = dataRs.getLong("ticket_id");
				int seatNo  = dataRs.getInt("seat_no");
				int section  = dataRs.getInt("section");
				long time  = dataRs.getLong("time");
				Ticket ticket =  new Ticket(ticketId, userId, time);
				ticket.setSeatNo(seatNo);
				ticket.setSection(section);
				return ticket;
			}

		}catch(Exception e) {
			throw e;
		}finally {
			try { close( statement, dataRs);}catch(Exception e) {}
		}
		return null;

	}

	/**
	 * 
	 * @param ticketId
	 * @return
	 */
	public Ticket getTicket(long ticketId) throws Exception {
		Connection conn =  null;
		Statement statement  = null;
		ResultSet dataRs = null;
		try {
			conn = getConnection();
			String selectSql = "select * from Tickets where ticket_id= "+ticketId ;
			statement  = conn.createStatement();
			dataRs = statement.executeQuery(selectSql);
			while(dataRs!=null && dataRs.next()){
				long userId  = dataRs.getLong("user_id");
				int seatNo  = dataRs.getInt("seat_no");
				int section  = dataRs.getInt("section");
				long time  = dataRs.getLong("time");
				Ticket ticket =  new Ticket(ticketId, userId, time);
				ticket.setSeatNo(seatNo);
				ticket.setSection(section);
				return ticket;
			}

		}catch(Exception e) {
			throw e;
		}finally {
			try { close( statement, dataRs);}catch(Exception e) {}
		}
		return null;
	}

	/**
	 * Here we will delete user vs ticket mapping alone 
	 * @param ticket
	 */
	public void deleteTicket(Ticket ticket) throws Exception {
		Connection conn =  null;
		PreparedStatement statement  = null;
		try {
			conn = getConnection();
			String insertSql = "delete from Tickets where user_id="+ticket.getUserId();
			statement  = conn.prepareStatement(insertSql);
			statement.execute();
		}catch(Exception e) {
			throw e;
		}finally {
			try { close( statement, null);}catch(Exception e) {};
		}
	}

	/**
	 * It will add the Train Object
	 * @param train
	 */
	public void addTrain(Train train) throws Exception  {
		Connection conn =  null;
		PreparedStatement statement  = null;
		try {
			conn = getConnection();
			String insertSql = "insert into Trains values ( "+train.getTrainId() + ","+train.getNoOfSections() + ","+train.getNoOfSeatsPerSection() +")";
			statement  = conn.prepareStatement(insertSql);
			statement.execute();
		}catch(Exception e) {
			throw e;
		}finally {
			try { close( statement, null);}catch(Exception e) {};
		}
	}

	/**
	 * It will return the Train Object  by train Id
	 * @param trainId
	 * @return
	 */
	public Train getTrain(long trainId) throws Exception  {
		Connection conn =  null;
		Statement statement  = null;
		ResultSet dataRs = null;
		try {
			conn = getConnection();
			String selectSql = "select * from Trains where train_id= "+trainId ;
			statement  = conn.createStatement();
			dataRs = statement.executeQuery(selectSql);
			while(dataRs!=null && dataRs.next()){
				int noSection  = dataRs.getInt("no_of_section");
				int noSeat  = dataRs.getInt("no_of_seat");
				return new Train(trainId, noSection, noSeat);
			}

		}catch(Exception e) {
			throw e;
		}finally {
			try { close( statement, dataRs);}catch(Exception e) {}
		}
		return null;
	}

	public  Map<Integer,Long> getSeatMap(int section)  throws Exception  {
		Connection conn =  null;
		Statement statement  = null;
		ResultSet dataRs = null;
		Map<Integer,Long> map = new HashMap<Integer, Long>();
		try {
			conn = getConnection();
			String selectSql = "select * from Tickets where section = "+section ;
			statement  = conn.createStatement();
			dataRs = statement.executeQuery(selectSql);
			while(dataRs!=null && dataRs.next()){
				long ticket_id  = dataRs.getLong("ticket_id");
				int seatNo  = dataRs.getInt("seat_no");
				map.put(seatNo, ticket_id);
			}
			return map;
		}catch(Exception e) {
			throw e;
		}finally {
			try { close( statement, dataRs);}catch(Exception e) {}
		}
	}

	public Ticket modifySeat(Ticket ticket,  int newSeatNo) throws Exception{
		Connection conn =  null;
		PreparedStatement statement  = null;
		try {
			conn = getConnection();
			String insertSql = "update Tickets set seat_no= "+newSeatNo + " where ticket_id="+ticket.getTicketId();
			statement  = conn.prepareStatement(insertSql);
			statement.execute();
		}catch(Exception e) {
			throw e;
		}finally {
			try { close( statement, null);}catch(Exception e) {};
		}
		ticket.setSeatNo(newSeatNo);
		return ticket;
	}

}
