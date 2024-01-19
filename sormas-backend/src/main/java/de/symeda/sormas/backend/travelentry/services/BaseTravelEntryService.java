package de.symeda.sormas.backend.travelentry.services;

import javax.ejb.EJB;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.deletionconfiguration.DeletionReference;
import de.symeda.sormas.api.person.PersonAssociation;
import de.symeda.sormas.api.person.PersonCriteria;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.ChangeDateBuilder;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.person.PersonQueryContext;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.travelentry.TravelEntryJoins;
import de.symeda.sormas.backend.travelentry.TravelEntryJurisdictionPredicateValidator;
import de.symeda.sormas.backend.travelentry.TravelEntryQueryContext;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;

public abstract class BaseTravelEntryService extends AbstractCoreAdoService<TravelEntry, TravelEntryJoins> {

	@EJB
	protected UserService userService;
	@EJB
	private PersonService personService;

	protected BaseTravelEntryService() {
		super(TravelEntry.class, DeletableEntityType.TRAVEL_ENTRY);
	}

	@Override
	public Predicate inJurisdictionOrOwned(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, TravelEntry> from) {
		return inJurisdictionOrOwned(new TravelEntryQueryContext(cb, query, from));
	}

	public Predicate inJurisdictionOrOwned(TravelEntryQueryContext qc) {
		return inJurisdictionOrOwned(qc, userService.getCurrentUser());
	}

	public Predicate inJurisdictionOrOwned(TravelEntryQueryContext qc, User user) {
		return TravelEntryJurisdictionPredicateValidator.of(qc, user).inJurisdictionOrOwned();
	}

	public Predicate createDefaultFilter(CriteriaBuilder cb, From<?, TravelEntry> root) {
		return cb.isFalse(root.get(TravelEntry.DELETED));
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Predicate createUserFilterInternal(CriteriaBuilder cb, CriteriaQuery cq, From<?, TravelEntry> from) {
		return createUserFilter(new TravelEntryQueryContext(cb, cq, from));
	}

	public Predicate createUserFilter(TravelEntryQueryContext qc) {
		return createUserFilter(qc, true);
	}

	public Predicate createUserFilter(TravelEntryQueryContext qc, boolean checkJurisdictionAndLimitedDisease) {
		User currentUser = getCurrentUser();
		if (currentUser == null) {
			return null;
		}
		final CriteriaBuilder cb = qc.getCriteriaBuilder();
		Predicate filter = null;
		if (checkJurisdictionAndLimitedDisease) {
			filter = inJurisdictionOrOwned(qc);
			filter = CriteriaBuilderHelper
				.and(cb, filter, CriteriaBuilderHelper.limitedDiseasePredicate(cb, currentUser, qc.getRoot().get(TravelEntry.DISEASE)));
		}
		if (isRestrictedToAssignedEntities()) {
			final PersonQueryContext personQueryContext = new PersonQueryContext(cb, qc.getQuery(), qc.getJoins().getPersonJoins());

			final PersonCriteria personCaseCriteria = new PersonCriteria();
			personCaseCriteria.setPersonAssociation(PersonAssociation.CASE);
			final Predicate personCaseUserFilter = personService.createUserFilter(personQueryContext, personCaseCriteria);

			final PersonCriteria personContactCriteria = new PersonCriteria();
			personContactCriteria.setPersonAssociation(PersonAssociation.CONTACT);
			final Predicate personContactUserFilter = personService.createUserFilter(personQueryContext, personContactCriteria);

			final PersonCriteria personEventParticipantCriteria = new PersonCriteria();
			personEventParticipantCriteria.setPersonAssociation(PersonAssociation.EVENT_PARTICIPANT);
			final Predicate personEventParticipantUserFilter = personService.createUserFilter(personQueryContext, personEventParticipantCriteria);

			filter = CriteriaBuilderHelper.and(cb, filter, cb.or(personCaseUserFilter, personContactUserFilter, personEventParticipantUserFilter));
		}
		return filter;
	}

	@Override
	protected TravelEntryJoins toJoins(From<?, TravelEntry> adoPath) {
		return new TravelEntryJoins(adoPath);
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Predicate createRelevantDataFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, TravelEntry> from) {

		Predicate filter = createDefaultFilter(cb, from);
		if (getCurrentUser() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, createUserFilterInternal(cb, cq, from));
		}

		return filter;
	}

	@Override
	protected <T extends ChangeDateBuilder<T>> T addChangeDates(T builder, TravelEntryJoins joins, boolean includeExtendedChangeDateFilters) {

		return super.addChangeDates(builder, joins, includeExtendedChangeDateFilters);
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
}
