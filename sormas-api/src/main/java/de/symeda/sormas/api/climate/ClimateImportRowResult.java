/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.api.climate;

import java.io.Serializable;

import de.symeda.sormas.api.audit.AuditedClass;

/**
 * What happened to one imported or pushed row.
 */
@AuditedClass
public class ClimateImportRowResult implements Serializable {

	private static final long serialVersionUID = -6370021684133452870L;

	public enum Status {

		SAVED,
		/** Valid, but not written: the day already holds a value that this row may not replace. */
		SKIPPED,
		/** Invalid, or its place is unknown. */
		REJECTED
	}

	private final Status status;
	private final String message;

	public ClimateImportRowResult(Status status, String message) {
		this.status = status;
		this.message = message;
	}

	public static ClimateImportRowResult saved() {
		return new ClimateImportRowResult(Status.SAVED, null);
	}

	public Status getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
}
