package de.symeda.sormas.api.export;

import java.io.File;
import java.nio.file.Path;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;

public enum DatabaseTable {

	CASES(DatabaseTableType.SORMAS, null, "cases", "cases"),
	CASE_SYMPTOMS(DatabaseTableType.SORMAS, CASES, "case_symptoms", "symptoms"),
	HOSPITALIZATIONS(DatabaseTableType.SORMAS, CASES, "hospitalizations", "hospitalization"),
	PREVIOUSHOSPITALIZATIONS(DatabaseTableType.SORMAS, HOSPITALIZATIONS, "previous_hospitalizations", "previoushospitalization"),
	EPIDATA(DatabaseTableType.SORMAS, CASES, "epidemiological_data", "epidata"),
	EPIDATABURIALS(DatabaseTableType.SORMAS, EPIDATA, "burials", "epidataburial"),
	EPIDATAGATHERINGS(DatabaseTableType.SORMAS, EPIDATA, "gatherings", "epidatagathering"),
	EPIDATATRAVELS(DatabaseTableType.SORMAS, EPIDATA, "travels", "epidatatravel"),
	CONTACTS(DatabaseTableType.SORMAS, null, "contacts", "contact"),
	VISITS(DatabaseTableType.SORMAS, CONTACTS, "visits", "visit"),
	VISIT_SYMPTOMS(DatabaseTableType.SORMAS, VISITS, "visit_symptoms", "symptoms"),
	EVENTS(DatabaseTableType.SORMAS, null, "events", "events"),
	EVENTPARTICIPANTS(DatabaseTableType.SORMAS, EVENTS, "event_persons_involved", "eventparticipant"),
	SAMPLES(DatabaseTableType.SORMAS, null, "samples", "samples"),
	SAMPLETESTS(DatabaseTableType.SORMAS, SAMPLES, "sample_tests", "sampletest"),
	TASKS(DatabaseTableType.SORMAS, null, "tasks", "task"),
	PERSONS(DatabaseTableType.SORMAS, null, "persons", "person"),
	LOCATIONS(DatabaseTableType.SORMAS, null, "locations", "location"),
	OUTBREAKS(DatabaseTableType.SORMAS, null, "outbreaks", "outbreak"),
	REGIONS(DatabaseTableType.INFRASTRUCTURE, null, "regions", "region"),
	DISTRICTS(DatabaseTableType.INFRASTRUCTURE, null, "districts", "district"),
	COMMUNITIES(DatabaseTableType.INFRASTRUCTURE, null, "communities", "community"),
	FACILITIES(DatabaseTableType.INFRASTRUCTURE, null, "facilities", "facility");

	private final DatabaseTableType databaseTableType;
	private final DatabaseTable parentTable;
	private final String fileName;
	private final String tableName;

	private DatabaseTable(DatabaseTableType databaseTableType, DatabaseTable parentTable, String fileName, String tableName) {
		this.databaseTableType = databaseTableType;
		this.parentTable = parentTable;
		this.fileName = fileName;
		this.tableName = tableName;
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public String getExportQuery(String date, int randomNumber) {
		switch (this) {
		case CASES:
		case HOSPITALIZATIONS:
		case PREVIOUSHOSPITALIZATIONS:
		case EPIDATA:
		case EPIDATABURIALS:
		case EPIDATAGATHERINGS:
		case EPIDATATRAVELS:
		case CONTACTS:
		case VISITS:
		case EVENTS:
		case EVENTPARTICIPANTS:
		case SAMPLES:
		case SAMPLETESTS:
		case TASKS:
		case PERSONS:
		case LOCATIONS:
		case REGIONS:
		case DISTRICTS:
		case COMMUNITIES:
		case FACILITIES:
		case OUTBREAKS:
			return generateCsvExportQuery(date, randomNumber);
		case CASE_SYMPTOMS:
			return generateCsvExportJoinQuery(CASES.tableName, tableName + ".id", CASES.tableName + ".symptoms_id", date, randomNumber);
		case VISIT_SYMPTOMS:
			return generateCsvExportJoinQuery(VISITS.tableName, tableName + ".id", VISITS.tableName + ".symptoms_id", date, randomNumber);
		default:
			throw new UnsupportedOperationException("getExportQuery not implemented for database table: " + this);
		}
	}

	/**
	 * Generates the query used to create a .csv file of this table. In order to gain access to the server file system, a function
	 * that needs to be defined in the database is used. The path to save the .csv file to needs to be specified in the sormas.properties file.
	 */
	private String generateCsvExportQuery(String date, int randomNumber) {
		Path path = new File(FacadeProvider.getConfigFacade().getExportPath()).toPath();
		String name = "sormas_export_" + fileName + "_" + date + "_" + randomNumber + ".csv";
		Path filePath = path.resolve(name);
		return "SELECT export_database('" + tableName + "', '" + filePath + "');";
	}

	/**
	 * Generates the query used to create a .csv file of a this table, joined with another table. This is specifially used to only retrieve
	 * the symptoms of cases or visits to export two different tables for this data.
	 */
	private String generateCsvExportJoinQuery(String joinTableName, String columnName, String joinColumnName, String date, int randomNumber) {
		Path path = new File(FacadeProvider.getConfigFacade().getExportPath()).toPath();
		String name = "sormas_export_" + fileName + "_" + date + "_" + randomNumber + ".csv";
		Path filePath = path.resolve(name);
		return "SELECT export_database_join('" + tableName + "', '" + joinTableName + "', '" + columnName + "', '" + joinColumnName + "', '" + 
				filePath + "');";
	}
	
	public DatabaseTableType getDatabaseTableType() {
		return databaseTableType;
	}

	public DatabaseTable getParentTable() {
		return parentTable;
	}

	public String getFileName() {
		return fileName;
	}
	
	public String getTableName() {
		return tableName;
	}

}
