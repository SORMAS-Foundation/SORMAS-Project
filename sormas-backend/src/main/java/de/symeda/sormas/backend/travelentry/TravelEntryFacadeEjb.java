package de.symeda.sormas.backend.travelentry;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.travelentry.TravelEntryFacade;
import de.symeda.sormas.api.travelentry.TravelEntryIndexDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.immunization.Immunization;
import de.symeda.sormas.backend.infrastructure.PointOfEntryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.PointOfEntryService;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.CommunityFacadeEjb;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "TravelEntryFacade")
public class TravelEntryFacadeEjb implements TravelEntryFacade {

	@EJB
	TravelEntryService travelEntryService;
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
	private PointOfEntryService pointOfEntryService;
	@EJB
	private CaseService caseService;

	@Override
	public TravelEntryDto getByUuid(String uuid) {
		return null;
	}

	@Override
	public void archive(String uuid) {
		TravelEntry travelEntry = travelEntryService.getByUuid(uuid);
		if (travelEntry != null) {
			travelEntry.setArchived(true);
			travelEntryService.ensurePersisted(travelEntry);
		}
	}

	@Override
	public void dearchive(String uuid) {
		//to be implemented
	}

	@Override
	public List<TravelEntryDto> getAllAfter(Date date) {
		return null;
	}

	@Override
	public List<TravelEntryDto> getByUuids(List<String> uuids) {
		return null;
	}

	@Override
	public List<String> getAllUuids() {
		return null;
	}

	@Override
	public long count(TravelEntryCriteria criteria) {
		return 0;
	}

	@Override
	public TravelEntryDto save(TravelEntryDto dto) {
		return null;
	}

	@Override
	public List<TravelEntryIndexDto> getIndexList(TravelEntryCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		return null;
	}

	@Override
	public void validate(TravelEntryDto travelEntryDto) {

	}

	public TravelEntryDto toDto(TravelEntry entity) {
		if (entity != null) {
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

		dto.setWasInQuarantineBeforeIsolation(entity.getWasInQuarantineBeforeIsolation());
		dto.setQuarantineReasonBeforeIsolation(entity.getQuarantineReasonBeforeIsolation());
		dto.setQuarantineReasonBeforeIsolationDetails(entity.getQuarantineReasonBeforeIsolationDetails());
		dto.setEndOfIsolationReason(entity.getEndOfIsolationReason());
		dto.setEndOfIsolationReasonDetails(entity.getEndOfIsolationReasonDetails());

		return dto;
	}

	private TravelEntry fillOrBuildEntity(@NotNull TravelEntryDto source, TravelEntry target, boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, target, TravelEntry::new, checkChangeDate);

		target.setPerson(personService.getByReferenceDto(source.getPerson()));
		target.setReportDate(source.getReportDate());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setArchived(source.isArchived());
		target.setDeleted(source.isDeleted());
		target.setDisease(source.getDisease());
		target.setDiseaseDetails(source.getDiseaseDetails());
		target.setDiseaseVariant(source.getDiseaseVariant());
		target.setResponsibleRegion(regionService.getByReferenceDto(source.getResponsibleRegion()));
		target.setResponsibleDistrict(districtService.getByReferenceDto(source.getResponsibleDistrict()));
		target.setResponsibleCommunity(communityService.getByReferenceDto(source.getResponsibleCommunity()));
		target.setPointOfEntryRegion(regionService.getByReferenceDto(source.getPointOfEntryRegion()));
		target.setPointOfEntryDistrict(districtService.getByReferenceDto(source.getResponsibleDistrict()));
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

		target.setWasInQuarantineBeforeIsolation(source.getWasInQuarantineBeforeIsolation());
		target.setQuarantineReasonBeforeIsolation(source.getQuarantineReasonBeforeIsolation());
		target.setQuarantineReasonBeforeIsolationDetails(source.getQuarantineReasonBeforeIsolationDetails());
		target.setEndOfIsolationReason(source.getEndOfIsolationReason());
		target.setEndOfIsolationReasonDetails(source.getEndOfIsolationReasonDetails());

		return target;
	}

	@LocalBean
	@Stateless
	public static class TravelEntryFacadeEjbLocal extends TravelEntryFacadeEjb {

	}
}
