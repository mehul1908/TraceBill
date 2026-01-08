package com.tracebill.module.auth.exception;

public class UserIdAndPasswordNotMatchException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3819445684606873905L;

	public UserIdAndPasswordNotMatchException() {
		super("User Id and Password does not match");
	}
	
}
