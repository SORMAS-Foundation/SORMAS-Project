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

package de.symeda.sormas.api.sormastosormas;

import java.util.Map;

public class SormasToSormasValidationException extends Exception {

	private final Map<String, ValidationErrors> errors;

	public SormasToSormasValidationException(Map<String, ValidationErrors> errors) {
		this.errors = errors;
	}

	public SormasToSormasValidationException(Map<String, ValidationErrors> errors, Throwable cause) {
		super("Validation error", cause);
		this.errors = errors;
	}

	public Map<String, ValidationErrors> getErrors() {
		return errors;
	}
}
