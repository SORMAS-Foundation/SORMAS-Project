package de.symeda.sormas.api.export;

import de.symeda.sormas.api.I18nProperties;

public enum DatabaseTable {

	CASES(DatabaseTableType.SORMAS, null, "cases"),
	CASE_SYMPTOMS(DatabaseTableType.SORMAS, CASES, "case_symptoms"),
	HOSPITALIZATIONS(DatabaseTableType.SORMAS, CASES, "hospitalizations"),
	PREVIOUSHOSPITALIZATIONS(DatabaseTableType.SORMAS, HOSPITALIZATIONS, "previous_hospitalizations"),
	EPIDATA(DatabaseTableType.SORMAS, CASES, "epidemiological_data"),
	EPIDATABURIALS(DatabaseTableType.SORMAS, EPIDATA, "burials"),
	EPIDATAGATHERINGS(DatabaseTableType.SORMAS, EPIDATA, "gatherings"),
	EPIDATATRAVELS(DatabaseTableType.SORMAS, EPIDATA, "travels"),
	CONTACTS(DatabaseTableType.SORMAS, null, "contacts"),
	VISITS(DatabaseTableType.SORMAS, CONTACTS, "visits"),
	VISIT_SYMPTOMS(DatabaseTableType.SORMAS, VISITS, "visit_symptoms"),
	EVENTS(DatabaseTableType.SORMAS, null, "events"),
	EVENTPARTICIPANTS(DatabaseTableType.SORMAS, EVENTS, "event_persons_involved"),
	SAMPLES(DatabaseTableType.SORMAS, null, "samples"),
	SAMPLETESTS(DatabaseTableType.SORMAS, SAMPLES, "sample_tests"),
	TASKS(DatabaseTableType.SORMAS, null, "tasks"),
	PERSONS(DatabaseTableType.SORMAS, null, "persons"),
	LOCATIONS(DatabaseTableType.SORMAS, null, "locations"),
	OUTBREAKS(DatabaseTableType.SORMAS, null, "outbreaks"),
	REGIONS(DatabaseTableType.INFRASTRUCTURE, null, "regions"),
	DISTRICTS(DatabaseTableType.INFRASTRUCTURE, null, "districts"),
	COMMUNITIES(DatabaseTableType.INFRASTRUCTURE, null, "communities"),
	FACILITIES(DatabaseTableType.INFRASTRUCTURE, null, "facilities");

	private final DatabaseTableType databaseTableType;
	private final DatabaseTable parentTable;
	private final String fileName;

	private DatabaseTable(DatabaseTableType databaseTableType, DatabaseTable parentTable, String fileName) {
		this.databaseTableType = databaseTableType;
		this.parentTable = parentTable;
		this.fileName = fileName;
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
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

}
