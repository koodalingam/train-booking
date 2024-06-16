package dto;

import org.json.JSONException;
import org.json.JSONObject;

import model.Database;
import util.BookingServiceUtil;
import util.Constants;

public class Ticket {
	private long ticketId;
	private long time;
	private long userId;
	private long trainId = Constants.DEFAULT_TRAIN_ID;
	private String fromLocation = "London";
	private String toLocation = "France";
	private int section;
	private int seatNo = 0;
	private float price = 5.0f;

	public Ticket(long ticketId, long userId, long time  ) {
		this.ticketId = ticketId;
		this.userId = userId;
		this.time = time;	
	}
	
	public long getTicketId() {
		return ticketId;
	}
	public void setTicketId(long ticketId) {
		this.ticketId = ticketId;
	}
	
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getTrainId() {
		return trainId;
	}

	public void setTrainId(long trainId) {
		this.trainId = trainId;
	}

	public String getFromLocation() {
		return fromLocation;
	}

	public void setFromLocation(String fromLocation) {
		this.fromLocation = fromLocation;
	}

	public String getToLocation() {
		return toLocation;
	}

	public void setToLocation(String toLocation) {
		this.toLocation = toLocation;
	}
	
	public int getSection() {
		return section;
	}

	public void setSection(int section) {
		this.section = section;
	}

	public int getSeatNo() {
		return seatNo;
	}

	public void setSeatNo(int seatNo) {
		this.seatNo = seatNo;
	}
	public float getPrice() {
		return price;
	}
	
	public void setPrice(float price) {
		this.price = price;
	}
	
	public JSONObject getJSON() throws JSONException, Exception {
		JSONObject json = new JSONObject();
		json.put("ticket_id", String.valueOf( this.ticketId)) ;
		json.put("user_id",  String.valueOf( this.userId));
		json.put("user_name", Database.getUser(this.userId).getDisplayName());

		json.put("from", this.fromLocation);
		json.put("to", this.toLocation);
		json.put("amount_paid",  String.valueOf( this. price));
		json.put("section", BookingServiceUtil.getSectionDisplayName(this.section));
		json.put("seat_no",  String.valueOf(this.seatNo));
		json.put("booked_time",  String.valueOf(this.time));
		
		return json;
	}
	
}
