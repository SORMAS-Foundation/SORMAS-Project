package de.symeda.sormas.backend.importexport;

/**
 * Defines how a sql table is to be exported.
 * 
 * @author Stefan Kock
 */
public class DatabaseExportConfiguration {

	private final String tableName;
	private final String joinTableName;
	private final String columnName;
	private final String joinColumnName;

	public DatabaseExportConfiguration(String tableName) {

		this(tableName, null, null, null);
	}

	public DatabaseExportConfiguration(String tableName, String joinTableName, String columnName, String joinColumnName) {

		this.tableName = tableName;
		this.joinTableName = joinTableName;
		this.columnName = columnName;
		this.joinColumnName = joinColumnName;
	}

	public String getTableName() {
		return tableName;
	}

	public boolean isUseJoinTable() {
		return joinColumnName != null;
	}

	public String getJoinTableName() {
		return joinTableName;
	}

	public String getColumnName() {
		return columnName;
	}

	public String getJoinColumnName() {
		return joinColumnName;
	}
}
