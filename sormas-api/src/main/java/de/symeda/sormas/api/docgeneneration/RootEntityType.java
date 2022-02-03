/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.docgeneneration;

public enum RootEntityType {

	ROOT_CASE("case"),
	ROOT_CONTACT("contact"),
	ROOT_EVENT_PARTICIPANT("eventparticipant"),
	ROOT_PERSON("person"),
	ROOT_USER("user"),
	ROOT_SAMPLE("sample"),
	ROOT_PATHOGEN_TEST("pathogentest"),
	ROOT_EVENT("event"),
	ROOT_EVENT_ACTIONS("eventActions"),
	ROOT_EVENT_PARTICIPANTS("eventParticipants"),
	ROOT_TRAVEL_ENTRY("travelEntry"),
	ROOT_VACCINATION("vaccination");

	private final String entityName;

	RootEntityType(String entityName) {
		this.entityName = entityName;
	}

	public String getEntityName() {
		return entityName;
	}

	public static RootEntityType ofEntityName(String name) {
		for (RootEntityType type : values()) {
			if (type.entityName.equalsIgnoreCase(name)) {
				return type;
			}
		}

		return null;
	}
}
