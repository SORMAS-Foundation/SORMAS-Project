/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.rest.exception.mappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class CatchAllExceptionMapper implements ExceptionMapper<Exception> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Response toResponse(Exception exception) {
		logger.warn(
			"A specialized ExceptionMapper for {} is missing. "
				+ "If the mapper exists, make sure the exception class is declared as a javax.ejb.ApplicationException.",
			exception.getClass().getName());
		String message = exception.getLocalizedMessage();
		exception.printStackTrace();
		return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
			.entity(StringUtils.isNotBlank(message) ? message : "An exception occurred while processing the request.")
			.build();
	}
}
