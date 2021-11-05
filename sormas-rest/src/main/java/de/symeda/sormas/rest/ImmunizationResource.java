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

package de.symeda.sormas.rest;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationIndexDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/immunizations")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed({
	"USER",
	"REST_USER" })
public class ImmunizationResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	public List<ImmunizationDto> getAllAfter(@PathParam("since") long since) {
		return FacadeProvider.getImmunizationFacade().getAllAfter(new Date(since));
	}

	@POST
	@Path("/query")
	public List<ImmunizationDto> getByUuids(List<String> uuids) {
		return FacadeProvider.getImmunizationFacade().getByUuids(uuids);
	}

	@POST
	@Path("/query/persons")
	public List<ImmunizationDto> getByPersonUuids(List<String> uuids) {
		return FacadeProvider.getImmunizationFacade().getByPersonUuids(uuids);
	}

	@POST
	@Path("/push")
	public List<PushResult> post(@Valid List<ImmunizationDto> dtos) {
		return savePushedDto(dtos, FacadeProvider.getImmunizationFacade()::save);
	}

	@GET
	@Path("/uuids")
	public List<String> getAllUuids() {
		return FacadeProvider.getImmunizationFacade().getAllUuids();
	}

	@GET
	@Path("/archived/{since}")
	public List<String> getArchivedUuidsSince(@PathParam("since") long since) {
		return FacadeProvider.getImmunizationFacade().getArchivedUuidsSince(new Date(since));
	}

	@GET
	@Path("/deleted/{since}")
	public List<String> getDeletedUuidsSince(@PathParam("since") long since) {
		return FacadeProvider.getImmunizationFacade().getDeletedUuidsSince(new Date(since));
	}

	@GET
	@Path("/{uuid}")
	public ImmunizationDto getByUuid(@PathParam("uuid") String uuid) {
		return FacadeProvider.getImmunizationFacade().getByUuid(uuid);
	}

	@POST
	@Path("/indexList")
	public Page<ImmunizationIndexDto> getIndexList(
		@RequestBody CriteriaWithSorting<ImmunizationCriteria> criteriaWithSorting,
		@QueryParam("offset") int offset,
		@QueryParam("size") int size) {
		return FacadeProvider.getImmunizationFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@POST
	@Path("/delete")
	public List<String> delete(List<String> uuids) {
		return FacadeProvider.getImmunizationFacade().deleteImmunizations(uuids);
	}

	@POST
	@Path("/createVaccination")
	public VaccinationDto createVaccination(
		@Valid VaccinationDto vaccination,
		RegionReferenceDto region,
		DistrictReferenceDto district,
		PersonReferenceDto person,
		Disease disease) {
		return FacadeProvider.getVaccinationFacade().create(vaccination, region, district, person, disease);
	}

	@POST
	@Path("/vaccinations")
	public List<VaccinationDto> getAllVaccinations(String personUuid, Disease disease) {
		return FacadeProvider.getVaccinationFacade().getAllVaccinations(personUuid, disease);
	}

}
