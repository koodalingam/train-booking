package exception;

import util.Constants.ErrorCode;

public class CheckedException extends Exception {

	private static final long serialVersionUID = 1L;

	private ErrorCode errorcode;
	public CheckedException(ErrorCode errorcode) {
		this.errorcode = errorcode;
	}

	public ErrorCode getErrorCode() {
		return this.errorcode;
	}
	
	public String getMessage() {
		return this.errorcode.getMessage();
	}

	public int getCode() {
		return this.errorcode.getCode();
	}

}
