package de.symeda.sormas.backend.travelentry.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryIndexDto;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.document.DocumentService;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.travelentry.TravelEntryJoins;
import de.symeda.sormas.backend.travelentry.TravelEntryQueryContext;
import de.symeda.sormas.backend.travelentry.transformers.TravelEntryIndexDtoResultTransformer;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.JurisdictionHelper;

@Stateless
@LocalBean
public class TravelEntryService extends BaseTravelEntryService {

	@EJB
	private TaskService taskService;
	@EJB
	private DocumentService documentService;

	public List<TravelEntryIndexDto> getIndexList(TravelEntryCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
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
			JurisdictionHelper.booleanSelector(cb, inJurisdictionOrOwned(travelEntryQueryContext)),
			travelEntry.get(TravelEntry.CHANGE_DATE));

		Predicate filter = createUserFilter(travelEntryQueryContext);
		if (criteria != null) {
			final Predicate criteriaFilter = buildCriteriaFilter(criteria, travelEntryQueryContext);
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

		return createQuery(cq, first, max).unwrap(org.hibernate.query.Query.class)
			.setResultTransformer(new TravelEntryIndexDtoResultTransformer())
			.getResultList();
	}

	public long count(TravelEntryCriteria criteria, boolean ignoreUserFilter) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		final Root<TravelEntry> travelEntry = cq.from(TravelEntry.class);

		TravelEntryQueryContext travelEntryQueryContext = new TravelEntryQueryContext(cb, cq, travelEntry);

		Predicate filter = ignoreUserFilter ? null : createUserFilter(travelEntryQueryContext);
		if (criteria != null) {
			final Predicate criteriaFilter = buildCriteriaFilter(criteria, travelEntryQueryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(travelEntry));
		return em.createQuery(cq).getSingleResult();
	}

	public boolean inJurisdictionOrOwned(TravelEntry travelEntry) {
		return inJurisdictionOrOwned(travelEntry, getCurrentUser());
	}

	public boolean inJurisdictionOrOwned(TravelEntry travelEntry, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Boolean> cq = cb.createQuery(Boolean.class);
		Root<TravelEntry> root = cq.from(TravelEntry.class);
		cq.multiselect(JurisdictionHelper.booleanSelector(cb, inJurisdictionOrOwned(new TravelEntryQueryContext(cb, cq, root), user)));
		cq.where(cb.equal(root.get(TravelEntry.UUID), travelEntry.getUuid()));
		return em.createQuery(cq).getResultList().stream().anyMatch(aBoolean -> aBoolean);
	}

	public Predicate createActiveTravelEntriesFilter(CriteriaBuilder cb, From<?, TravelEntry> root) {
		return cb.and(cb.isFalse(root.get(TravelEntry.ARCHIVED)), cb.isFalse(root.get(TravelEntry.DELETED)));
	}

	public List<TravelEntry> getAllByResultingCase(Case caze) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TravelEntry> cq = cb.createQuery(getElementClass());
		Root<TravelEntry> from = cq.from(getElementClass());

		cq.where(cb.and(createDefaultFilter(cb, from), cb.equal(from.get(TravelEntry.RESULTING_CASE), caze)));
		cq.orderBy(cb.desc(from.get(TravelEntry.REPORT_DATE)));

