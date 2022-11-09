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

package de.symeda.sormas.rest.exception;

import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.http.HttpStatus;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.ConstrainValidationHelper;
import de.symeda.sormas.api.utils.DataHelper;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

	@Override
	public Response toResponse(ConstraintViolationException e) {
		return Response.status(HttpStatus.SC_BAD_REQUEST)
			.entity(new ConstraintViolationError(I18nProperties.getString(Strings.errorConstraintViolation), getPropertyErrors(e)))
			.build();
	}

	private Map<String, String> getPropertyErrors(ConstraintViolationException e) {
		return ConstrainValidationHelper.getPropertyErrors(e.getConstraintViolations())
			.entrySet()
			.stream()
			.map(entry -> DataHelper.Pair.createPair(String.join(".", entry.getKey()), entry.getValue()))
			.collect(Collectors.toMap(DataHelper.Pair::getElement0, DataHelper.Pair::getElement1));
	}

	private static final class ConstraintViolationError {

		private final String message;
		private final Map<String, String> errors;

		public ConstraintViolationError(String message, Map<String, String> errors) {
			this.message = message;
			this.errors = errors;
		}

		public String getMessage() {
			return message;
		}

		public Map<String, String> getErrors() {
			return errors;
		}
	}
}
