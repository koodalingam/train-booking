package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Database;
import util.BookingServiceUtil;
import util.Constants.ErrorCode;

import java.io.IOException;

import org.h2.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import dto.Ticket;
import exception.CheckedException;

/**
 * Servlet implementation class TicketsServlet
 */
@WebServlet("/api/v1/tickets")
public class TicketsController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public  TicketsController() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		JSONObject json = new JSONObject();

		try {
			String action = request.getParameter("action");

			if("viewreceipt".equals(action)) {
				String userId = request.getParameter("user_id");
				if( !StringUtils.isNumber(userId)) {
					throw new CheckedException(ErrorCode.NO_USER_FOUND);
				}
				BookingServiceUtil.validateUser(Long.valueOf(userId));
				Ticket ticket = Database.getTicketbyUserId(Long.valueOf(userId));
				if(ticket == null) {
					throw new CheckedException(ErrorCode.NO_TICKET_FOUND);
				}
				json.put("data", ticket.getJSON());
				json.put("success", true);

			}else if("viewsection".equals(action)) {
				String section = request.getParameter("section");
				JSONArray data = BookingServiceUtil.viewSeatDetailsOfSection(BookingServiceUtil.getSection(section));
				json.put("data", data);
				json.put("success", true);
			}else {
				throw new CheckedException(ErrorCode.INVALID_REQUEST);
			}
		}catch(Exception e) {
			json.put("success", false);
			if(e instanceof CheckedException) {
				CheckedException exp = (CheckedException) e;
				json.put("error", exp.getErrorCode().getJSON());
			}else {
				System.out.println("Error "+e.getMessage());
				e.printStackTrace();
				json.put("error", ErrorCode.SERVER_ERROR.getJSON());
			}
		}
		BookingServiceUtil.writeResponse(response, json);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = new JSONObject();
		try {
			Ticket ticket = BookingServiceUtil.createTicket(request);
			json.put("data", ticket.getJSON());
			json.put("success", true);
		}catch(Exception e) {
			json.put("success", false);
			if(e instanceof CheckedException) {
				CheckedException exp = (CheckedException) e;
				json.put("error", exp.getErrorCode().getJSON());
			}else {
				System.out.println("Error "+e.getMessage());
				e.printStackTrace();
				json.put("error", ErrorCode.SERVER_ERROR.getJSON());
			}
		}
		BookingServiceUtil.writeResponse(response, json);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = new JSONObject();
		try {
			String action = request.getParameter("action");
			if("modify".equals(action)) {
				String userId = request.getParameter("user_id");
				if( !StringUtils.isNumber(userId)) {
					throw new CheckedException(ErrorCode.NO_USER_FOUND);
				}
				BookingServiceUtil.validateUser(Long.valueOf(userId));

				String seatNo = request.getParameter("seat_no");
				Ticket ticket = Database.getTicketbyUserId(Long.valueOf(userId));
				if(ticket == null) {
					throw new CheckedException(ErrorCode.NO_TICKET_FOUND);
				}
				ticket = BookingServiceUtil.modifySeat(ticket, Integer.valueOf(seatNo));
				json.put("data", ticket.getJSON());
				json.put("success", true);
			}else {
				throw new CheckedException(ErrorCode.INVALID_REQUEST);
			}

		}catch(Exception e) {
			json.put("success", false);
			if(e instanceof CheckedException) {
				CheckedException exp = (CheckedException) e;
				json.put("error", exp.getErrorCode().getJSON());
			}else {
				System.out.println("Error "+e.getMessage());
				e.printStackTrace();
				json.put("error", ErrorCode.SERVER_ERROR.getJSON());
			}
		}
		BookingServiceUtil.writeResponse(response, json);

	}

}
