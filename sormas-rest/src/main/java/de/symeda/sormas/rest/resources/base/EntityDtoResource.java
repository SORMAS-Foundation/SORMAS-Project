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

package de.symeda.sormas.rest.resources.base;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import javax.ejb.EJB;
import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Providers;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.PostResponse;
import de.symeda.sormas.rest.TransactionWrapper;
import io.swagger.v3.oas.annotations.Operation;

public abstract class EntityDtoResource<DTO> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private TransactionWrapper transactionWrapper;

	@Context
	private Providers providers;

	protected <T> Response savePostedDtosNonAtomic(List<T> dtos, UnaryOperator<T> saveEntityDto) {
		if (dtos == null || dtos.isEmpty()) {
			return Response.status(HttpStatus.SC_OK).build();
		}

		List<PostResponse> results = new ArrayList<>(dtos.size());

		for (T dto : dtos) {
			try {
				transactionWrapper.execute(saveEntityDto, dto);
				// save a few bytes by setting only the status code
				results.add(new PostResponse(HttpStatus.SC_OK, null));
			} catch (Exception e) {
				results.add(getPostResultError(e));
			}
		}

		if (dtos.size() == 1) {
			return Response.status(results.get(0).getStatusCode()).entity(results.get(0)).build();
		} else {
			return Response.status(HttpStatus.SC_MULTI_STATUS).entity(results).build();
		}
	}

	private PostResponse getPostResultError(Exception e) {

		logger.warn("{}", e.getMessage());

		final ExceptionMapper<Exception> exceptionMapper = (ExceptionMapper<Exception>) providers.getExceptionMapper(e.getClass());
		if (exceptionMapper != null) {
			try (Response response = exceptionMapper.toResponse(e)) {
				return new PostResponse(response.getStatus(), response.getEntity());
			}
		}
		return new PostResponse(HttpStatus.SC_UNPROCESSABLE_ENTITY, "The entity could not be processed.");
	}

	@Operation(summary = "Create or update one or multiple entities.",
		description = "A uuid will be generated for any entity that doesn't have one yet. "
			+ "Posting entities without uuid multiple times will also create them multiple times.")
	@POST
	@Path("/push")
	public Response postEntityDtos(@Valid List<DTO> dtos) {
		return savePostedDtosNonAtomic(dtos, getSave());
	}

	public abstract UnaryOperator<DTO> getSave();

}
