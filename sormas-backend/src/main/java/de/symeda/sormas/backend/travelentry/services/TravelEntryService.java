package de.symeda.sormas.backend.travelentry.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.collections4.CollectionUtils;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryIndexDto;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
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
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless
@LocalBean
public class TravelEntryService extends BaseTravelEntryService {

	@EJB
	private TaskService taskService;
	@EJB
	private DocumentService documentService;

	public List<TravelEntry> getByPersonUuids(List<String> personUuids) {

		List<TravelEntry> travelEntries = new LinkedList<>();
		IterableHelper.executeBatched(personUuids, ModelConstants.PARAMETER_LIMIT, batchedPersonUuids -> {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TravelEntry> cq = cb.createQuery(TravelEntry.class);
			Root<TravelEntry> travelEntryRoot = cq.from(TravelEntry.class);
			Join<TravelEntry, Person> personJoin = travelEntryRoot.join(TravelEntry.PERSON, JoinType.INNER);

			cq.where(personJoin.get(AbstractDomainObject.UUID).in(batchedPersonUuids));

			travelEntries.addAll(em.createQuery(cq).getResultList());
		});
		return travelEntries;
	}

	public List<TravelEntryIndexDto> getIndexList(TravelEntryCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		List<Long> indexListIds = getIndexListIds(criteria, first, max, sortProperties);

		List<TravelEntryIndexDto> travelEntries = new ArrayList<>();

		IterableHelper.executeBatched(indexListIds, ModelConstants.PARAMETER_LIMIT, batchedIds -> {
			final CriteriaBuilder cb = em.getCriteriaBuilder();
			final CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			final Root<TravelEntry> travelEntry = cq.from(TravelEntry.class);

			TravelEntryQueryContext travelEntryQueryContext = new TravelEntryQueryContext(cb, cq, travelEntry);
			TravelEntryJoins joins = travelEntryQueryContext.getJoins();

			final Join<TravelEntry, Person> person = joins.getPerson();
			final Join<TravelEntry, PointOfEntry> pointOfEntry = joins.getPointOfEntry();

			final Join<Location, District> district = joins.getPersonJoins().getAddressJoins().getDistrict();

			cq.multiselect(
				Stream
					.concat(
						Stream.of(
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
							travelEntry.get(TravelEntry.DELETION_REASON),
							travelEntry.get(TravelEntry.OTHER_DELETION_REASON),
							JurisdictionHelper.booleanSelector(cb, inJurisdictionOrOwned(travelEntryQueryContext)),
							travelEntry.get(TravelEntry.CHANGE_DATE)),
						// add sorting properties to the select clause
						sortBy(sortProperties, travelEntryQueryContext).stream())
					.collect(Collectors.toList()));

			Predicate filter = travelEntry.get(TravelEntry.ID).in(batchedIds);

			filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(travelEntryQueryContext));
			if (criteria != null) {
				final Predicate criteriaFilter = buildCriteriaFilter(criteria, travelEntryQueryContext);
				filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
			}

			if (filter != null) {
				cq.where(filter);
			}

			cq.distinct(true);

			travelEntries.addAll(QueryHelper.getResultList(em, cq, new TravelEntryIndexDtoResultTransformer(), null, null));
		});

