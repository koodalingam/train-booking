package servlets;

import java.io.IOException;

import org.h2.util.StringUtils;
import org.json.JSONObject;

import exception.CheckedException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.BookingServiceUtil;
import util.Constants.ErrorCode;

@WebServlet("/api/v1/users")
public class UsersController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	public  UsersController() {
		super();
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		JSONObject json = new JSONObject();
		try {
			String userId = request.getParameter("user_id");
			if( !StringUtils.isNumber(userId)) {
				throw new CheckedException(ErrorCode.NO_USER_FOUND);
			}
			BookingServiceUtil.validateUser(Long.valueOf(userId));

			BookingServiceUtil.removeUserFromTrain(Long.valueOf(userId));
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

}
