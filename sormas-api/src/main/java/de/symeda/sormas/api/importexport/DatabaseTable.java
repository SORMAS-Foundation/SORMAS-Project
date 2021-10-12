/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.importexport;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum DatabaseTable {

	CASES(DatabaseTableType.SORMAS, null, "cases"),
	CASE_SYMPTOMS(DatabaseTableType.SORMAS, CASES, "case_symptoms"),
	HOSPITALIZATIONS(DatabaseTableType.SORMAS, CASES, "hospitalizations"),
	PREVIOUSHOSPITALIZATIONS(DatabaseTableType.SORMAS, HOSPITALIZATIONS, "previous_hospitalizations"),
	EPIDATA(DatabaseTableType.SORMAS, CASES, "epidemiological_data"),
	EXPOSURES(DatabaseTableType.SORMAS, EPIDATA, "exposures"),
	THERAPIES(DatabaseTableType.SORMAS, CASES, "therapies"),
	PRESCRIPTIONS(DatabaseTableType.SORMAS, THERAPIES, "prescriptions"),
	TREATMENTS(DatabaseTableType.SORMAS, THERAPIES, "treatments"),
	CLINICAL_COURSES(DatabaseTableType.SORMAS, CASES, "clinical_courses"),
	HEALTH_CONDITIONS(DatabaseTableType.SORMAS, CLINICAL_COURSES, "health_conditions"),
	CLINICAL_VISITS(DatabaseTableType.SORMAS, CLINICAL_COURSES, "clinical_visits"),
	CLINICAL_VISIT_SYMPTOMS(DatabaseTableType.SORMAS, CLINICAL_VISITS, "clinical_visit_symptoms"),
	CONTACTS(DatabaseTableType.SORMAS, null, "contacts"),
	VISITS(DatabaseTableType.SORMAS, CONTACTS, "visits"),
	VISIT_SYMPTOMS(DatabaseTableType.SORMAS, VISITS, "visit_symptoms"),
	EVENTS(DatabaseTableType.SORMAS, null, "events"),
	EVENTGROUPS(DatabaseTableType.SORMAS, EVENTS, "eventgroups"),
	EVENTPARTICIPANTS(DatabaseTableType.SORMAS, EVENTS, "event_persons_involved"),
	ACTIONS(DatabaseTableType.SORMAS, EVENTS, "actions"),
	TRAVEL_ENTRIES(DatabaseTableType.SORMAS, null, "travel_entries"),
	IMMUNIZATIONS(DatabaseTableType.SORMAS, null, "immunizations"),
	VACCINATIONS(DatabaseTableType.SORMAS, IMMUNIZATIONS, "vaccinations"),
	SAMPLES(DatabaseTableType.SORMAS, null, "samples"),
	SAMPLETESTS(DatabaseTableType.SORMAS, SAMPLES, "sample_tests"),
	TASKS(DatabaseTableType.SORMAS, null, "tasks"),
	PERSONS(DatabaseTableType.SORMAS, null, "persons"),
	PERSON_CONTACT_DETAILS(DatabaseTableType.SORMAS, PERSONS, "person_contact_details"),
	LOCATIONS(DatabaseTableType.SORMAS, null, "locations"),
	OUTBREAKS(DatabaseTableType.SORMAS, null, "outbreaks"),
	COUNTRIES(DatabaseTableType.INFRASTRUCTURE, null, "countries"),
	REGIONS(DatabaseTableType.INFRASTRUCTURE, null, "regions"),
	DISTRICTS(DatabaseTableType.INFRASTRUCTURE, null, "districts"),
	COMMUNITIES(DatabaseTableType.INFRASTRUCTURE, null, "communities"),
	FACILITIES(DatabaseTableType.INFRASTRUCTURE, null, "facilities"),
	CUSTOMIZABLE_ENUM_VALUES(DatabaseTableType.CONFIGURATION, null, "customizable_enum_values"),

	USERS(DatabaseTableType.SORMAS, null, "users"),
	USER_ROLES(DatabaseTableType.SORMAS, USERS, "user_roles");

	private final DatabaseTableType databaseTableType;
	private final DatabaseTable parentTable;
	private final String fileName;

	DatabaseTable(DatabaseTableType databaseTableType, DatabaseTable parentTable, String fileName) {

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
