package de.symeda.sormas.backend.travelentry;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.common.CoreEntityType;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.deletionconfiguration.DeletionReference;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
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
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
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
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.Pseudonymizer;

@Stateless(name = "TravelEntryFacade")
public class TravelEntryFacadeEjb
	extends AbstractCoreFacadeEjb<TravelEntry, TravelEntryDto, TravelEntryIndexDto, TravelEntryReferenceDto, TravelEntryService, TravelEntryCriteria>
	implements TravelEntryFacade {

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
	public TravelEntryFacadeEjb(TravelEntryService service, UserService userService) {
		super(TravelEntry.class, TravelEntryDto.class, service, userService);
	}

	@Override
	public boolean isDeleted(String travelEntryUuid) {
		return service.isDeleted(travelEntryUuid);
	}

	@Override
	public Boolean isTravelEntryEditAllowed(String travelEntryUuid) {
		TravelEntry travelEntry = service.getByUuid(travelEntryUuid);
		return service.isTravelEntryEditAllowed(travelEntry);
	}

	@Override
	public void delete(String travelEntryUuid) {
		if (!userService.hasRight(UserRight.TRAVEL_ENTRY_DELETE)) {
			throw new UnsupportedOperationException("User " + userService.getCurrentUser().getUuid() + " is not allowed to delete travel entries");
		}

		TravelEntry travelEntry = service.getByUuid(travelEntryUuid);
		service.delete(travelEntry);

		if (travelEntry.getResultingCase() != null) {
			caseFacade.onCaseChanged(caseFacade.toDto(travelEntry.getResultingCase()), travelEntry.getResultingCase());
		}
	}

	@Override
	protected void selectDtoFields(CriteriaQuery<TravelEntryDto> cq, Root<TravelEntry> root) {
	}

	@Override
	public List<DeaContentEntry> getDeaContentOfLastTravelEntry() {
		final TravelEntry lastTravelEntry = service.getLastTravelEntry();

		if (lastTravelEntry != null) {
			Pseudonymizer aDefault = Pseudonymizer.getDefault(userService::hasRight);
			TravelEntryDto travelEntryDto = convertToDto(lastTravelEntry, aDefault);
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
	protected void pseudonymizeDto(TravelEntry source, TravelEntryDto dto, Pseudonymizer pseudonymizer) {
		if (dto != null) {
			boolean inJurisdiction = service.inJurisdictionOrOwned(source);
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
	public void validate(TravelEntryDto travelEntryDto) {
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

		return target;
	}

	@Override
	protected String getDeleteReferenceField(DeletionReference deletionReference) {
		if (deletionReference.equals(DeletionReference.ORIGIN)) {
			return TravelEntry.REPORT_DATE;
		}
		return super.getDeleteReferenceField(deletionReference);
	}

	@LocalBean
	@Stateless
	public static class TravelEntryFacadeEjbLocal extends TravelEntryFacadeEjb {

		public TravelEntryFacadeEjbLocal() {
		}

		@Inject
		public TravelEntryFacadeEjbLocal(TravelEntryService service, UserService userService) {
			super(service, userService);
		}
	}
}
