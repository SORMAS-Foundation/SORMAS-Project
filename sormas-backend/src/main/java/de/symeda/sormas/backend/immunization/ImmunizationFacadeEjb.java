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

package de.symeda.sormas.backend.immunization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationFacade;
import de.symeda.sormas.api.immunization.ImmunizationIndexDto;
import de.symeda.sormas.api.immunization.ImmunizationListEntryDto;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationReferenceDto;
import de.symeda.sormas.api.immunization.ImmunizationSimilarityCriteria;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityService;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.country.CountryService;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.facility.FacilityService;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.vaccination.VaccinationEntity;
import de.symeda.sormas.backend.vaccination.VaccinationFacadeEjb;

@Stateless(name = "ImmunizationFacade")
public class ImmunizationFacadeEjb implements ImmunizationFacade {

	@EJB
	private ImmunizationService immunizationService;
	@EJB
	private DirectoryImmunizationService directoryImmunizationService;
	@EJB
	private PersonService personService;
	@EJB
	private UserService userService;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private CaseService caseService;
	@EJB
	private CountryService countryService;
	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;
	@EJB
	private VaccinationFacadeEjb.VaccinationFacadeEjbLocal vaccinationFacade;

	public static ImmunizationReferenceDto toReferenceDto(Immunization entity) {
		if (entity == null) {
			return null;
		}
		return new ImmunizationReferenceDto(entity.getUuid(), entity.toString(), entity.getExternalId());
	}

	public static ImmunizationReferenceDto toReferenceDto(ImmunizationDto dto) {
		if (dto == null) {
			return null;
		}
		return new ImmunizationReferenceDto(dto.getUuid(), dto.toString(), dto.getExternalId());
	}

