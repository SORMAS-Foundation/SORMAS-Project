package de.symeda.sormas.backend.travelentry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.travelentry.DeaContentEntry;
import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.travelentry.TravelEntryFacade;
import de.symeda.sormas.api.travelentry.TravelEntryIndexDto;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityService;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless(name = "TravelEntryFacade")
public class TravelEntryFacadeEjb implements TravelEntryFacade {

	@EJB
	TravelEntryService travelEntryService;
	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;
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
	@EJB
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacade;

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

	@Override
	public TravelEntryDto getByUuid(String uuid) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return convertToDto(travelEntryService.getByUuid(uuid), pseudonymizer);
	}

	@Override
	public TravelEntryReferenceDto getReferenceByUuid(String uuid) {
		return Optional.of(uuid).map(u -> travelEntryService.getByUuid(u)).map(TravelEntryFacadeEjb::toReferenceDto).orElse(null);
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
		TravelEntry travelEntry = travelEntryService.getByUuid(uuid);
		if (travelEntry != null) {
			travelEntry.setArchived(false);
			travelEntryService.ensurePersisted(travelEntry);
		}
	}

	@Override
	public boolean isDeleted(String travelEntryUuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TravelEntry> from = cq.from(TravelEntry.class);

		cq.where(cb.and(cb.isTrue(from.get(TravelEntry.DELETED)), cb.equal(from.get(AbstractDomainObject.UUID), travelEntryUuid)));
		cq.select(cb.count(from));
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	@Override
	public boolean isArchived(String travelEntryUuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TravelEntry> from = cq.from(TravelEntry.class);

		cq.where(cb.and(cb.equal(from.get(TravelEntry.ARCHIVED), true), cb.equal(from.get(AbstractDomainObject.UUID), travelEntryUuid)));
		cq.select(cb.count(from));
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	@Override
	public void archiveOrDearchiveTravelEntry(String travelEntryUuid, boolean archive) {
		TravelEntry travelEntry = travelEntryService.getByUuid(travelEntryUuid);
		travelEntry.setArchived(archive);
		travelEntryService.ensurePersisted(travelEntry);
	}

	@Override
	public Boolean isTravelEntryEditAllowed(String travelEntryUuid) {
		TravelEntry travelEntry = travelEntryService.getByUuid(travelEntryUuid);
		return travelEntryService.isTravelEntryEditAllowed(travelEntry);
	}

	@Override
	public void deleteTravelEntry(String travelEntryUuid) {
		if (!userService.hasRight(UserRight.TRAVEL_ENTRY_DELETE)) {
			throw new UnsupportedOperationException("User " + userService.getCurrentUser().getUuid() + " is not allowed to delete travel entries");
		}

		TravelEntry travelEntry = travelEntryService.getByUuid(travelEntryUuid);
		travelEntryService.delete(travelEntry);

		if (travelEntry.getResultingCase() != null) {
			caseFacade.onCaseChanged(CaseFacadeEjb.CaseFacadeEjbLocal.toDto(travelEntry.getResultingCase()), travelEntry.getResultingCase());
		}
	}

	@Override
	public List<TravelEntryDto> getAllAfter(Date date) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return travelEntryService.getAllActiveAfter(date).stream().map(c -> convertToDto(c, pseudonymizer)).collect(Collectors.toList());
	}

	@Override
	public List<TravelEntryDto> getByUuids(List<String> uuids) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return travelEntryService.getByUuids(uuids).stream().map(c -> convertToDto(c, pseudonymizer)).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids() {
		return travelEntryService.getAllUuids();
	}

	@Override
	public List<DeaContentEntry> getDeaContentOfLastTravelEntry() {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<TravelEntry> query = cb.createQuery(TravelEntry.class);
		final Root<TravelEntry> from = query.from(TravelEntry.class);
		query.select(from);
		query.where(cb.and(travelEntryService.createDefaultFilter(cb, from), cb.lessThanOrEqualTo(from.get(TravelEntry.CREATION_DATE), new Date())));
		query.orderBy(cb.desc(from.get(TravelEntry.CREATION_DATE)));

		final TypedQuery<TravelEntry> q = em.createQuery(query);
		final TravelEntry lastTravelEntry = q.getResultList().stream().findFirst().orElse(null);

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
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		final Root<TravelEntry> travelEntry = cq.from(TravelEntry.class);

		TravelEntryQueryContext travelEntryQueryContext = new TravelEntryQueryContext(cb, cq, travelEntry);

		Predicate filter = ignoreUserFilter ? null : travelEntryService.createUserFilter(travelEntryQueryContext);
		if (criteria != null) {
			final Predicate criteriaFilter = travelEntryService.buildCriteriaFilter(criteria, travelEntryQueryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(travelEntry));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public TravelEntryDto save(@Valid TravelEntryDto dto) {
		TravelEntry existingTravelEntry = dto.getUuid() != null ? travelEntryService.getByUuid(dto.getUuid()) : null;
		TravelEntryDto existingDto = toDto(existingTravelEntry);

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		restorePseudonymizedDto(dto, existingDto, existingTravelEntry, pseudonymizer);

		validate(dto);

		existingTravelEntry = fillOrBuildEntity(dto, existingTravelEntry, true);
		travelEntryService.ensurePersisted(existingTravelEntry);

		return convertToDto(existingTravelEntry, pseudonymizer);
	}

	public TravelEntryDto convertToDto(TravelEntry source, Pseudonymizer pseudonymizer) {
		TravelEntryDto dto = toDto(source);
		pseudonimyzeDto(source, dto, pseudonymizer);
		return dto;
	}

	private void pseudonimyzeDto(TravelEntry source, TravelEntryDto dto, Pseudonymizer pseudonymizer) {
		if (dto != null) {
			boolean inJurisdiction = travelEntryService.inJurisdictionOrOwned(source);
			pseudonymizer.pseudonymizeDto(TravelEntryDto.class, dto, inJurisdiction, c -> {
				User currentUser = userService.getCurrentUser();
				pseudonymizer.pseudonymizeUser(source.getReportingUser(), currentUser, dto::setReportingUser);
			});
		}
	}

	private void restorePseudonymizedDto(TravelEntryDto dto, TravelEntryDto existingDto, TravelEntry travelEntry, Pseudonymizer pseudonymizer) {
		if (existingDto != null) {
			final boolean inJurisdiction = travelEntryService.inJurisdictionOrOwned(travelEntry);
			final User currentUser = userService.getCurrentUser();
			pseudonymizer.restoreUser(travelEntry.getReportingUser(), currentUser, dto, dto::setReportingUser);
			pseudonymizer.restorePseudonymizedValues(TravelEntryDto.class, dto, existingDto, inJurisdiction);
		}
	}

	@Override
	public boolean exists(String uuid) {
		return travelEntryService.exists(uuid);
	}

	@Override
	public List<TravelEntryIndexDto> getIndexList(TravelEntryCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<TravelEntryIndexDto> cq = cb.createQuery(TravelEntryIndexDto.class);
		final Root<TravelEntry> travelEntry = cq.from(TravelEntry.class);

		TravelEntryQueryContext travelEntryQueryContext = new TravelEntryQueryContext(cb, cq, travelEntry);
		TravelEntryJoins<TravelEntry> joins = (TravelEntryJoins<TravelEntry>) travelEntryQueryContext.getJoins();

		final Join<TravelEntry, Person> person = joins.getPerson();
		final Join<TravelEntry, PointOfEntry> pointOfEntry = joins.getPointOfEntry();

		final Join<Person, Location> location = person.join(Person.ADDRESS, JoinType.LEFT);
		final Join<Location, District> district = location.join(Location.DISTRICT, JoinType.LEFT);

		cq.multiselect(
			travelEntry.get(TravelEntry.UUID),
			travelEntry.get(TravelEntry.EXTERNAL_ID),
			person.get(Person.FIRST_NAME),
			person.get(Person.LAST_NAME),
			district.get(District.NAME),
			pointOfEntry.get(PointOfEntry.NAME),
			travelEntry.get(TravelEntry.POINT_OF_ENTRY_DETAILS),
			travelEntry.get(TravelEntry.RECOVERED),
			travelEntry.get(TravelEntry.VACCINATED),
			travelEntry.get(TravelEntry.TESTED_NEGATIVE),
			travelEntry.get(TravelEntry.QUARANTINE_TO),
			travelEntry.get(TravelEntry.REPORT_DATE),
			travelEntry.get(TravelEntry.DISEASE),
			travelEntry.get(TravelEntry.CHANGE_DATE),
			JurisdictionHelper.booleanSelector(cb, travelEntryService.inJurisdictionOrOwned(travelEntryQueryContext)));

		Predicate filter = travelEntryService.createUserFilter(travelEntryQueryContext);
		if (criteria != null) {
			final Predicate criteriaFilter = travelEntryService.buildCriteriaFilter(criteria, travelEntryQueryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		if (CollectionUtils.isNotEmpty(sortProperties)) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case TravelEntryIndexDto.UUID:
				case TravelEntryIndexDto.EXTERNAL_ID:
				case TravelEntryIndexDto.RECOVERED:
				case TravelEntryIndexDto.VACCINATED:
				case TravelEntryIndexDto.TESTED_NEGATIVE:
				case TravelEntryIndexDto.QUARANTINE_TO:
					expression = travelEntry.get(sortProperty.propertyName);
					break;
				case TravelEntryIndexDto.PERSON_FIRST_NAME:
					expression = person.get(Person.FIRST_NAME);
					break;
				case TravelEntryIndexDto.PERSON_LAST_NAME:
					expression = person.get(Person.LAST_NAME);
					break;
				case TravelEntryIndexDto.HOME_DISTRICT_NAME:
					expression = district.get(District.NAME);
					break;
				case TravelEntryIndexDto.POINT_OF_ENTRY_NAME:
					expression = pointOfEntry.get(PointOfEntry.NAME);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(travelEntry.get(TravelEntry.CHANGE_DATE)));
		}

		cq.distinct(true);

		List<TravelEntryIndexDto> resultList = QueryHelper.getResultList(em, cq, first, max);

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		pseudonymizer.pseudonymizeDtoCollection(TravelEntryIndexDto.class, resultList, TravelEntryIndexDto::isInJurisdiction, null);

		return resultList;
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

	@LocalBean
	@Stateless
	public static class TravelEntryFacadeEjbLocal extends TravelEntryFacadeEjb {
	}
}
