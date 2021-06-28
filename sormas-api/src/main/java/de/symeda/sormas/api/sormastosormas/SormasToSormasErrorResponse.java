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

import java.io.Serializable;
import java.util.Map;

public class SormasToSormasErrorResponse implements Serializable {

	private static final long serialVersionUID = -469412330276433776L;

	private String message;

	private String property;

	private Object[] args;

	private Map<ValidationErrorGroup, ValidationErrors> errors;

	public SormasToSormasErrorResponse() {
	}

	public SormasToSormasErrorResponse(String message, String property, Map<ValidationErrorGroup, ValidationErrors> errors, Object[] args) {
		this.message = message;
		this.property = property;
		this.errors = errors;
		this.args = args;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public Map<ValidationErrorGroup, ValidationErrors> getErrors() {
		return errors;
	}

	public void setErrors(Map<ValidationErrorGroup, ValidationErrors> errors) {
		this.errors = errors;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}
}
