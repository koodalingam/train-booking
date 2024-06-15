package util;

import org.json.JSONObject;

public class Constants {
	
	public static long DEFAULT_TRAIN_ID = 1L;
	public static int DEFAULT_TRAIN_SECTION = 2;
	public static int DEFAULT_TRAIN_SECTION_SEAT_COUNT = 10;

	public enum ErrorCode{
		SERVER_ERROR(500, "Technical issues at the moment"), 
		UNABLE_MODIFY_TICKET(1000, "Unable to modify the ticket for the user. Already someone occuppied this seat"),
		ALREADY_BOOKED(1001, "The given email user was already booked the ticket") ,
		NO_AVAILABLE_SEATS(1002, "No seats available to book"),
		NO_TICKET_FOUND(1003, "No ticket found for this user"),
		NO_USER_FOUND(1004, "No user found for this user Id"),
		INVALID_SECTION(1005, "Invalid Section"),
		INVALID_REQUEST(1006, "Invalid Request") ;

		private int code;
		private String message;

		private ErrorCode(int code, String message) {
			this.code = code;
			this.message = message;
		}

		public int getCode() {
			return this.code;
		}

		public String getMessage() {
			return this.message;
		}
		
		public JSONObject getJSON() {
			JSONObject json = new JSONObject();
			json.put("code", this.code);
			json.put("message", this.message);
			return json;

		}

	}
}
