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

package de.symeda.sormas.rest;

import java.io.OutputStream;
import java.util.Date;
import java.util.function.Consumer;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.bagexport.BAGExportCaseDto;
import de.symeda.sormas.api.bagexport.BAGExportContactDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.CsvStreamUtils;
import de.symeda.sormas.api.utils.DateHelper;

@Path("/bagexport")
@Produces(MediaType.APPLICATION_OCTET_STREAM)
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed({
	UserRole._BAG_USER })
public class BAGExportResource {

	@GET
	@Path("/cases")
	public Response exportCases(String file) {
		return createFileDownloadResponse(
			output -> CsvStreamUtils.writeCsvContentToStream(
				BAGExportCaseDto.class,
				(from, to) -> FacadeProvider.getBAGExportFacade().getCaseExportList(from, to),
				(propertyId, type) -> propertyId,
				null,
				null,
				FacadeProvider.getConfigFacade(),
				output),
			"sormas_BAG_cases_",
			"csv");
	}

	@GET
	@Path("/contacts")
	public Response exportContacts(String file) {
		return createFileDownloadResponse(
			output -> CsvStreamUtils.writeCsvContentToStream(
				BAGExportContactDto.class,
				(from, to) -> FacadeProvider.getBAGExportFacade().getContactExportList(from, to),
				(propertyId, type) -> propertyId,
				null,
				null,
				FacadeProvider.getConfigFacade(),
				output),
			"sormas_BAG_contacts_",
			"csv");
	}

	private Response createFileDownloadResponse(Consumer<OutputStream> writeContent, String fileNamePrefix, String extension) {
		StreamingOutput fileStream = writeContent::accept;

		Response.ResponseBuilder response = Response.ok(fileStream);
		response.header("Content-Disposition", "attachment;filename=" + createFileNameWithCurrentDate(fileNamePrefix, extension));

		return response.build();
	}

	private String createFileNameWithCurrentDate(String fileNamePrefix, String extension) {
		return fileNamePrefix + DateHelper.formatDateForExport(new Date()) + "." + extension;
	}
}
