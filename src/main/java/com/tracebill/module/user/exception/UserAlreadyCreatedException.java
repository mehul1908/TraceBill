package com.tracebill.module.user.exception;

public class UserAlreadyCreatedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1167378994349548186L;
	
	public UserAlreadyCreatedException() {
		super("User is already created");
	}
	
	public UserAlreadyCreatedException(String email) {
		super("User with email id " + email+ " is already created");
	}

}