		return travelEntries;
	}

	private List<Long> getIndexListIds(TravelEntryCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		final Root<TravelEntry> travelEntry = cq.from(TravelEntry.class);

		TravelEntryQueryContext travelEntryQueryContext = new TravelEntryQueryContext(cb, cq, travelEntry);

		List<Selection<?>> selections = new ArrayList<>();
		selections.add(travelEntry.get(Person.ID));
		selections.addAll(sortBy(sortProperties, travelEntryQueryContext));

		cq.multiselect(selections);

		Predicate filter = createUserFilter(travelEntryQueryContext);
		if (criteria != null) {
			final Predicate criteriaFilter = buildCriteriaFilter(criteria, travelEntryQueryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}
		cq.distinct(true);

		List<Tuple> travelEntries = QueryHelper.getResultList(em, cq, first, max);
		return travelEntries.stream().map(t -> t.get(0, Long.class)).collect(Collectors.toList());
	}

	private List<Selection<?>> sortBy(List<SortProperty> sortProperties, TravelEntryQueryContext travelEntryQueryContext) {

		List<Selection<?>> selections = new ArrayList<>();
		CriteriaBuilder cb = travelEntryQueryContext.getCriteriaBuilder();
		CriteriaQuery<?> cq = travelEntryQueryContext.getQuery();

		TravelEntryJoins travelEntryJoins = travelEntryQueryContext.getJoins();
		Join<TravelEntry, Person> person = travelEntryJoins.getPerson();
		final Join<Location, District> district = travelEntryJoins.getPersonJoins().getAddressJoins().getDistrict();

		if (CollectionUtils.isNotEmpty(sortProperties)) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case TravelEntryIndexDto.UUID:
				case TravelEntryIndexDto.RECOVERED:
				case TravelEntryIndexDto.VACCINATED:
				case TravelEntryIndexDto.TESTED_NEGATIVE:
				case TravelEntryIndexDto.QUARANTINE_TO:
					expression = travelEntryJoins.getRoot().get(sortProperty.propertyName);
					break;
				case TravelEntryIndexDto.EXTERNAL_ID:
					expression = cb.lower(travelEntryJoins.getRoot().get(sortProperty.propertyName));
					break;
				case TravelEntryIndexDto.PERSON_FIRST_NAME:
					expression = cb.lower(person.get(Person.FIRST_NAME));
					break;
				case TravelEntryIndexDto.PERSON_LAST_NAME:
					expression = cb.lower(person.get(Person.LAST_NAME));
					break;
				case TravelEntryIndexDto.HOME_DISTRICT_NAME:
					expression = cb.lower(district.get(District.NAME));
					break;
				case TravelEntryIndexDto.POINT_OF_ENTRY_NAME:
					expression = cb.lower(travelEntryJoins.getPointOfEntry().get(PointOfEntry.NAME));
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
				selections.add(expression);
			}
			cq.orderBy(order);
		} else {
			Path<Object> changeDate = travelEntryJoins.getRoot().get(TravelEntry.CHANGE_DATE);
			cq.orderBy(cb.desc(changeDate));
			selections.add(changeDate);
		}
		return selections;
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

	public Predicate createActiveTravelEntriesFilter(CriteriaBuilder cb, From<?, TravelEntry> root) {
		return cb.and(cb.isFalse(root.get(TravelEntry.ARCHIVED)), cb.isFalse(root.get(TravelEntry.DELETED)));
	}

	public List<TravelEntry> getAllByResultingCase(Case caze) {
		return getAllByResultingCase(caze, false);
	}

	public List<TravelEntry> getAllByResultingCase(Case caze, boolean includeDeleted) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TravelEntry> cq = cb.createQuery(getElementClass());
		Root<TravelEntry> from = cq.from(getElementClass());

		Predicate filter = includeDeleted ? null : createDefaultFilter(cb, from);
		filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(TravelEntry.RESULTING_CASE), caze));
		cq.where(filter);

		cq.orderBy(cb.desc(from.get(TravelEntry.REPORT_DATE)));

		return em.createQuery(cq).getResultList();
	}

	@Override
	public EditPermissionType getEditPermissionType(TravelEntry travelEntry) {

		if (!inJurisdictionOrOwned(travelEntry)) {
			return EditPermissionType.OUTSIDE_JURISDICTION;
		}

		return super.getEditPermissionType(travelEntry);
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

	public TravelEntry getLastTravelEntry() {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<TravelEntry> query = cb.createQuery(TravelEntry.class);
		final Root<TravelEntry> from = query.from(TravelEntry.class);
		query.select(from);
		query.where(cb.and(createDefaultFilter(cb, from), cb.lessThanOrEqualTo(from.get(TravelEntry.CREATION_DATE), new Date())));
		query.orderBy(cb.desc(from.get(TravelEntry.CREATION_DATE)));

		return QueryHelper.getFirstResult(em, query);
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
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.DELETED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(TravelEntry.DELETED), true));
			}
		}
		if (criteria.getRelevanceStatus() != EntityRelevanceStatus.DELETED) {
			filter = CriteriaBuilderHelper.and(cb, filter, createDefaultFilter(cb, from));
		}

		if (criteria.getReportDateFrom() != null && criteria.getReportDateTo() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.between(from.get(TravelEntry.REPORT_DATE), criteria.getReportDateFrom(), criteria.getReportDateTo()));
		} else if (criteria.getReportDateFrom() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.greaterThanOrEqualTo(from.get(TravelEntry.REPORT_DATE), criteria.getReportDateFrom()));
		} else if (criteria.getReportDateTo() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.lessThanOrEqualTo(from.get(TravelEntry.REPORT_DATE), criteria.getReportDateTo()));
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

		return filter;
	}
}
