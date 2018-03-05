package de.symeda.sormas.api.utils;

@SuppressWarnings("serial")
public class ExportErrorException extends Exception {

	public ExportErrorException() {
		super("There was an error when trying to export the database or a part of it.");
	}
	
	public ExportErrorException(String message) {
		super(message); 
	}
	
}
