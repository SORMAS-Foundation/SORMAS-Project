package de.symeda.sormas.backend.importexport;

public class ImportErrorException extends Exception {
	
	private static final long serialVersionUID = -5852533615013283186L;

	public ImportErrorException(String value, String column) {
		super("Invalid value " + value + " in column " + column);
	}
	
	public ImportErrorException(String message) {
		super(message);
	}
	
}
