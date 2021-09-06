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
package de.symeda.sormas.api.utils;

import java.util.List;
import java.util.Map;

import javax.ejb.ApplicationException;

/*
 * ATTENTION: Does not do a rollback when thrown because this class is used in
 * case import where no rollback may be done (in order to continue with the import
 * when validation of a single case fails).
 * Make sure to call this before changing backend data (e.g. when using it to
 * validate transfered cases).
 */
@SuppressWarnings("serial")
@ApplicationException(rollback = false)
public class ValidationRuntimeException extends RuntimeException {

	private Map<List<String>, String> propertyErrors;

	public ValidationRuntimeException(String message) {
		super(message);
	}

	public ValidationRuntimeException(Map<List<String>, String> propertyErrors) {
		this.propertyErrors = propertyErrors;
	}

	public ValidationRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public Map<List<String>, String> getPropertyErrors() {
		return propertyErrors;
	}
}
