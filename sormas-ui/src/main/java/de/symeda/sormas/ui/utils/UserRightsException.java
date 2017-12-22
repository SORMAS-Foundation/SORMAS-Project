package de.symeda.sormas.ui.utils;

@SuppressWarnings("serial")
public class UserRightsException extends Exception {

	public UserRightsException() {
		super("You do not have the required rights do perform this action.");
	}
	
	public UserRightsException(String message) {
		super(message); 
	}
	
}
