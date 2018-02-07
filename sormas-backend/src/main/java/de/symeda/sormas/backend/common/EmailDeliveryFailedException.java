package de.symeda.sormas.backend.common;

@SuppressWarnings("serial")
public class EmailDeliveryFailedException extends Exception {
	
	public EmailDeliveryFailedException(String message, Throwable cause) {
		super(message, cause);
	}

}