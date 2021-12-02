/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.util;

public interface ModelConstants {

	String PERSISTENCE_UNIT_NAME = "SormasPU";
	String PERSISTENCE_UNIT_NAME_AUDITLOG = "auditlogPU";
	String HINT_HIBERNATE_READ_ONLY = "org.hibernate.readOnly";
	String FUNCTION_YEAR = "year";
	String FUNCTION_MONTH = "month";
	String FUNCTION_DAY = "day";

	/**
	 * Hard limit how much parameter can be allowed in a query.<br />
	 * Typically this is an issue when an IN clause becomes too big for an SQL statement in PostgreSQL.
	 */
	int PARAMETER_LIMIT = 32_000;

	/**
	 * Data of the given Collection or Map is persisted as JSON representation in the database.
	 */
	String COLUMN_DEFINITION_JSON = "json";

	/**
	 * Hibernate takes care of transforming the given Java model to and from JSON content in the database.
	 */
	String HIBERNATE_TYPE_JSON = "json";
}
