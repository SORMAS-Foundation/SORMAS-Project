/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.adverseeventsfollowingimmunization;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventState;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AdverseEventsDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiCriteria;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiFacade;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiIndexDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiListCriteria;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiListEntryDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiReferenceDto;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.adverseeventsfollowingimmunization.entity.Aefi;
import de.symeda.sormas.backend.common.AbstractCoreFacadeEjb;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb.ImmunizationFacadeEjbLocal;
import de.symeda.sormas.backend.immunization.ImmunizationService;
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
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.RightsAllowed;
import de.symeda.sormas.backend.vaccination.Vaccination;
import de.symeda.sormas.backend.vaccination.VaccinationFacadeEjb;
import de.symeda.sormas.backend.vaccination.VaccinationFacadeEjb.VaccinationFacadeEjbLocal;
import de.symeda.sormas.backend.vaccination.VaccinationService;

@Stateless(name = "AefiFacade")
@RightsAllowed(UserRight._ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_VIEW)
public class AefiFacadeEjb extends AbstractCoreFacadeEjb<Aefi, AefiDto, AefiIndexDto, AefiReferenceDto, AefiService, AefiCriteria>
	implements AefiFacade {

	private final Logger logger = LoggerFactory.getLogger(AefiFacadeEjb.class);

	@EJB
	private ImmunizationFacadeEjbLocal immunizationFacade;
	@EJB
	private ImmunizationService immunizationService;
	@EJB
	private PersonService personService;
	@EJB
	private LocationFacadeEjbLocal locationFacade;
	@EJB
	private VaccinationFacadeEjbLocal vaccinationFacade;
	@EJB
	private VaccinationService vaccinationService;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private CountryService countryService;
	@EJB
	private AdverseEventsMapper adverseEventsMapper;

	public AefiFacadeEjb() {
	}

	@Inject
	public AefiFacadeEjb(AefiService service) {
		super(Aefi.class, AefiDto.class, service);
	}

	@Override
	@RightsAllowed({
		UserRight._ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_CREATE,
		UserRight._ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_EDIT })
	public AefiDto save(@Valid @NotNull AefiDto dto) {
		return save(dto, true, true);
	}

	@RightsAllowed({
		UserRight._ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_CREATE,
		UserRight._ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_EDIT })
	public AefiDto save(@Valid @NotNull AefiDto dto, boolean checkChangeDate, boolean internal) {
		Aefi existingAefi = service.getByUuid(dto.getUuid());

		FacadeHelper.checkCreateAndEditRights(
			existingAefi,
			userService,
			UserRight.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_CREATE,
			UserRight.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_EDIT);

		if (internal && existingAefi != null && !service.isEditAllowed(existingAefi)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorAdverseEventNotEditable));
		}

		AefiDto existingDto = toDto(existingAefi);

		Pseudonymizer<AefiDto> pseudonymizer = createPseudonymizer(existingAefi);
		restorePseudonymizedDto(dto, existingDto, existingAefi, pseudonymizer);

		validate(dto);

		Aefi aefi = fillOrBuildEntity(dto, existingAefi, checkChangeDate);

		service.ensurePersisted(aefi);

		return toPseudonymizedDto(aefi, pseudonymizer);
	}

	@Override
	public long count(AefiCriteria criteria) {
		return service.count(criteria);
	}

	@Override
	public List<AefiIndexDto> getIndexList(AefiCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		List<AefiIndexDto> resultsList = service.getIndexList(criteria, first, max, sortProperties);
		Pseudonymizer<AefiIndexDto> pseudonymizer = createGenericPlaceholderPseudonymizer();
		pseudonymizer.pseudonymizeDtoCollection(AefiIndexDto.class, resultsList, AefiIndexDto::isInJurisdiction, null);
		return resultsList;
	}

	@Override
	public List<AefiListEntryDto> getEntriesList(AefiListCriteria criteria, Integer first, Integer max) {
		Long immunizationId = immunizationService.getIdByUuid(criteria.getImmunization().getUuid());
		return service.getEntriesList(immunizationId, first, max);
	}

	@Override
	public void validate(AefiDto aefiDto) throws ValidationRuntimeException {
		if (DateHelper.isDateAfter(aefiDto.getStartDateTime(), aefiDto.getReportDate())) {
			String validationError = String.format(
				I18nProperties.getValidationError(Validations.afterDate),
				I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, AefiDto.START_DATE_TIME),
				I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, AefiDto.REPORT_DATE));
			throw new ValidationRuntimeException(validationError);
		}

		// Check whether any required field that does not have a not null constraint in the database is empty
		if (aefiDto.getImmunization() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validImmunization));
		}

		if (aefiDto.getPrimarySuspectVaccine() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.aefiWithoutPrimarySuspectVaccine));
		}

		AdverseEventsDto adverseEvents = aefiDto.getAdverseEvents();
		if (adverseEvents == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.aefiWithoutAdverseEvents));
		} else {
			if (adverseEvents.getSevereLocalReaction() == null
				&& adverseEvents.getSeizures() == null
				&& adverseEvents.getAbscess() == null
				&& adverseEvents.getSepsis() == null
				&& adverseEvents.getEncephalopathy() == null
				&& adverseEvents.getToxicShockSyndrome() == null
				&& adverseEvents.getThrombocytopenia() == null
				&& adverseEvents.getAnaphylaxis() == null
				&& adverseEvents.getFeverishFeeling() == null
				&& StringUtils.isBlank(adverseEvents.getOtherAdverseEventDetails())) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.aefiWithoutAdverseEvents));
			}

			boolean adverseEventSelected = adverseEvents.getSevereLocalReaction() == AdverseEventState.YES
				|| adverseEvents.getSeizures() == AdverseEventState.YES
				|| adverseEvents.getAbscess() == AdverseEventState.YES
				|| adverseEvents.getSepsis() == AdverseEventState.YES
				|| adverseEvents.getEncephalopathy() == AdverseEventState.YES
				|| adverseEvents.getToxicShockSyndrome() == AdverseEventState.YES
				|| adverseEvents.getThrombocytopenia() == AdverseEventState.YES
				|| adverseEvents.getAnaphylaxis() == AdverseEventState.YES
				|| adverseEvents.getFeverishFeeling() == AdverseEventState.YES
				|| !StringUtils.isBlank(adverseEvents.getOtherAdverseEventDetails());

			if (!adverseEventSelected) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.aefiWithoutAdverseEvents));
			}
		}

		if (aefiDto.getReportingUser() == null && !aefiDto.isPseudonymized()) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validReportingUser));
		}
	}

	@Override
	public List<String> getArchivedUuidsSince(Date since) {
		return null;
	}

	@Override
	public List<ProcessedEntity> delete(List<String> uuids, DeletionDetails deletionDetails) {
		return null;
	}

	@Override
	public List<ProcessedEntity> restore(List<String> uuids) {
		return null;
	}

	@Override
	protected Aefi fillOrBuildEntity(AefiDto source, Aefi target, boolean checkChangeDate) {
		return fillOrBuildEntity(source, target, checkChangeDate, false);
	}

	protected Aefi fillOrBuildEntity(@NotNull AefiDto source, Aefi target, boolean checkChangeDate, boolean includeVaccinations) {

		target = DtoHelper.fillOrBuildEntity(source, target, Aefi::build, checkChangeDate);

		target.setImmunization(immunizationService.getByReferenceDto(source.getImmunization()));
		target.setPerson(personService.getByReferenceDto(source.getPerson()));
		target.setAddress(locationFacade.fillOrBuildEntity(source.getAddress(), target.getAddress(), checkChangeDate));

		if (includeVaccinations) {
			List<Vaccination> vaccinationEntities = new ArrayList<>();
			for (VaccinationDto vaccinationDto : source.getVaccinations()) {
				Vaccination vaccination = vaccinationService.getByUuid(vaccinationDto.getUuid());
				vaccination = vaccinationFacade.fillOrBuildEntity(vaccinationDto, vaccination, checkChangeDate);
				vaccinationEntities.add(vaccination);
			}
			target.getVaccinations().clear();
			target.getVaccinations().addAll(vaccinationEntities);
		}

		target.setPrimarySuspectVaccine(vaccinationService.getByUuid(source.getPrimarySuspectVaccine().getUuid()));
		target.setAdverseEvents(adverseEventsMapper.fillOrBuildEntity(source.getAdverseEvents(), target.getAdverseEvents(), checkChangeDate));
		target.setReportDate(source.getReportDate());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setExternalId(source.getExternalId());
		target.setResponsibleRegion(regionService.getByReferenceDto(source.getResponsibleRegion()));
		target.setResponsibleDistrict(districtService.getByReferenceDto(source.getResponsibleDistrict()));
		target.setResponsibleCommunity(communityService.getByReferenceDto(source.getResponsibleCommunity()));
		target.setCountry(countryService.getByReferenceDto(source.getCountry()));
		target.setReportingIdNumber(source.getReportingIdNumber());
		target.setPhoneNumber(source.getPhoneNumber());
		target.setPregnant(source.getPregnant());
		target.setTrimester(source.getTrimester());
		target.setLactating(source.getLactating());
		target.setOnsetAgeYears(source.getOnsetAgeYears());
		target.setOnsetAgeMonths(source.getOnsetAgeMonths());
		target.setOnsetAgeDays(source.getOnsetAgeDays());
		target.setAgeGroup(source.getAgeGroup());
		target.setHealthFacility(facilityService.getByReferenceDto(source.getHealthFacility()));
		target.setHealthFacilityDetails(source.getHealthFacilityDetails());
		target.setReporterName(source.getReporterName());
		target.setReporterInstitution(facilityService.getByReferenceDto(source.getReporterInstitution()));
		target.setReporterDesignation(source.getReporterDesignation());
		target.setReporterDepartment(source.getReporterDepartment());
		target.setReporterAddress(locationFacade.fillOrBuildEntity(source.getReporterAddress(), target.getReporterAddress(), checkChangeDate));
		target.setReporterPhone(source.getReporterPhone());
		target.setReporterEmail(source.getReporterEmail());
		target.setTodaysDate(source.getTodaysDate());
		target.setStartDateTime(source.getStartDateTime());
		target.setAefiDescription(source.getAefiDescription());
		target.setSerious(source.getSerious());
		target.setSeriousReason(source.getSeriousReason());
		target.setSeriousReasonDetails(source.getSeriousReasonDetails());
		target.setOutcome(source.getOutcome());
		target.setDeathDate(source.getDeathDate());
		target.setAutopsyDone(source.getAutopsyDone());
		target.setPastMedicalHistory(source.getPastMedicalHistory());
		target.setInvestigationNeeded(source.getInvestigationNeeded());
		target.setInvestigationPlannedDate(source.getInvestigationPlannedDate());
		target.setReceivedAtNationalLevelDate(source.getReceivedAtNationalLevelDate());
		target.setWorldwideId(source.getWorldwideId());
		target.setNationalLevelComment(source.getNationalLevelComment());
		target.setArchived(source.isArchived());
		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

		return target;
	}

	@Override
	protected AefiDto toDto(Aefi entity) {
		return toAefiDto(entity);
	}

	public static AefiDto toAefiDto(Aefi entity) {

		if (entity == null) {
			return null;
		}
		AefiDto dto = new AefiDto();
		DtoHelper.fillDto(dto, entity);

		dto.setImmunization(ImmunizationFacadeEjb.toReferenceDto(entity.getImmunization()));
		dto.setPerson(PersonFacadeEjb.toReferenceDto(entity.getPerson()));
		dto.setAddress(LocationFacadeEjb.toDto(entity.getAddress()));

		List<VaccinationDto> vaccinationDtos = new ArrayList<>();
		for (Vaccination vaccination : entity.getImmunization().getVaccinations()) {
			VaccinationDto vaccinationDto = VaccinationFacadeEjb.toVaccinationDto(vaccination);
			vaccinationDtos.add(vaccinationDto);
		}
		dto.setVaccinations(vaccinationDtos);

		dto.setPrimarySuspectVaccine(VaccinationFacadeEjb.toVaccinationDto(entity.getPrimarySuspectVaccine()));
		dto.setAdverseEvents(AdverseEventsMapper.toDto(entity.getAdverseEvents()));
		dto.setReportDate(entity.getReportDate());
		dto.setReportingUser(UserFacadeEjb.toReferenceDto(entity.getReportingUser()));
		dto.setExternalId(entity.getExternalId());
		dto.setResponsibleRegion(RegionFacadeEjb.toReferenceDto(entity.getResponsibleRegion()));
		dto.setResponsibleDistrict(DistrictFacadeEjb.toReferenceDto(entity.getResponsibleDistrict()));
		dto.setResponsibleCommunity(CommunityFacadeEjb.toReferenceDto(entity.getResponsibleCommunity()));
		dto.setCountry(CountryFacadeEjb.toReferenceDto(entity.getCountry()));
		dto.setReportingIdNumber(entity.getReportingIdNumber());
		dto.setPhoneNumber(entity.getPhoneNumber());
		dto.setPregnant(entity.getPregnant());
		dto.setTrimester(entity.getTrimester());
		dto.setLactating(entity.getLactating());
		dto.setOnsetAgeYears(entity.getOnsetAgeYears());
		dto.setOnsetAgeMonths(entity.getOnsetAgeMonths());
		dto.setOnsetAgeDays(entity.getOnsetAgeDays());
		dto.setAgeGroup(entity.getAgeGroup());
		dto.setHealthFacility(FacilityFacadeEjb.toReferenceDto(entity.getHealthFacility()));
		dto.setHealthFacilityDetails(entity.getHealthFacilityDetails());
		dto.setReporterName(entity.getReporterName());
		dto.setReporterInstitution(FacilityFacadeEjb.toReferenceDto(entity.getReporterInstitution()));
		dto.setReporterDesignation(entity.getReporterDesignation());
		dto.setReporterDepartment(entity.getReporterDepartment());
		dto.setReporterAddress(LocationFacadeEjb.toDto(entity.getReporterAddress()));
		dto.setReporterPhone(entity.getReporterPhone());
		dto.setReporterEmail(entity.getReporterEmail());
		dto.setTodaysDate(entity.getTodaysDate());
		dto.setStartDateTime(entity.getStartDateTime());
		dto.setAefiDescription(entity.getAefiDescription());
		dto.setSerious(entity.getSerious());
		dto.setSeriousReason(entity.getSeriousReason());
		dto.setSeriousReasonDetails(entity.getSeriousReasonDetails());
		dto.setOutcome(entity.getOutcome());
		dto.setDeathDate(entity.getDeathDate());
		dto.setAutopsyDone(entity.getAutopsyDone());
		dto.setPastMedicalHistory(entity.getPastMedicalHistory());
		dto.setInvestigationNeeded(entity.getInvestigationNeeded());
		dto.setInvestigationPlannedDate(entity.getInvestigationPlannedDate());
		dto.setReceivedAtNationalLevelDate(entity.getReceivedAtNationalLevelDate());
		dto.setWorldwideId(entity.getWorldwideId());
		dto.setNationalLevelComment(entity.getNationalLevelComment());
		dto.setArchived(entity.isArchived());
		dto.setDeleted(entity.isDeleted());
		dto.setDeletionReason(entity.getDeletionReason());
		dto.setOtherDeletionReason(entity.getOtherDeletionReason());

		return dto;
	}

	@Override
	protected AefiReferenceDto toRefDto(Aefi aefi) {
		return toReferenceDto(aefi);
	}

	public static AefiReferenceDto toReferenceDto(Aefi entity) {

		if (entity == null) {
			return null;
		}

		return new AefiReferenceDto(entity.getUuid(), "", "");
	}

	@Override
	protected void pseudonymizeDto(Aefi source, AefiDto dto, Pseudonymizer<AefiDto> pseudonymizer, boolean inJurisdiction) {

		if (dto != null) {
			pseudonymizer.pseudonymizeDto(AefiDto.class, dto, inJurisdiction, c -> {
				pseudonymizer.pseudonymizeUser(source.getReportingUser(), userService.getCurrentUser(), dto::setReportingUser, dto);
			});
		}
	}

	@Override
	protected void restorePseudonymizedDto(AefiDto dto, AefiDto existingDto, Aefi entity, Pseudonymizer<AefiDto> pseudonymizer) {

		if (existingDto != null) {
			final boolean inJurisdiction = service.inJurisdictionOrOwned(entity);
			final User currentUser = userService.getCurrentUser();
			pseudonymizer.restoreUser(entity.getReportingUser(), currentUser, dto, dto::setReportingUser);
			pseudonymizer.restorePseudonymizedValues(AefiDto.class, dto, existingDto, inJurisdiction);
		}
	}

	@Override
	protected DeletableEntityType getDeletableEntityType() {
		return null;
	}

	@LocalBean
	@Stateless
	public static class AefiFacadeEjbLocal extends AefiFacadeEjb {

		public AefiFacadeEjbLocal() {
			super();
		}

		@Inject
		public AefiFacadeEjbLocal(AefiService service) {
			super(service);
		}
	}
}
