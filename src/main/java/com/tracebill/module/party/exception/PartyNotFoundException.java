package com.tracebill.module.party.exception;

public class PartyNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4697879734659812241L;

	
	public PartyNotFoundException() {
		super("Party not found");
	}
	
	public PartyNotFoundException(String email) {
		super("Party with given email id " + email +" is not found");
	}

	public PartyNotFoundException(Long partyId) {
		super("Party with given email id " + partyId +" is not found");
	}
}
