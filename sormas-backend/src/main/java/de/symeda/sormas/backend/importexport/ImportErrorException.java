package de.symeda.sormas.backend.importexport;

public class ImportErrorException extends Exception {
	
	private static final long serialVersionUID = -5852533615013283186L;
	
	private String value;
	private String columnName;
	private String customMessage;
	
	public ImportErrorException(String customMessage) {
		super();
		this.customMessage = customMessage;
	}
	
	public ImportErrorException(String value, String columnName) {
		super();
		this.value = value;
		this.columnName = columnName;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getColumnName() {
		return columnName;
	}
	
	public String getCustomMessage() {
		return customMessage;
	}

}
