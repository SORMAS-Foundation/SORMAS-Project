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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.rest;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.user.UserReferenceDto;

@Path("/cases")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@RolesAllowed("USER")
public class CaseResource {

	@GET
	@Path("/all/{since}")
	public List<CaseDataDto> getAllCases(@Context SecurityContext sc, @PathParam("since") long since) {
		
		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<CaseDataDto> cases = FacadeProvider.getCaseFacade().getAllActiveCasesAfter(new Date(since), userDto.getUuid());
		return cases;
	}

	@POST
	@Path("/query")
	public List<CaseDataDto> getByUuids(@Context SecurityContext sc, List<String> uuids) {

		List<CaseDataDto> result = FacadeProvider.getCaseFacade().getByUuids(uuids); 
		return result;
	}

	@POST 
	@Path("/push")
	public Integer postCases(List<CaseDataDto> dtos) {
		
		CaseFacade caseFacade = FacadeProvider.getCaseFacade();
		for (CaseDataDto dto : dtos) {
			caseFacade.saveCase(dto);
		}
		
		return dtos.size();
	}
	
	@GET
	@Path("/uuids")
	public List<String> getAllUuids(@Context SecurityContext sc) {
		
		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<String> uuids = FacadeProvider.getCaseFacade().getAllActiveUuids(userDto.getUuid());
		return uuids;
	}
	
	@GET
	@Path("/archived/{since}")
	public List<String> getArchivedUuidsSince(@Context SecurityContext sc, @PathParam("since") long since) {

		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<String> uuids = FacadeProvider.getCaseFacade().getArchivedUuidsSince(userDto.getUuid(), new Date(since));
		return uuids;
	}
}
