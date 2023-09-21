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

import java.sql.Timestamp;
import java.time.LocalDate;
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
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
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
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.common.progress.ProcessedEntityStatus;
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
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestDataType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractCoreFacadeEjb;
import de.symeda.sormas.backend.event.EventParticipant;
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
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfo;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoService;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareInfoHelper;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfo;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.RightsAllowed;
import de.symeda.sormas.backend.vaccination.Vaccination;
import de.symeda.sormas.backend.vaccination.VaccinationFacadeEjb.VaccinationFacadeEjbLocal;
import de.symeda.sormas.backend.vaccination.VaccinationService;

@Stateless(name = "ImmunizationFacade")
@RightsAllowed(UserRight._IMMUNIZATION_VIEW)
public class ImmunizationFacadeEjb
	extends
	AbstractCoreFacadeEjb<Immunization, ImmunizationDto, ImmunizationIndexDto, ImmunizationReferenceDto, ImmunizationService, ImmunizationCriteria>
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
	private SormasToSormasOriginInfoService originInfoService;
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
	@EJB
	private VaccinationService vaccinationService;

	public ImmunizationFacadeEjb() {
	}

	@Inject
	public ImmunizationFacadeEjb(ImmunizationService service) {
		super(Immunization.class, ImmunizationDto.class, service);
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
		return new ImmunizationReferenceDto(dto.getUuid(), dto.buildCaption(), dto.getExternalId());
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

		dto.setDeleted(entity.isDeleted());
		dto.setDeletionReason(entity.getDeletionReason());
		dto.setOtherDeletionReason(entity.getOtherDeletionReason());

		return dto;
	}

	@Override
	protected DeletableEntityType getDeletableEntityType() {
		return DeletableEntityType.IMMUNIZATION;
	}

	@Override
	public List<String> getArchivedUuidsSince(Date since) {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return service.getArchivedUuidsSince(since);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@RightsAllowed(UserRight._SYSTEM)
	public void archiveAllArchivableImmunizations(int daysAfterImmunizationsGetsArchived) {
		archiveAllArchivableImmunizations(daysAfterImmunizationsGetsArchived, LocalDate.now());
	}

	private void archiveAllArchivableImmunizations(int daysAfterImmunizationGetsArchived, @NotNull LocalDate referenceDate) {
		LocalDate notChangedSince = referenceDate.minusDays(daysAfterImmunizationGetsArchived);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Immunization> from = cq.from(Immunization.class);

		Timestamp notChangedTimestamp = Timestamp.valueOf(notChangedSince.atStartOfDay());
		cq.where(
			cb.equal(from.get(Immunization.ARCHIVED), false),
			cb.equal(from.get(Immunization.DELETED), false),
			cb.not(service.createChangeDateFilter(cb, from, notChangedTimestamp)));
		cq.select(from.get(Immunization.UUID)).distinct(true);
		List<String> immunizationUuids = em.createQuery(cq).getResultList();

		if (!immunizationUuids.isEmpty()) {
			archive(immunizationUuids);
		}
	}

	@Override
	public List<String> getDeletedUuidsSince(Date since) {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}
		return service.getDeletedUuidsSince(since);
	}

	@Override
	@RightsAllowed(UserRight._IMMUNIZATION_DELETE)
	public void delete(String uuid, DeletionDetails deletionDetails) {
		Immunization immunization = service.getByUuid(uuid);

		if (!service.inJurisdictionOrOwned(immunization)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.messageImmunizationOutsideJurisdictionDeletionDenied));
		}

		service.delete(immunization, deletionDetails);
	}

	@Override
	@RightsAllowed(UserRight._IMMUNIZATION_DELETE)
	public List<ProcessedEntity> delete(List<String> uuids, DeletionDetails deletionDetails) {
		List<ProcessedEntity> processedImmunizations = new ArrayList<>();
		List<Immunization> immunizationsToBeDeleted = service.getByUuids(uuids);

		if (immunizationsToBeDeleted != null) {
			immunizationsToBeDeleted.forEach(immunizationToBeDeleted -> {
				try {
					delete(immunizationToBeDeleted.getUuid(), deletionDetails);
					processedImmunizations.add(new ProcessedEntity(immunizationToBeDeleted.getUuid(), ProcessedEntityStatus.SUCCESS));
				} catch (AccessDeniedException e) {
					processedImmunizations.add(new ProcessedEntity(immunizationToBeDeleted.getUuid(), ProcessedEntityStatus.ACCESS_DENIED_FAILURE));
					logger.error(
						"The immunization with uuid {} could not be deleted due to a AccessDeniedException",
						immunizationToBeDeleted.getUuid(),
						e);
				} catch (Exception e) {
					processedImmunizations.add(new ProcessedEntity(immunizationToBeDeleted.getUuid(), ProcessedEntityStatus.INTERNAL_FAILURE));
					logger.error("The immunization with uuid {} could not be deleted due to an Exception", immunizationToBeDeleted.getUuid(), e);
				}
			});
		}
		return processedImmunizations;
	}

	@Override
	@RightsAllowed(UserRight._IMMUNIZATION_DELETE)
	public void restore(String uuid) {
		super.restore(uuid);
	}

	@Override
	@RightsAllowed(UserRight._IMMUNIZATION_DELETE)
	public List<ProcessedEntity> restore(List<String> uuids) {
		List<ProcessedEntity> processedImmunizationUuids = new ArrayList<>();
		List<Immunization> immunizationsToBeRestored = service.getByUuids(uuids);

		if (immunizationsToBeRestored != null) {
			immunizationsToBeRestored.forEach(immunizationToBeRestored -> {
				try {
					service.restore(immunizationToBeRestored);
					processedImmunizationUuids.add(new ProcessedEntity(immunizationToBeRestored.getUuid(), ProcessedEntityStatus.SUCCESS));
				} catch (Exception e) {
					processedImmunizationUuids.add(new ProcessedEntity(immunizationToBeRestored.getUuid(), ProcessedEntityStatus.INTERNAL_FAILURE));
					logger.error("The immunization with uuid {} could not be restored due to an Exception", immunizationToBeRestored.getUuid(), e);
				}
			});
		}
		return processedImmunizationUuids;
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
	@RightsAllowed({
		UserRight._IMMUNIZATION_CREATE,
		UserRight._IMMUNIZATION_EDIT })
	public ImmunizationDto save(@Valid @NotNull ImmunizationDto dto) {
		return save(dto, true, true);
	}

	@RightsAllowed({
		UserRight._IMMUNIZATION_CREATE,
		UserRight._IMMUNIZATION_EDIT })
	public ImmunizationDto save(@Valid @NotNull ImmunizationDto dto, boolean checkChangeDate, boolean internal) {
		Immunization existingImmunization = service.getByUuid(dto.getUuid());
		FacadeHelper.checkCreateAndEditRights(existingImmunization, userService, UserRight.IMMUNIZATION_CREATE, UserRight.IMMUNIZATION_EDIT);

		if (internal && existingImmunization != null && !service.isEditAllowed(existingImmunization)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorImmunizationNotEditable));
		}

		ImmunizationDto existingDto = toDto(existingImmunization);

		Pseudonymizer pseudonymizer = createPseudonymizer();
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

			vaccinationFacade
				.updateVaccinationStatuses(vaccination, existingVaccination, immunization.getPerson().getId(), immunization.getDisease());
		});

		service.ensurePersisted(immunization);

		onImmunizationChanged(immunization, internal);

		return toPseudonymizedDto(immunization, pseudonymizer);
	}

	@Override
	protected void pseudonymizeDto(Immunization source, ImmunizationDto dto, Pseudonymizer pseudonymizer, boolean inJurisdiction) {

		if (dto != null) {
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
	public void validate(@Valid ImmunizationDto immunizationDto) throws ValidationRuntimeException {
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

		if (immunizationDto.getReportingUser() == null && !immunizationDto.isPseudonymized()) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validReportingUser));
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

	@Override
	protected ImmunizationReferenceDto toRefDto(Immunization immunization) {
		return toReferenceDto(immunization);
	}

	@Override
	protected Immunization fillOrBuildEntity(@NotNull ImmunizationDto source, Immunization target, boolean checkChangeDate) {
		return fillOrBuildEntity(source, target, checkChangeDate, true);
	}

	protected Immunization fillOrBuildEntity(
		@NotNull ImmunizationDto source,
		Immunization target,
		boolean checkChangeDate,
		boolean includeVaccinations) {

		target = DtoHelper.fillOrBuildEntity(source, target, Immunization::build, checkChangeDate);

		target.setDisease(source.getDisease());
		target.setDiseaseDetails(source.getDiseaseDetails());
		target.setPerson(personService.getByReferenceDto(source.getPerson()));
		target.setReportDate(source.getReportDate());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setArchived(source.isArchived());
		if (source.getImmunizationStatus() != null) {
			target.setImmunizationStatus(source.getImmunizationStatus());
		}
		target.setMeansOfImmunization(source.getMeansOfImmunization());
		target.setMeansOfImmunizationDetails(source.getMeansOfImmunizationDetails());
		if (source.getImmunizationManagementStatus() != null) {
			target.setImmunizationManagementStatus(source.getImmunizationManagementStatus());
		}
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

		if (includeVaccinations) {
			List<Vaccination> vaccinationEntities = new ArrayList<>();
			for (VaccinationDto vaccinationDto : source.getVaccinations()) {
				Vaccination vaccination = vaccinationService.getByUuid(vaccinationDto.getUuid());
				vaccination = vaccinationFacade.fillOrBuildEntity(vaccinationDto, vaccination, checkChangeDate);
				vaccination.setImmunization(target);
				vaccinationEntities.add(vaccination);
			}
			target.getVaccinations().clear();
			target.getVaccinations().addAll(vaccinationEntities);
		}

		if (source.getSormasToSormasOriginInfo() != null) {
			target.setSormasToSormasOriginInfo(originInfoService.getByUuid(source.getSormasToSormasOriginInfo().getUuid()));
		}

		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

		return target;
	}

	@Override
	@RightsAllowed(UserRight._SYSTEM)
	public void updateImmunizationStatuses() {
		service.updateImmunizationStatuses();
	}

	@Override
	@RightsAllowed(UserRight._IMMUNIZATION_EDIT)
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
		return toDtos(service.getByPersonUuids(uuids, true).stream());
	}

	@RightsAllowed({
		UserRight._IMMUNIZATION_CREATE,
		UserRight._IMMUNIZATION_EDIT })
	public void onImmunizationChanged(Immunization immunization, boolean syncShares) {
		if (syncShares && sormasToSormasFacade.isFeatureConfigured()) {
			syncSharesAsync(immunization);
		}
	}

	private void syncSharesAsync(Immunization immunization) {
		//sync case/contact/event this immunization was shared with

		SormasToSormasOriginInfo sormasToSormasOriginInfo = immunization.getSormasToSormasOriginInfo();
		List<DataHelper.Pair<ShareRequestDataType, ShareTreeCriteria>> syncParams = new ArrayList<>();
		if (sormasToSormasOriginInfo != null) {
			if (!sormasToSormasOriginInfo.getCases().isEmpty()) {
				syncParams.addAll(
					immunization.getPerson()
						.getCases()
						.stream()
						.filter(c -> DataHelper.isSame(c.getSormasToSormasOriginInfo(), sormasToSormasOriginInfo))
						.map(c -> new DataHelper.Pair<>(ShareRequestDataType.CASE, new ShareTreeCriteria(c.getUuid())))
						.collect(Collectors.toList()));
			}

			if (!sormasToSormasOriginInfo.getContacts().isEmpty()) {
				syncParams.addAll(
					immunization.getPerson()
						.getContacts()
						.stream()
						.filter(c -> DataHelper.isSame(c.getSormasToSormasOriginInfo(), sormasToSormasOriginInfo))
						.map(c -> new DataHelper.Pair<>(ShareRequestDataType.CONTACT, new ShareTreeCriteria(c.getUuid())))
						.collect(Collectors.toList()));
			}

			if (!sormasToSormasOriginInfo.getEvents().isEmpty()) {
				syncParams.addAll(
					immunization.getPerson()
						.getEventParticipants()
						.stream()
						.map(EventParticipant::getEvent)
						.distinct()
						.filter(c -> DataHelper.isSame(c.getSormasToSormasOriginInfo(), sormasToSormasOriginInfo))
						.map(e -> new DataHelper.Pair<>(ShareRequestDataType.EVENT, new ShareTreeCriteria(e.getUuid())))
						.collect(Collectors.toList()));
			}
		}

		syncParams.addAll(
			immunization.getSormasToSormasShares()
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
						return new DataHelper.Pair<>(ShareRequestDataType.EVENT, new ShareTreeCriteria(s.getEvent().getUuid()));
					}

					return null;
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList()));

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

	@RightsAllowed({
		UserRight._IMMUNIZATION_CREATE,
		UserRight._PERSON_EDIT })
	public void copyImmunizationToLeadPerson(ImmunizationDto immunizationDto, PersonDto leadPerson, List<VaccinationDto> leadPersonVaccinations) {
		Immunization immunization = fillOrBuildEntity(immunizationDto, null, false, false);
		immunization.setUuid(DataHelper.createUuid());

		immunization.setPerson(personService.getByReferenceDto(leadPerson.toReference()));
		service.persist(immunization);

		vaccinationFacade.copyOrMergeVaccinations(immunizationDto, immunization, leadPersonVaccinations);

		service.ensurePersisted(immunization);
	}

	@Override
	@RightsAllowed(UserRight._IMMUNIZATION_ARCHIVE)
	public ProcessedEntity archive(String entityUuid, Date endOfProcessingDate) {
		return super.archive(entityUuid, endOfProcessingDate);
	}

	@Override
	@RightsAllowed(UserRight._IMMUNIZATION_ARCHIVE)
	public List<ProcessedEntity> dearchive(List<String> entityUuids, String dearchiveReason) {
		return super.dearchive(entityUuids, dearchiveReason);
	}

	@LocalBean
	@Stateless
	public static class ImmunizationFacadeEjbLocal extends ImmunizationFacadeEjb {

		public ImmunizationFacadeEjbLocal() {
			super();
		}

		@Inject
		public ImmunizationFacadeEjbLocal(ImmunizationService service) {
			super(service);
		}
	}
}