		return em.createQuery(cq).getResultList();
	}

	public boolean isTravelEntryEditAllowed(TravelEntry travelEntry) {
		return userService.hasRight(UserRight.TRAVEL_ENTRY_EDIT) && inJurisdictionOrOwned(travelEntry);
	}

	@Override
	public void deletePermanent(TravelEntry travelEntry) {
		// Delete all tasks associated with this travel entry
		List<Task> tasks = taskService.findBy(
			new TaskCriteria().travelEntry(
				new TravelEntryReferenceDto(
					travelEntry.getUuid(),
					travelEntry.getExternalId(),
					travelEntry.getPerson().getFirstName(),
					travelEntry.getPerson().getLastName())),
			true);
		for (Task task : tasks) {
			taskService.deletePermanent(task);
		}

		documentService.getRelatedToEntity(DocumentRelatedEntityType.TRAVEL_ENTRY, travelEntry.getUuid())
			.forEach(document -> documentService.markAsDeleted(document));

		super.deletePermanent(travelEntry);
	}

	public boolean isDeleted(String travelEntryUuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TravelEntry> from = cq.from(TravelEntry.class);

		cq.where(cb.and(cb.isTrue(from.get(TravelEntry.DELETED)), cb.equal(from.get(AbstractDomainObject.UUID), travelEntryUuid)));
		cq.select(cb.count(from));
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	public TravelEntry getLastTravelEntry() {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<TravelEntry> query = cb.createQuery(TravelEntry.class);
		final Root<TravelEntry> from = query.from(TravelEntry.class);
		query.select(from);
		query.where(cb.and(createDefaultFilter(cb, from), cb.lessThanOrEqualTo(from.get(TravelEntry.CREATION_DATE), new Date())));
		query.orderBy(cb.desc(from.get(TravelEntry.CREATION_DATE)));

		final TypedQuery<TravelEntry> q = em.createQuery(query);
		return q.getResultList().stream().findFirst().orElse(null);
	}

	private Predicate buildCriteriaFilter(TravelEntryCriteria criteria, TravelEntryQueryContext travelEntryQueryContext) {

		final TravelEntryJoins joins = (TravelEntryJoins) travelEntryQueryContext.getJoins();
		final CriteriaBuilder cb = travelEntryQueryContext.getCriteriaBuilder();
		final From<?, TravelEntry> from = travelEntryQueryContext.getRoot();
		Join<TravelEntry, Person> person = joins.getPerson();
		Join<TravelEntry, Case> resultingCase = joins.getResultingCase();

		Predicate filter = null;

		if (Boolean.TRUE.equals(criteria.getOnlyRecoveredEntries())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isTrue(from.get(TravelEntry.RECOVERED)));
		}

		if (Boolean.TRUE.equals(criteria.getOnlyVaccinatedEntries())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isTrue(from.get(TravelEntry.VACCINATED)));
		}

		if (Boolean.TRUE.equals(criteria.getOnlyEntriesTestedNegative())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isTrue(from.get(TravelEntry.TESTED_NEGATIVE)));
		}

		if (Boolean.TRUE.equals(criteria.getOnlyEntriesConvertedToCase())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isNotNull(from.get(TravelEntry.RESULTING_CASE)));
		}

		if (criteria.getPerson() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(person.get(Person.UUID), criteria.getPerson().getUuid()));
		}

		if (criteria.getCase() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(resultingCase.get(Case.UUID), criteria.getCase().getUuid()));
		}

		if (criteria.getRelevanceStatus() != null) {
			if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper
					.and(cb, filter, cb.or(cb.equal(from.get(TravelEntry.ARCHIVED), false), cb.isNull(from.get(TravelEntry.ARCHIVED))));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(TravelEntry.ARCHIVED), true));
			}
		}

		if (criteria.getReportDateFrom() != null && criteria.getReportDateTo() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.between(from.get(TravelEntry.REPORT_DATE), criteria.getReportDateFrom(), criteria.getReportDateTo()));
		} else if (criteria.getReportDateFrom() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.greaterThanOrEqualTo(from.get(TravelEntry.REPORT_DATE), criteria.getReportDateFrom()));
		} else if (criteria.getReportDateTo() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.lessThanOrEqualTo(from.get(TravelEntry.REPORT_DATE), criteria.getReportDateTo()));
		}

		if (criteria.getDeleted() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(TravelEntry.DELETED), criteria.getDeleted()));
		}

		if (!DataHelper.isNullOrEmpty(criteria.getNameUuidExternalIDLike())) {
			Predicate likeFilters = CriteriaBuilderHelper.buildFreeTextSearchPredicate(
				cb,
				criteria.getNameUuidExternalIDLike(),
				textFilter -> cb.or(
					CriteriaBuilderHelper.unaccentedIlike(cb, person.get(Person.FIRST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, person.get(Person.LAST_NAME), textFilter),
					CriteriaBuilderHelper.ilike(cb, from.get(TravelEntry.UUID), textFilter),
					CriteriaBuilderHelper.ilike(cb, from.get(TravelEntry.EXTERNAL_ID), textFilter),
					CriteriaBuilderHelper.ilike(cb, person.get(Person.UUID), textFilter),
					CriteriaBuilderHelper.ilike(cb, person.get(Person.EXTERNAL_ID), textFilter)));
			filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
		}

		filter = CriteriaBuilderHelper.and(cb, filter, createDefaultFilter(cb, from));
		return filter;
	}
}