	@Override
	public ImmunizationDto getByUuid(String uuid) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefaultWithInaccessibleValuePlaceHolder(userService::hasRight);
		return convertToDto(immunizationService.getByUuid(uuid), pseudonymizer);
	}

	@Override
	public void archive(String uuid) {
		Immunization immunization = immunizationService.getByUuid(uuid);
		if (immunization != null) {
			immunization.setArchived(true);
			immunizationService.ensurePersisted(immunization);
		}
	}

	@Override
	public void dearchive(String uuid) {
		Immunization immunization = immunizationService.getByUuid(uuid);
		if (immunization != null) {
			immunization.setArchived(false);
			immunizationService.ensurePersisted(immunization);
		}
	}

	@Override
	public List<ImmunizationDto> getAllAfter(Date date) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefaultWithInaccessibleValuePlaceHolder(userService::hasRight);
		return immunizationService.getAllActiveAfter(date).stream().map(c -> convertToDto(c, pseudonymizer)).collect(Collectors.toList());
	}

	@Override
	public List<ImmunizationDto> getByUuids(List<String> uuids) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefaultWithInaccessibleValuePlaceHolder(userService::hasRight);
		return immunizationService.getByUuids(uuids).stream().map(c -> convertToDto(c, pseudonymizer)).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids() {
		return immunizationService.getAllUuids();
	}

	@Override
	public List<String> getArchivedUuidsSince(Date since) {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return immunizationService.getArchivedUuidsSince(since);
	}

	@Override
	public List<String> getDeletedUuidsSince(Date since) {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}
		return immunizationService.getDeletedUuidsSince(since);
	}

	@Override
	public boolean exists(String uuid) {
		return immunizationService.exists(uuid);
	}

	@Override
	public ImmunizationReferenceDto getReferenceByUuid(String uuid) {
		return Optional.of(uuid).map(u -> immunizationService.getByUuid(u)).map(ImmunizationFacadeEjb::toReferenceDto).orElse(null);
	}

	@Override
	public void deleteImmunization(String uuid) {
		if (!userService.hasRight(UserRight.IMMUNIZATION_DELETE)) {
			throw new UnsupportedOperationException("User " + userService.getCurrentUser().getUuid() + " is not allowed to delete immunizations");
		}

		Immunization immunization = immunizationService.getByUuid(uuid);
		immunizationService.delete(immunization);
	}

	@Override
	public boolean isArchived(String uuid) {
		return immunizationService.isArchived(uuid);
	}

	@Override
	public void archiveOrDearchiveImmunization(String uuid, boolean archive) {
		Immunization immunization = immunizationService.getByUuid(uuid);
		immunization.setArchived(archive);
		immunizationService.ensurePersisted(immunization);
	}

	@Override
	public boolean isImmunizationEditAllowed(String uuid) {
		Immunization immunization = immunizationService.getByUuid(uuid);
		return userService.hasRight(UserRight.IMMUNIZATION_EDIT) && immunizationService.inJurisdictionOrOwned(immunization);
	}

	@Override
	public List<ImmunizationDto> getSimilarImmunizations(ImmunizationSimilarityCriteria criteria) {
		return immunizationService.getSimilarImmunizations(criteria).stream().map(result -> {
			ImmunizationDto immunizationDto = new ImmunizationDto();
			immunizationDto.setUuid((String) result[0]);
			immunizationDto.setMeansOfImmunization((MeansOfImmunization) result[1]);
			immunizationDto.setImmunizationManagementStatus((ImmunizationManagementStatus) result[2]);
			immunizationDto.setImmunizationStatus((ImmunizationStatus) result[3]);
			immunizationDto.setStartDate((Date) result[4]);
			immunizationDto.setEndDate((Date) result[5]);
			immunizationDto.setRecoveryDate((Date) result[6]);
			return immunizationDto;
		}).collect(Collectors.toList());
	}

	@Override
	public ImmunizationDto save(ImmunizationDto dto) {
		Immunization existingImmunization = dto.getUuid() != null ? immunizationService.getByUuid(dto.getUuid()) : null;
		ImmunizationDto existingDto = toDto(existingImmunization);

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		restorePseudonymizedDto(dto, existingDto, existingImmunization, pseudonymizer);

		validate(dto);

		existingImmunization = fillOrBuildEntity(dto, existingImmunization);
		immunizationService.ensurePersisted(existingImmunization);

		return convertToDto(existingImmunization, pseudonymizer);
	}

	public ImmunizationDto convertToDto(Immunization source, Pseudonymizer pseudonymizer) {

		ImmunizationDto dto = toDto(source);

		pseudonymizeDto(source, dto, pseudonymizer);

		return dto;
	}

	private void pseudonymizeDto(Immunization source, ImmunizationDto dto, Pseudonymizer pseudonymizer) {
		if (dto != null) {
			boolean inJurisdiction = immunizationService.inJurisdictionOrOwned(source);
			pseudonymizer.pseudonymizeDto(ImmunizationDto.class, dto, inJurisdiction, c -> {
				User currentUser = userService.getCurrentUser();
				pseudonymizer.pseudonymizeUser(source.getReportingUser(), currentUser, dto::setReportingUser);
				pseudonymizer.pseudonymizeDto(PersonReferenceDto.class, c.getPerson(), inJurisdiction, null);
			});
		}
	}

	private void restorePseudonymizedDto(ImmunizationDto dto, ImmunizationDto existingDto, Immunization immunization, Pseudonymizer pseudonymizer) {
		if (existingDto != null) {
			final boolean inJurisdiction = immunizationService.inJurisdictionOrOwned(immunization);
			final User currentUser = userService.getCurrentUser();
			pseudonymizer.restoreUser(immunization.getReportingUser(), currentUser, dto, dto::setReportingUser);
			pseudonymizer.restorePseudonymizedValues(ImmunizationDto.class, dto, existingDto, inJurisdiction);
		}
	}

	@Override
	public void validate(ImmunizationDto immunizationDto) throws ValidationRuntimeException {

		// Check whether any required field that does not have a not null constraint in the database is empty
		if (immunizationDto.getPerson() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validPerson));
		}
	}

	@Override
	public long count(ImmunizationCriteria criteria) {
		return directoryImmunizationService.count(criteria);
	}

	@Override
	public List<ImmunizationIndexDto> getIndexList(ImmunizationCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		return directoryImmunizationService.getIndexList(criteria, first, max, sortProperties);
	}

	@Override
	public List<ImmunizationListEntryDto> getEntriesList(String personUuid, Integer first, Integer max) {
		Long personId = personFacade.getPersonIdByUuid(personUuid);
		return immunizationService.getEntriesList(personId, first, max);
	}

	public ImmunizationDto toDto(Immunization entity) {
		if (entity == null) {
			return null;
		}
		ImmunizationDto dto = new ImmunizationDto();
		DtoHelper.fillDto(dto, entity);

		dto.setDisease(entity.getDisease());
		dto.setDiseaseDetails(entity.getDiseaseDetails());
		dto.setPerson(PersonFacadeEjb.toReferenceDto(entity.getPerson()));
		dto.setReportDate(entity.getReportDate());
		dto.setReportingUser(UserFacadeEjb.toReferenceDto(entity.getReportingUser()));
		dto.setArchived(entity.isArchived());
		dto.setImmunizationStatus(entity.getImmunizationStatus());
		dto.setMeansOfImmunization(entity.getMeansOfImmunization());
		dto.setMeansOfImmunizationDetails(entity.getMeansOfImmunizationDetails());
		dto.setImmunizationManagementStatus(entity.getImmunizationManagementStatus());
		dto.setExternalId(entity.getExternalId());
		dto.setResponsibleRegion(RegionFacadeEjb.toReferenceDto(entity.getResponsibleRegion()));
		dto.setResponsibleDistrict(DistrictFacadeEjb.toReferenceDto(entity.getResponsibleDistrict()));
		dto.setResponsibleCommunity(CommunityFacadeEjb.toReferenceDto(entity.getResponsibleCommunity()));
		dto.setCountry(CountryFacadeEjb.toReferenceDto(entity.getCountry()));
		dto.setFacilityType(entity.getFacilityType());
		dto.setHealthFacility(FacilityFacadeEjb.toReferenceDto(entity.getHealthFacility()));
		dto.setHealthFacilityDetails(entity.getHealthFacilityDetails());
		dto.setStartDate(entity.getStartDate());
		dto.setEndDate(entity.getEndDate());
		dto.setNumberOfDoses(entity.getNumberOfDoses());
		dto.setPreviousInfection(entity.getPreviousInfection());
		dto.setLastInfectionDate(entity.getLastInfectionDate());
		dto.setAdditionalDetails(entity.getAdditionalDetails());
		dto.setPositiveTestResultDate(entity.getPositiveTestResultDate());
		dto.setRecoveryDate(entity.getRecoveryDate());
		dto.setValidFrom(entity.getValidFrom());
		dto.setValidUntil(entity.getValidUntil());
		dto.setRelatedCase(CaseFacadeEjb.toReferenceDto(entity.getRelatedCase()));

		List<VaccinationDto> vaccinationDtos = new ArrayList<>();
		for (VaccinationEntity vaccinationEntity : entity.getVaccinations()) {
			VaccinationDto vaccinationDto = vaccinationFacade.toDto(vaccinationEntity);
			vaccinationDtos.add(vaccinationDto);
		}
		dto.setVaccinations(vaccinationDtos);

		return dto;
	}

	private Immunization fillOrBuildEntity(@NotNull ImmunizationDto source, Immunization target) {
		target = DtoHelper.fillOrBuildEntity(source, target, Immunization::new, true);

		target.setDisease(source.getDisease());
		target.setDiseaseDetails(source.getDiseaseDetails());
		target.setPerson(personService.getByReferenceDto(source.getPerson()));
		target.setReportDate(source.getReportDate());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setArchived(source.isArchived());
		target.setImmunizationStatus(source.getImmunizationStatus());
		target.setMeansOfImmunization(source.getMeansOfImmunization());
		target.setMeansOfImmunizationDetails(source.getMeansOfImmunizationDetails());
		target.setImmunizationManagementStatus(source.getImmunizationManagementStatus());
		target.setExternalId(source.getExternalId());
		target.setResponsibleRegion(regionService.getByReferenceDto(source.getResponsibleRegion()));
		target.setResponsibleDistrict(districtService.getByReferenceDto(source.getResponsibleDistrict()));
		target.setResponsibleCommunity(communityService.getByReferenceDto(source.getResponsibleCommunity()));
		target.setCountry(countryService.getByReferenceDto(source.getCountry()));
		target.setFacilityType(source.getFacilityType());
		target.setHealthFacility(facilityService.getByReferenceDto(source.getHealthFacility()));
		target.setHealthFacilityDetails(source.getHealthFacilityDetails());
		target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());
		target.setNumberOfDoses(source.getNumberOfDoses());
		target.setPreviousInfection(source.getPreviousInfection());
		target.setLastInfectionDate(source.getLastInfectionDate());
		target.setAdditionalDetails(source.getAdditionalDetails());
		target.setPositiveTestResultDate(source.getPositiveTestResultDate());
		target.setRecoveryDate(source.getRecoveryDate());
		target.setValidFrom(source.getValidFrom());
		target.setValidUntil(source.getValidUntil());
		target.setRelatedCase(caseService.getByReferenceDto(source.getRelatedCase()));

		List<VaccinationEntity> vaccinationEntities = new ArrayList<>();
		for (VaccinationDto vaccinationDto : source.getVaccinations()) {
			VaccinationEntity vaccinationEntity = vaccinationFacade.fromDto(vaccinationDto, true);
			vaccinationEntity.setImmunization(target);
			vaccinationEntities.add(vaccinationEntity);
		}
		target.getVaccinations().clear();
		target.getVaccinations().addAll(vaccinationEntities);

		return target;
	}

	@Override
	public void updateImmunizationStatuses() {
		immunizationService.updateImmunizationStatuses();
	}

	@LocalBean
	@Stateless
	public static class ImmunizationFacadeEjbLocal extends ImmunizationFacadeEjb {

	}
}
