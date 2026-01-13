package com.tracebill.module.inventory.exception;

public class InsufficientStockException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 541632905904064999L;

	public InsufficientStockException(String message) {
		super(message);
	}
	
}
