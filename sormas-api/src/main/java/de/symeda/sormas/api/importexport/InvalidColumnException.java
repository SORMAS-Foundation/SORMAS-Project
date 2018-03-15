package de.symeda.sormas.api.importexport;

public class InvalidColumnException extends Exception {

	private static final long serialVersionUID = -1597100047088745699L;
	
	private String columnName;
	
	public InvalidColumnException(String columnName) {
		super();
		this.columnName = columnName;
	}
	
	public String getColumnName() {
		return columnName;
	}
	
}
