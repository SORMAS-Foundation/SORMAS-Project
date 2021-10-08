package de.symeda.sormas.backend.travelentry;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.JurisdictionHelper;

@Stateless
@LocalBean
public class TravelEntryService extends AbstractCoreAdoService<TravelEntry> {

	@EJB
	private UserService userService;
	@EJB
	private TaskService taskService;

	public TravelEntryService() {
		super(TravelEntry.class);
	}

	public boolean inJurisdictionOrOwned(TravelEntry travelEntry) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Boolean> cq = cb.createQuery(Boolean.class);
		Root<TravelEntry> root = cq.from(TravelEntry.class);
		cq.multiselect(JurisdictionHelper.booleanSelector(cb, inJurisdictionOrOwned(new TravelEntryQueryContext(cb, cq, root))));
		cq.where(cb.equal(root.get(TravelEntry.UUID), travelEntry.getUuid()));
		return em.createQuery(cq).getSingleResult();
	}

	public Predicate inJurisdictionOrOwned(TravelEntryQueryContext qc) {
		final User currentUser = userService.getCurrentUser();
		return TravelEntryJurisdictionPredicateValidator.of(qc, currentUser).inJurisdictionOrOwned();
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, TravelEntry> travelEntryPath) {
		return inJurisdictionOrOwned(new TravelEntryQueryContext(cb, cq, travelEntryPath));
	}

	public Predicate createUserFilter(TravelEntryQueryContext travelEntryQueryContext) {
		return inJurisdictionOrOwned(travelEntryQueryContext);
	}

	public Predicate createActiveTravelEntriesFilter(CriteriaBuilder cb, From<?, TravelEntry> root) {
		return cb.and(cb.isFalse(root.get(TravelEntry.ARCHIVED)), cb.isFalse(root.get(TravelEntry.DELETED)));
	}

	public Predicate createDefaultFilter(CriteriaBuilder cb, From<?, TravelEntry> root) {
		return cb.isFalse(root.get(TravelEntry.DELETED));
	}

	public Predicate buildCriteriaFilter(TravelEntryCriteria criteria, TravelEntryQueryContext travelEntryQueryContext) {

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

	public List<TravelEntry> getAllActiveAfter(Date date) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TravelEntry> cq = cb.createQuery(getElementClass());
		Root<TravelEntry> from = cq.from(getElementClass());

		Predicate filter = createDefaultFilter(cb, from);

		if (getCurrentUser() != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			if (userFilter != null) {
				filter = cb.and(filter, userFilter);
			}
		}

		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, DateHelper.toTimestampUpper(date));
			if (dateFilter != null) {
				filter = cb.and(filter, dateFilter);
			}
		}
		cq.where(filter);
		cq.orderBy(cb.desc(from.get(TravelEntry.CHANGE_DATE)));
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
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
	public void delete(TravelEntry travelEntry) {

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
			taskService.delete(task);
		}

		// Mark the travel entry as deleted
		super.delete(travelEntry);
	}
}
