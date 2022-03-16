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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationFacade;
import de.symeda.sormas.api.immunization.ImmunizationIndexDto;
import de.symeda.sormas.api.immunization.ImmunizationListCriteria;
import de.symeda.sormas.api.immunization.ImmunizationListEntryDto;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationReferenceDto;
import de.symeda.sormas.api.immunization.ImmunizationSimilarityCriteria;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sormastosormas.ShareTreeCriteria;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestDataType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractCoreFacadeEjb;
import de.symeda.sormas.api.common.CoreEntityType;
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
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.entities.caze.SormasToSormasCaseFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.entities.contact.SormasToSormasContactFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.entities.event.SormasToSormasEventFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.ShareInfoHelper;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.ShareRequestInfo;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.vaccination.Vaccination;
import de.symeda.sormas.backend.vaccination.VaccinationFacadeEjb.VaccinationFacadeEjbLocal;

@Stateless(name = "ImmunizationFacade")
public class ImmunizationFacadeEjb
	extends AbstractCoreFacadeEjb<Immunization, ImmunizationDto, ImmunizationIndexDto, ImmunizationReferenceDto, ImmunizationService, ImmunizationCriteria>
	implements ImmunizationFacade {

	private final Logger logger = LoggerFactory.getLogger(ImmunizationFacadeEjb.class);

	@EJB
	private DirectoryImmunizationService directoryImmunizationService;
	@EJB
	private PersonService personService;
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
	private VaccinationFacadeEjbLocal vaccinationFacade;
	@EJB
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacade;
	@EJB
	private SampleFacadeEjb.SampleFacadeEjbLocal sampleFacade;
	@EJB
	private PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal pathogenTestFacade;
	@EJB
	private SormasToSormasOriginInfoFacadeEjb.SormasToSormasOriginInfoFacadeEjbLocal originInfoFacade;
	@EJB
	private SormasToSormasFacadeEjb.SormasToSormasFacadeEjbLocal sormasToSormasFacade;
	@Resource
	private ManagedScheduledExecutorService executorService;
	@EJB
	private SormasToSormasCaseFacadeEjb.SormasToSormasCaseFacadeEjbLocal sormasToSormasCaseFacade;
	@EJB
	private SormasToSormasContactFacadeEjb.SormasToSormasContactFacadeEjbLocal sormasToSormasContactFacade;
	@EJB
	private SormasToSormasEventFacadeEjb.SormasToSormasEventFacadeEjbLocal sormasToSormasEventFacadeEjbLocal;

	public ImmunizationFacadeEjb() {
	}

	@Inject
	public ImmunizationFacadeEjb(ImmunizationService service, UserService userService) {
		super(Immunization.class, ImmunizationDto.class, service, userService);
	}

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
		dto.setNumberOfDosesDetails(entity.getNumberOfDosesDetails());
		dto.setPreviousInfection(entity.getPreviousInfection());
		dto.setLastInfectionDate(entity.getLastInfectionDate());
		dto.setAdditionalDetails(entity.getAdditionalDetails());
		dto.setPositiveTestResultDate(entity.getPositiveTestResultDate());
		dto.setRecoveryDate(entity.getRecoveryDate());
		dto.setValidFrom(entity.getValidFrom());
		dto.setValidUntil(entity.getValidUntil());
		dto.setRelatedCase(CaseFacadeEjb.toReferenceDto(entity.getRelatedCase()));

		List<VaccinationDto> vaccinationDtos = new ArrayList<>();
		for (Vaccination vaccination : entity.getVaccinations()) {
			VaccinationDto vaccinationDto = vaccinationFacade.toDto(vaccination);
			vaccinationDtos.add(vaccinationDto);
		}
		dto.setVaccinations(vaccinationDtos);

		dto.setSormasToSormasOriginInfo(SormasToSormasOriginInfoFacadeEjb.toDto(entity.getSormasToSormasOriginInfo()));
		dto.setOwnershipHandedOver(entity.getSormasToSormasShares().stream().anyMatch(ShareInfoHelper::isOwnerShipHandedOver));

		return dto;
	}

	@Override
	protected CoreEntityType getCoreEntityType() {
		return CoreEntityType.IMMUNIZATION;
	}

	@Override
	protected void selectDtoFields(CriteriaQuery<ImmunizationDto> cq, Root<Immunization> root) {
	}

	@Override
	public List<String> getArchivedUuidsSince(Date since) {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return service.getArchivedUuidsSince(since);
	}

	@Override
	public List<String> getDeletedUuidsSince(Date since) {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}
		return service.getDeletedUuidsSince(since);
	}

	@Override
	public void delete(String uuid) {
		if (!userService.hasRight(UserRight.IMMUNIZATION_DELETE)) {
			throw new UnsupportedOperationException("User " + userService.getCurrentUser().getUuid() + " is not allowed to delete immunizations");
		}

		Immunization immunization = service.getByUuid(uuid);
		service.delete(immunization);
	}

	@Override
	public boolean isImmunizationEditAllowed(String uuid) {
		Immunization immunization = service.getByUuid(uuid);

		return service.isImmunizationEditAllowed(immunization);
	}

	@Override
	public List<ImmunizationDto> getSimilarImmunizations(ImmunizationSimilarityCriteria criteria) {
		return service.getSimilarImmunizations(criteria).stream().map(result -> {
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
	public ImmunizationDto save(@Valid @NotNull ImmunizationDto dto) {
		return save(dto, true, true);
	}

	public ImmunizationDto save(@Valid @NotNull ImmunizationDto dto, boolean checkChangeDate, boolean internal) {
		Immunization existingImmunization = service.getByUuid(dto.getUuid());

		if (internal && existingImmunization != null && !service.isImmunizationEditAllowed(existingImmunization)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorImmunizationNotEditable));
		}

		ImmunizationDto existingDto = toDto(existingImmunization);

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		restorePseudonymizedDto(dto, existingDto, existingImmunization, pseudonymizer);

		validate(dto);

		Immunization immunization = fillOrBuildEntity(dto, existingImmunization, checkChangeDate);

		service.updateImmunizationStatusBasedOnVaccinations(immunization);

		immunization.getVaccinations().forEach(vaccination -> {
			VaccinationDto existingVaccination = null;
			if (existingDto != null) {
				existingVaccination = existingDto.getVaccinations()
					.stream()
					.filter(vaccinationDto -> vaccination.getUuid().equals(vaccinationDto.getUuid()))
					.findAny()
					.orElse(null);
			}
			Date oldVaccinationDate = existingVaccination != null ? existingVaccination.getVaccinationDate() : null;
			vaccinationFacade.updateVaccinationStatuses(
				vaccination.getVaccinationDate(),
				oldVaccinationDate,
				immunization.getPersonId(),
				immunization.getDisease());
		});

		service.ensurePersisted(immunization);

		if (existingImmunization != null && internal && sormasToSormasFacade.isFeatureConfigured()) {
			syncSharesAsync(existingImmunization);
		}

		return convertToDto(immunization, pseudonymizer);
	}

	protected void pseudonymizeDto(Immunization source, ImmunizationDto dto, Pseudonymizer pseudonymizer) {
		if (dto != null) {
			boolean inJurisdiction = service.inJurisdictionOrOwned(source);
			pseudonymizer.pseudonymizeDto(ImmunizationDto.class, dto, inJurisdiction, c -> {
				User currentUser = userService.getCurrentUser();
				pseudonymizer.pseudonymizeUser(source.getReportingUser(), currentUser, dto::setReportingUser);
				pseudonymizer.pseudonymizeDto(PersonReferenceDto.class, c.getPerson(), inJurisdiction, null);
			});
		}
	}

	protected void restorePseudonymizedDto(ImmunizationDto dto, ImmunizationDto existingDto, Immunization entity, Pseudonymizer pseudonymizer) {
		if (existingDto != null) {
			final boolean inJurisdiction = service.inJurisdictionOrOwned(entity);
			final User currentUser = userService.getCurrentUser();
			pseudonymizer.restoreUser(entity.getReportingUser(), currentUser, dto, dto::setReportingUser);
			pseudonymizer.restorePseudonymizedValues(ImmunizationDto.class, dto, existingDto, inJurisdiction);
		}
	}

	@Override
	public void validate(ImmunizationDto immunizationDto) throws ValidationRuntimeException {
		if (DateHelper.isStartDateBeforeEndDate(immunizationDto.getStartDate(), immunizationDto.getEndDate())) {
			String validationError = String.format(
				I18nProperties.getValidationError(Validations.afterDate),
				I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, ImmunizationDto.END_DATE),
				I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, ImmunizationDto.START_DATE));
			throw new ValidationRuntimeException(validationError);
		}

		if (DateHelper.isStartDateBeforeEndDate(immunizationDto.getValidFrom(), immunizationDto.getValidUntil())) {
			String validationError = String.format(
				I18nProperties.getValidationError(Validations.afterDate),
				I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, ImmunizationDto.VALID_UNTIL),
				I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, ImmunizationDto.VALID_FROM));
			throw new ValidationRuntimeException(validationError);
		}

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
		List<ImmunizationIndexDto> resultsList = directoryImmunizationService.getIndexList(criteria, first, max, sortProperties);
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		pseudonymizer.pseudonymizeDtoCollection(ImmunizationIndexDto.class, resultsList, ImmunizationIndexDto::isInJurisdiction, null);
		return resultsList;
	}

	@Override
	public List<ImmunizationListEntryDto> getEntriesList(ImmunizationListCriteria criteria, Integer first, Integer max) {
		Long personId = personService.getIdByUuid(criteria.getPerson().getUuid());
		return service.getEntriesList(personId, criteria.getDisease(), first, max);
	}

	@Override
	public Page<ImmunizationIndexDto> getIndexPage(
		ImmunizationCriteria immunizationCriteria,
		Integer offset,
		Integer size,
		List<SortProperty> sortProperties) {
		List<ImmunizationIndexDto> immunizationIndexList = getIndexList(immunizationCriteria, offset, size, sortProperties);
		long totalElementCount = count(immunizationCriteria);
		return new Page<>(immunizationIndexList, offset, size, totalElementCount);
	}

	public List<String> deleteImmunizations(List<String> immunizationUuids) {
		if (!userService.hasRight(UserRight.IMMUNIZATION_DELETE)) {
			throw new UnsupportedOperationException("User " + userService.getCurrentUser().getUuid() + " is not allowed to delete immunizations.");
		}
		List<String> deletedImmunizationUuids = new ArrayList<>();
		List<Immunization> immunizationsToBeDeleted = service.getByUuids(immunizationUuids);
		if (immunizationsToBeDeleted != null) {
			immunizationsToBeDeleted.forEach(immunizationToBeDeleted -> {
				service.delete(immunizationToBeDeleted);
				deletedImmunizationUuids.add(immunizationToBeDeleted.getUuid());
			});
		}
		return deletedImmunizationUuids;
	}

	@Override
	public ImmunizationReferenceDto toRefDto(Immunization immunization) {
		return toReferenceDto(immunization);
	}

	@Override
	protected Immunization fillOrBuildEntity(@NotNull ImmunizationDto source, Immunization target, boolean checkChangeDate) {
		return fillOrBuildEntity(source, target, checkChangeDate, true);
	}

	protected Immunization fillOrBuildEntity(@NotNull ImmunizationDto source, Immunization target, boolean checkChangeDate, boolean copyVaccinations) {
		target = DtoHelper.fillOrBuildEntity(source, target, Immunization::new, checkChangeDate);

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
		target.setNumberOfDosesDetails(source.getNumberOfDosesDetails());
		target.setPreviousInfection(source.getPreviousInfection());
		target.setLastInfectionDate(source.getLastInfectionDate());
		target.setAdditionalDetails(source.getAdditionalDetails());
		target.setPositiveTestResultDate(source.getPositiveTestResultDate());
		target.setRecoveryDate(source.getRecoveryDate());
		target.setValidFrom(source.getValidFrom());
		target.setValidUntil(source.getValidUntil());
		target.setRelatedCase(caseService.getByReferenceDto(source.getRelatedCase()));

		if (copyVaccinations) {
			copyVaccinations(source, target, checkChangeDate);
		}

		if (source.getSormasToSormasOriginInfo() != null) {
			target.setSormasToSormasOriginInfo(originInfoFacade.fromDto(source.getSormasToSormasOriginInfo(), checkChangeDate));
		}

		return target;
	}

	public void copyVaccinations(ImmunizationDto source, Immunization target, boolean checkChangeDate) {
		List<Vaccination> vaccinationEntities = new ArrayList<>();
		for (VaccinationDto vaccinationDto : source.getVaccinations()) {
			Vaccination vaccination = vaccinationFacade.fromDto(vaccinationDto, checkChangeDate);
			vaccination.setImmunization(target);
			vaccinationEntities.add(vaccination);
		}
		target.getVaccinations().clear();
		target.getVaccinations().addAll(vaccinationEntities);
	}

	@Override
	public void updateImmunizationStatuses() {
		service.updateImmunizationStatuses();
	}

	@Override
	public boolean linkRecoveryImmunizationToSearchedCase(String specificCaseSearchValue, ImmunizationDto immunization) {

		CaseCriteria criteria = new CaseCriteria();
		criteria.setPerson(immunization.getPerson());
		criteria.setDisease(immunization.getDisease());
		criteria.setOutcome(CaseOutcome.RECOVERED);

		String foundCaseUuid = caseFacade.getUuidByUuidEpidNumberOrExternalId(specificCaseSearchValue, criteria);

		if (foundCaseUuid != null) {
			CaseDataDto caseDataDto = caseFacade.getCaseDataByUuid(foundCaseUuid);
			List<String> samples = sampleFacade.getByCaseUuids(Collections.singletonList(caseDataDto.getUuid()))
				.stream()
				.map(EntityDto::getUuid)
				.collect(Collectors.toList());
			List<PathogenTestDto> pathogenTestDto = pathogenTestFacade.getBySampleUuids(samples);
			PathogenTestDto relevantPathogenTest = pathogenTestDto.stream()
				.filter(
					pathogenTest -> pathogenTest.getTestedDisease().equals(caseDataDto.getDisease())
						&& PathogenTestResultType.POSITIVE.equals(pathogenTest.getTestResult()))
				.sorted(Comparator.comparing(PathogenTestDto::getTestDateTime))
				.findFirst()
				.orElse(null);

			immunization.setRelatedCase(new CaseReferenceDto(foundCaseUuid));
			if (relevantPathogenTest != null) {
				Date latestPositiveTestResultDate = relevantPathogenTest.getTestDateTime();

				if (latestPositiveTestResultDate != null) {
					immunization.setPositiveTestResultDate(latestPositiveTestResultDate);
				}

				Date onsetDate = caseDataDto.getSymptoms().getOnsetDate();
				if (onsetDate != null) {
					immunization.setLastInfectionDate(onsetDate);
				}

				Date outcomeDate = caseDataDto.getOutcomeDate();
				if (outcomeDate != null) {
					immunization.setRecoveryDate(outcomeDate);
				}
			}
			this.save(immunization);
			return true;
		}
		return false;
	}

	@Override
	public List<ImmunizationDto> getByPersonUuids(List<String> uuids) {
		return service.getByPersonUuids(uuids).stream().map(i -> toDto(i)).collect(Collectors.toList());
	}

	public void syncSharesAsync(Immunization immunization) {
		//sync case/contact/event this immunization was shared with
		List<DataHelper.Pair<ShareRequestDataType, ShareTreeCriteria>> syncParams = immunization.getSormasToSormasShares()
			.stream()
			.map(immunizationShare -> ShareInfoHelper.getLatestAcceptedRequest(immunizationShare.getRequests().stream()).orElse(null))
			.filter(Objects::nonNull)
			.map(ShareRequestInfo::getShares)
			.flatMap(Collection::stream)
			.map(s -> {
				if (s.getCaze() != null) {
					return new DataHelper.Pair<>(ShareRequestDataType.CASE, new ShareTreeCriteria(s.getCaze().getUuid()));
				}

				if (s.getContact() != null) {
					return new DataHelper.Pair<>(ShareRequestDataType.CONTACT, new ShareTreeCriteria(s.getContact().getUuid()));
				}

				if (s.getEvent() != null) {
					return new DataHelper.Pair<>(ShareRequestDataType.EVENT, new ShareTreeCriteria(s.getContact().getUuid()));
				}

				return null;
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toList());

		executorService.schedule(() -> {
			try {
				syncParams.forEach((p -> {
					if (p.getElement0() == ShareRequestDataType.CASE) {
						sormasToSormasCaseFacade.syncShares(p.getElement1());
					}

					if (p.getElement0() == ShareRequestDataType.CONTACT) {
						sormasToSormasContactFacade.syncShares(p.getElement1());
					}

					if (p.getElement0() == ShareRequestDataType.EVENT) {
						sormasToSormasEventFacadeEjbLocal.syncShares(p.getElement1());
					}
				}));
			} catch (Exception e) {
				logger.error("Failed to sync shares of immunization", e);
			}
		}, 5, TimeUnit.SECONDS);
	}

	public void copyImmunizationsToLeadPerson(ImmunizationDto immunizationDto, PersonDto leadPerson) {
		Immunization newImmunization = new Immunization();
		newImmunization.setUuid(DataHelper.createUuid());

		newImmunization = fillOrBuildEntity(immunizationDto, newImmunization, false, false);

		newImmunization.setPerson(personService.getByReferenceDto(leadPerson.toReference()));
		service.persist(newImmunization);

		copyVaccinations(immunizationDto, newImmunization, false);

		vaccinationFacade.copyExistingVaccinationsToNewImmunization(immunizationDto, newImmunization);
		service.ensurePersisted(newImmunization);
	}

	@LocalBean
	@Stateless
	public static class ImmunizationFacadeEjbLocal extends ImmunizationFacadeEjb {

		public ImmunizationFacadeEjbLocal() {
			super();
		}

		@Inject
		public ImmunizationFacadeEjbLocal(ImmunizationService service, UserService userService) {
			super(service, userService);
		}
	}
}
