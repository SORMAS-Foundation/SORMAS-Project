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
package de.symeda.auditlog.api.value;

import java.util.Date;

/**
 * Formats typical Java objects and collects the attributes to be audited for the Auditlog (key-value pairs).
 * 
 * @author Stefan Kock
 */
public interface SimpleValueContainer extends ValueContainer {

	/**
	 * Saves the Boolean to audit.
	 * 
	 * @param key
	 *            Identifier for attribute of the audited entity.
	 * @param value
	 *            If {@code null}, the {@link #getNullString()} will be saved as the value.
	 */
	void put(String key, Boolean value);

	/**
	 * Saves the {@link Enum} to audit.
	 * 
	 * @param key
	 *            Identifier for attribute of the audited entity.
	 * @param value
	 *            If {@code null}, the {@link #getNullString()} will be saved as the value.
	 */
	void put(String key, Enum<?> value);

	/**
	 * Saves the {@link Number} to audit.
	 * 
	 * @param key
	 *            Identifier for attribute of the audited entity.
	 * @param value
	 *            If {@code null}, the {@link #getNullString()} will be saved as the value.
	 */
	void put(String key, Number value);

	/**
	 * Saves the {@link Date} to audit.
	 * 
	 * @param key
	 *            Identifier for attribute of the audited entity.
	 * @param value
	 *            If {@code null}, the {@link #getNullString()} will be saved as the value.
	 */
	void put(String key, Date date, String datePattern);
}
