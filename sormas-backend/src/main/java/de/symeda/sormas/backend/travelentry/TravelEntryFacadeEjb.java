package de.symeda.sormas.backend.travelentry;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.common.CoreEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.common.progress.ProcessedEntityStatus;
import de.symeda.sormas.api.deletionconfiguration.DeletionReference;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.travelentry.DeaContentEntry;
import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.travelentry.TravelEntryFacade;
import de.symeda.sormas.api.travelentry.TravelEntryIndexDto;
import de.symeda.sormas.api.travelentry.TravelEntryListCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryListEntryDto;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractCoreFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityService;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryService;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.travelentry.services.TravelEntryListService;
import de.symeda.sormas.backend.travelentry.services.TravelEntryService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "TravelEntryFacade")
@RightsAllowed(UserRight._TRAVEL_ENTRY_VIEW)
public class TravelEntryFacadeEjb
	extends AbstractCoreFacadeEjb<TravelEntry, TravelEntryDto, TravelEntryIndexDto, TravelEntryReferenceDto, TravelEntryService, TravelEntryCriteria>
	implements TravelEntryFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private TravelEntryListService travelEntryListService;
	@EJB
	private PersonService personService;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;
	@EJB
	private PointOfEntryService pointOfEntryService;
	@EJB
	private CaseService caseService;
	@EJB
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacade;
	@EJB
	private TravelEntryService travelEntryService;

	public TravelEntryFacadeEjb() {
	}

	public static TravelEntryReferenceDto toReferenceDto(TravelEntry entity) {

		if (entity == null) {
			return null;
		}
		return new TravelEntryReferenceDto(
			entity.getUuid(),
			entity.getExternalId(),
			entity.getPerson().getFirstName(),
			entity.getPerson().getLastName());
	}

	@Inject
	public TravelEntryFacadeEjb(TravelEntryService service) {
		super(TravelEntry.class, TravelEntryDto.class, service);
	}

	@Override
	@RightsAllowed(UserRight._TRAVEL_ENTRY_DELETE)
	public void delete(String travelEntryUuid, DeletionDetails deletionDetails) {
		TravelEntry travelEntry = service.getByUuid(travelEntryUuid);

		if (!service.inJurisdictionOrOwned(travelEntry)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.messageTravelEntryOutsideJurisdictionDeletionDenied));
		}

		service.delete(travelEntry, deletionDetails);

		if (travelEntry.getResultingCase() != null) {
			caseFacade.onCaseChanged(caseFacade.toDto(travelEntry.getResultingCase()), travelEntry.getResultingCase());
		}
	}

	@Override
	@RightsAllowed(UserRight._TRAVEL_ENTRY_DELETE)
	public List<ProcessedEntity> delete(List<String> uuids, DeletionDetails deletionDetails) {
		List<ProcessedEntity> processedTravelEntries = new ArrayList<>();
		List<TravelEntry> travelEntriesToBeDeleted = travelEntryService.getByUuids(uuids);

		if (travelEntriesToBeDeleted != null) {
			travelEntriesToBeDeleted.forEach(travelEntryToBeDeleted -> {
				if (!travelEntryToBeDeleted.isDeleted()) {
					try {
						delete(travelEntryToBeDeleted.getUuid(), deletionDetails);
						processedTravelEntries.add(new ProcessedEntity(travelEntryToBeDeleted.getUuid(), ProcessedEntityStatus.SUCCESS));
					} catch (AccessDeniedException e) {
						processedTravelEntries
							.add(new ProcessedEntity(travelEntryToBeDeleted.getUuid(), ProcessedEntityStatus.ACCESS_DENIED_FAILURE));
						logger.error(
							"The travel entry with uuid {} could not be deleted due to an AccessDeniedException",
							travelEntryToBeDeleted.getUuid(),
							e);
					} catch (Exception e) {
						processedTravelEntries.add(new ProcessedEntity(travelEntryToBeDeleted.getUuid(), ProcessedEntityStatus.INTERNAL_FAILURE));
						logger.error("The travel entry with uuid:" + travelEntryToBeDeleted.getUuid() + "could not be deleted");
					}
				}
			});
		}

		return processedTravelEntries;
	}

	@Override
	@RightsAllowed(UserRight._TRAVEL_ENTRY_DELETE)
	public void restore(String uuid) {
		super.restore(uuid);
	}

	@Override
	@RightsAllowed(UserRight._TRAVEL_ENTRY_DELETE)
	public List<ProcessedEntity> restore(List<String> uuids) {
		List<ProcessedEntity> processedTravelEntries = new ArrayList<>();
		List<TravelEntry> travelEntriesToBeRestored = travelEntryService.getByUuids(uuids);

		if (travelEntriesToBeRestored != null) {
			travelEntriesToBeRestored.forEach(travelEntryToBeRestored -> {
				try {
					restore(travelEntryToBeRestored.getUuid());
					processedTravelEntries.add(new ProcessedEntity(travelEntryToBeRestored.getUuid(), ProcessedEntityStatus.SUCCESS));
				} catch (Exception e) {
					processedTravelEntries.add(new ProcessedEntity(travelEntryToBeRestored.getUuid(), ProcessedEntityStatus.INTERNAL_FAILURE));
					logger.error("The travel entry with uuid {} could not be restored due to an Exception", travelEntryToBeRestored.getUuid(), e);
				}
			});
		}

		return processedTravelEntries;
	}

	@Override
	public List<DeaContentEntry> getDeaContentOfLastTravelEntry() {
		final TravelEntry lastTravelEntry = service.getLastTravelEntry();

		if (lastTravelEntry != null) {
			TravelEntryDto travelEntryDto = toPseudonymizedDto(lastTravelEntry);
			return travelEntryDto.getDeaContent();
		}

		return null;
	}

	@Override
	public long count(TravelEntryCriteria criteria) {
		return count(criteria, false);
	}

	@Override
	public long count(TravelEntryCriteria criteria, boolean ignoreUserFilter) {
		return service.count(criteria, ignoreUserFilter);
	}

	@Override
	protected void pseudonymizeDto(TravelEntry source, TravelEntryDto dto, Pseudonymizer pseudonymizer, boolean inJurisdiction) {

		if (dto != null) {
			pseudonymizer.pseudonymizeDto(TravelEntryDto.class, dto, inJurisdiction, c -> {
				User currentUser = userService.getCurrentUser();
				pseudonymizer.pseudonymizeUser(source.getReportingUser(), currentUser, dto::setReportingUser);
			});
		}
	}

	@Override
	protected void restorePseudonymizedDto(TravelEntryDto dto, TravelEntryDto existingDto, TravelEntry entity, Pseudonymizer pseudonymizer) {
		if (existingDto != null) {
			final boolean inJurisdiction = service.inJurisdictionOrOwned(entity);
			final User currentUser = userService.getCurrentUser();
			pseudonymizer.restoreUser(entity.getReportingUser(), currentUser, dto, dto::setReportingUser);
			pseudonymizer.restorePseudonymizedValues(TravelEntryDto.class, dto, existingDto, inJurisdiction);
		}
	}

	@Override
	public List<TravelEntryIndexDto> getIndexList(TravelEntryCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		List<TravelEntryIndexDto> resultList = service.getIndexList(criteria, first, max, sortProperties);

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		pseudonymizer.pseudonymizeDtoCollection(TravelEntryIndexDto.class, resultList, TravelEntryIndexDto::isInJurisdiction, null);

		return resultList;
	}

	public Page<TravelEntryIndexDto> getIndexPage(TravelEntryCriteria criteria, Integer offset, Integer size, List<SortProperty> sortProperties) {
		List<TravelEntryIndexDto> travelEntryIndexList = service.getIndexList(criteria, offset, size, sortProperties);
		long totalElementCount = count(criteria);
		return new Page<>(travelEntryIndexList, offset, size, totalElementCount);
	}

	@Override
	public List<TravelEntryDto> getByPersonUuids(List<String> uuids) {
		return toDtos(service.getByPersonUuids(uuids).stream());
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@RightsAllowed(UserRight._SYSTEM)
	public void archiveAllArchivableTravelEntries(int daysAfterTravelEntryGetsArchived) {
		archiveAllArchivableTravelEntry(daysAfterTravelEntryGetsArchived, LocalDate.now());
	}

	private void archiveAllArchivableTravelEntry(int daysAfterTravelEntryGetsArchived, @NotNull LocalDate referenceDate) {
		LocalDate notChangedSince = referenceDate.minusDays(daysAfterTravelEntryGetsArchived);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TravelEntry> from = cq.from(TravelEntry.class);

		Timestamp notChangedTimestamp = Timestamp.valueOf(notChangedSince.atStartOfDay());
		cq.where(
			cb.equal(from.get(TravelEntry.ARCHIVED), false),
			cb.equal(from.get(TravelEntry.DELETED), false),
			cb.not(service.createChangeDateFilter(cb, from, notChangedTimestamp)));
		cq.select(from.get(TravelEntry.UUID)).distinct(true);
		List<String> travelEntryUuids = em.createQuery(cq).getResultList();

		if (!travelEntryUuids.isEmpty()) {
			archive(travelEntryUuids);
		}
	}

	@Override
	public List<TravelEntryListEntryDto> getEntriesList(TravelEntryListCriteria criteria, Integer first, Integer max) {
		Long personId = null;
		Long caseId = null;
		if (criteria.getPersonReferenceDto() != null) {
			personId = personService.getIdByUuid(criteria.getPersonReferenceDto().getUuid());
		}
		if (criteria.getCaseReferenceDto() != null) {
			caseId = caseService.getIdByUuid(criteria.getCaseReferenceDto().getUuid());
		}
		List<TravelEntryListEntryDto> entries = travelEntryListService.getEntriesList(personId, caseId, first, max);

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		pseudonymizer.pseudonymizeDtoCollection(TravelEntryListEntryDto.class, entries, TravelEntryListEntryDto::isInJurisdiction, null);

		return entries;
	}

	@Override
	protected CoreEntityType getCoreEntityType() {
		return CoreEntityType.TRAVEL_ENTRY;
	}

	@Override
	public void validate(@Valid TravelEntryDto travelEntryDto) {
		if (travelEntryDto.getPerson() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validPerson));
		}
		if (travelEntryDto.getReportDate() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validReportDateTime));
		}
		if (travelEntryDto.getDisease() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validDisease));
		}
		if (travelEntryDto.getResponsibleRegion() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validRegion));
		}
		if (travelEntryDto.getResponsibleDistrict() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validDistrict));
		}
		if (travelEntryDto.getPointOfEntry() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validPointOfEntry));
		} else {
			if (travelEntryDto.getPointOfEntry().isOtherPointOfEntry() && StringUtils.isEmpty(travelEntryDto.getPointOfEntryDetails())) {
				throw new ValidationRuntimeException(
					I18nProperties.getValidationError(
						Validations.required,
						I18nProperties.getPrefixCaption(TravelEntryDto.I18N_PREFIX, TravelEntryDto.POINT_OF_ENTRY_DETAILS)));
			}
		}
		if (travelEntryDto.getDateOfArrival() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validDateOfArrival));
		}
	}

	public TravelEntryDto toDto(TravelEntry entity) {
		if (entity == null) {
			return null;
		}

		TravelEntryDto dto = new TravelEntryDto();
		DtoHelper.fillDto(dto, entity);

		dto.setPerson(PersonFacadeEjb.toReferenceDto(entity.getPerson()));
		dto.setReportDate(entity.getReportDate());
		dto.setReportingUser(entity.getReportingUser().toReference());
		dto.setArchived(entity.isArchived());
		dto.setDeleted(entity.isDeleted());
		dto.setDisease(entity.getDisease());
		dto.setDiseaseDetails(entity.getDiseaseDetails());
		dto.setDiseaseVariant(entity.getDiseaseVariant());
		dto.setDiseaseVariantDetails(entity.getDiseaseVariantDetails());
		dto.setResponsibleRegion(RegionFacadeEjb.toReferenceDto(entity.getResponsibleRegion()));
		dto.setResponsibleDistrict(DistrictFacadeEjb.toReferenceDto(entity.getResponsibleDistrict()));
		dto.setResponsibleCommunity(CommunityFacadeEjb.toReferenceDto(entity.getResponsibleCommunity()));
		dto.setPointOfEntryRegion(RegionFacadeEjb.toReferenceDto(entity.getPointOfEntryRegion()));
		dto.setPointOfEntryDistrict(DistrictFacadeEjb.toReferenceDto(entity.getPointOfEntryDistrict()));
		dto.setPointOfEntry(PointOfEntryFacadeEjb.toReferenceDto(entity.getPointOfEntry()));
		dto.setPointOfEntryDetails(entity.getPointOfEntryDetails());
		dto.setResultingCase(CaseFacadeEjb.toReferenceDto(entity.getResultingCase()));
		dto.setExternalId(entity.getExternalId());
		dto.setRecovered(entity.isRecovered());
		dto.setVaccinated(entity.isVaccinated());
		dto.setTestedNegative(entity.isTestedNegative());
		dto.setDeaContent(entity.getDeaContent());

		dto.setQuarantine(entity.getQuarantine());
		dto.setQuarantineTypeDetails(entity.getQuarantineTypeDetails());
		dto.setQuarantineFrom(entity.getQuarantineFrom());
		dto.setQuarantineTo(entity.getQuarantineTo());
		dto.setQuarantineHelpNeeded(entity.getQuarantineHelpNeeded());
		dto.setQuarantineOrderedVerbally(entity.isQuarantineOrderedVerbally());
		dto.setQuarantineOrderedOfficialDocument(entity.isQuarantineOrderedOfficialDocument());
		dto.setQuarantineOrderedVerballyDate(entity.getQuarantineOrderedVerballyDate());
		dto.setQuarantineOrderedOfficialDocumentDate(entity.getQuarantineOrderedOfficialDocumentDate());
		dto.setQuarantineHomePossible(entity.getQuarantineHomePossible());
		dto.setQuarantineHomePossibleComment(entity.getQuarantineHomePossibleComment());
		dto.setQuarantineHomeSupplyEnsured(entity.getQuarantineHomeSupplyEnsured());
		dto.setQuarantineHomeSupplyEnsuredComment(entity.getQuarantineHomeSupplyEnsuredComment());
		dto.setQuarantineExtended(entity.isQuarantineExtended());
		dto.setQuarantineReduced(entity.isQuarantineReduced());
		dto.setQuarantineOfficialOrderSent(entity.isQuarantineOfficialOrderSent());
		dto.setQuarantineOfficialOrderSentDate(entity.getQuarantineOfficialOrderSentDate());
		dto.setDateOfArrival(entity.getDateOfArrival());

		dto.setDeletionReason(entity.getDeletionReason());
		dto.setOtherDeletionReason(entity.getOtherDeletionReason());

		return dto;
	}

	@Override
	public TravelEntryReferenceDto toRefDto(TravelEntry entity) {
		if (entity == null) {
			return null;
		}
		return new TravelEntryReferenceDto(
			entity.getUuid(),
			entity.getExternalId(),
			entity.getPerson().getFirstName(),
			entity.getPerson().getLastName());
	}

	@Override
	protected TravelEntry fillOrBuildEntity(@NotNull TravelEntryDto source, TravelEntry target, boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, target, TravelEntry::new, checkChangeDate);

		target.setPerson(personService.getByReferenceDto(source.getPerson()));
		target.setReportDate(source.getReportDate());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setArchived(source.isArchived());
		target.setDeleted(source.isDeleted());
		target.setDisease(source.getDisease());
		target.setDiseaseDetails(source.getDiseaseDetails());
		target.setDiseaseVariant(source.getDiseaseVariant());
		target.setDiseaseVariantDetails(source.getDiseaseVariantDetails());
		target.setResponsibleRegion(regionService.getByReferenceDto(source.getResponsibleRegion()));
		target.setResponsibleDistrict(districtService.getByReferenceDto(source.getResponsibleDistrict()));
		target.setResponsibleCommunity(communityService.getByReferenceDto(source.getResponsibleCommunity()));
		target.setPointOfEntryRegion(regionService.getByReferenceDto(source.getPointOfEntryRegion()));
		target.setPointOfEntryDistrict(districtService.getByReferenceDto(source.getPointOfEntryDistrict()));
		target.setPointOfEntry(pointOfEntryService.getByReferenceDto(source.getPointOfEntry()));
		target.setPointOfEntryDetails(source.getPointOfEntryDetails());
		target.setResultingCase(caseService.getByReferenceDto(source.getResultingCase()));
		target.setExternalId(source.getExternalId());
		target.setRecovered(source.isRecovered());
		target.setVaccinated(source.isVaccinated());
		target.setTestedNegative(source.isTestedNegative());
		target.setDeaContent(source.getDeaContent());

		target.setQuarantine(source.getQuarantine());
		target.setQuarantineTypeDetails(source.getQuarantineTypeDetails());
		target.setQuarantineFrom(source.getQuarantineFrom());
		target.setQuarantineTo(source.getQuarantineTo());
		target.setQuarantineHelpNeeded(source.getQuarantineHelpNeeded());
		target.setQuarantineOrderedVerbally(source.isQuarantineOrderedVerbally());
		target.setQuarantineOrderedOfficialDocument(source.isQuarantineOrderedOfficialDocument());
		target.setQuarantineOrderedVerballyDate(source.getQuarantineOrderedVerballyDate());
		target.setQuarantineOrderedOfficialDocumentDate(source.getQuarantineOrderedOfficialDocumentDate());
		target.setQuarantineHomePossible(source.getQuarantineHomePossible());
		target.setQuarantineHomePossibleComment(source.getQuarantineHomePossibleComment());
		target.setQuarantineHomeSupplyEnsured(source.getQuarantineHomeSupplyEnsured());
		target.setQuarantineHomeSupplyEnsuredComment(source.getQuarantineHomeSupplyEnsuredComment());
		target.setQuarantineExtended(source.isQuarantineExtended());
		target.setQuarantineReduced(source.isQuarantineReduced());
		target.setQuarantineOfficialOrderSent(source.isQuarantineOfficialOrderSent());
		target.setQuarantineOfficialOrderSentDate(source.getQuarantineOfficialOrderSentDate());
		target.setDateOfArrival(source.getDateOfArrival());

		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

		return target;
	}

	@Override
	protected String getDeleteReferenceField(DeletionReference deletionReference) {
		if (deletionReference.equals(DeletionReference.ORIGIN)) {
			return TravelEntry.DATE_OF_ARRIVAL;
		} else if (deletionReference == DeletionReference.REPORT) {
			return TravelEntry.REPORT_DATE;
		}

		return super.getDeleteReferenceField(deletionReference);
	}

	@Override
	@RightsAllowed(UserRight._TRAVEL_ENTRY_ARCHIVE)
	public ProcessedEntity archive(String entityUuid, Date endOfProcessingDate) {
		return super.archive(entityUuid, endOfProcessingDate);
	}

	@Override
	@RightsAllowed(UserRight._TRAVEL_ENTRY_ARCHIVE)
	public List<ProcessedEntity> archive(List<String> entityUuids) {
		return super.archive(entityUuids);
	}

	@Override
	@RightsAllowed(UserRight._TRAVEL_ENTRY_ARCHIVE)
	public List<ProcessedEntity> dearchive(List<String> entityUuids, String dearchiveReason) {
		return super.dearchive(entityUuids, dearchiveReason);
	}

	@Override
	public List<String> getArchivedUuidsSince(Date since) {
		throw new NotImplementedException();
	}

	@Override
	@RightsAllowed({
		UserRight._TRAVEL_ENTRY_CREATE,
		UserRight._TRAVEL_ENTRY_EDIT })
	public TravelEntryDto save(@Valid @NotNull TravelEntryDto travelEntryDto) {
		TravelEntry existingTravelEntry = travelEntryService.getByUuid(travelEntryDto.getUuid());
		FacadeHelper.checkCreateAndEditRights(existingTravelEntry, userService, UserRight.TRAVEL_ENTRY_CREATE, UserRight.TRAVEL_ENTRY_EDIT);
		return doSave(travelEntryDto);
	}

	@LocalBean
	@Stateless
	public static class TravelEntryFacadeEjbLocal extends TravelEntryFacadeEjb {

		public TravelEntryFacadeEjbLocal() {
		}

		@Inject
		public TravelEntryFacadeEjbLocal(TravelEntryService service) {
			super(service);
		}
	}
}
