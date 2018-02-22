package de.symeda.sormas.backend.common;

@SuppressWarnings("serial")
public class SmsDeliveryFailedException extends Exception {

	public SmsDeliveryFailedException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
