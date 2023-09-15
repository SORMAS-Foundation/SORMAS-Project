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

package de.symeda.sormas.rest.resources;

import java.util.Date;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationIndexDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.Experimental;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.rest.resources.base.EntityDtoResource;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/immunizations")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class ImmunizationResource extends EntityDtoResource<ImmunizationDto> {

	@GET
	@Path("/all/{since}")
	public List<ImmunizationDto> getAllAfter(@PathParam("since") long since) {
		return FacadeProvider.getImmunizationFacade().getAllAfter(new Date(since));
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	public List<ImmunizationDto> getAllAfter(
		@PathParam("since") long since,
		@PathParam("size") int size,
		@PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
		return FacadeProvider.getImmunizationFacade().getAllAfter(new Date(since), size, lastSynchronizedUuid);
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
	@Path("/obsolete/{since}")
	public List<String> getObsoleteUuidsSince(@PathParam("since") long since) {
		return FacadeProvider.getImmunizationFacade().getObsoleteUuidsSince(new Date(since));
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
		return FacadeProvider.getImmunizationFacade()
			.delete(uuids, new DeletionDetails(DeletionReason.OTHER_REASON, "Deleted via ReST call"))
			.stream()
			.map(ProcessedEntity::getEntityUuid)
			.collect(Collectors.toList());
	}

	@POST
	@Path("/createVaccination")
	public VaccinationDto createVaccination(
		@Valid VaccinationDto vaccination,
		@QueryParam("regionUuid") String regionUuid,
		@QueryParam("districtUuid") String districtUuid,
		@QueryParam("personUuid") String personUuid,
		@QueryParam("disease") Disease disease) {
		return FacadeProvider.getVaccinationFacade()
			.createWithImmunization(
				vaccination,
				new RegionReferenceDto(regionUuid),
				new DistrictReferenceDto(districtUuid),
				new PersonReferenceDto(personUuid),
				disease);
	}

	@POST
	@Path("/vaccinations")
	public List<VaccinationDto> getAllVaccinations(@QueryParam("personUuid") String personUuid, @QueryParam("disease") Disease disease) {
		return FacadeProvider.getVaccinationFacade().getAllVaccinations(personUuid, disease);
	}

	@GET
	@Path("/vaccination/{uuid}")
	public VaccinationDto getVaccinationByUuid(@PathParam("uuid") String uuid) {
		return FacadeProvider.getVaccinationFacade().getByUuid(uuid);
	}

	@POST
	@Path("/vaccination/push")
	public VaccinationDto postVaccination(@Valid VaccinationDto vaccination) {
		return FacadeProvider.getVaccinationFacade().save(vaccination);
	}

	/**
	 * This endpoint is used to partially update the VaccinationData.
	 * For allowing only a subset of the fields of the caseDataDto to be updated
	 * THIS METHOD IS EXPERIMENTAL!!!
	 * 
	 * @param uuid
	 * @param vaccinationDataDtoJson
	 *            - a subset of caseDataDto fields, same structure as vaccinationDataDtoJson
	 * @return - the updated caseDataDto
	 * @throws Exception
	 */
	@POST
	@Path("/vaccination/postUpdate/{uuid}")
	@Experimental
	public VaccinationDto postUpdate(@PathParam("uuid") String uuid, JsonNode vaccinationDataDtoJson) throws Exception {
		return FacadeProvider.getVaccinationFacade().postUpdate(uuid, vaccinationDataDtoJson);
	}

	@Override
	public UnaryOperator<ImmunizationDto> getSave() {
		return FacadeProvider.getImmunizationFacade()::save;
	}

	@Override
	public Response postEntityDtos(List<ImmunizationDto> immunizationDtos) {
		return super.postEntityDtos(immunizationDtos);
	}
}
