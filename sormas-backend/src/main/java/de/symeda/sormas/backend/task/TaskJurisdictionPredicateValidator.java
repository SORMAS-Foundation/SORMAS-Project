package de.symeda.sormas.backend.task;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.api.utils.jurisdiction.JurisdictionValidator;
import de.symeda.sormas.backend.user.User;

public class TaskJurisdictionPredicateValidator extends JurisdictionValidator<Predicate> {

	private final CriteriaBuilder cb;
	private final TaskJoins joins;
	private final User currentUser;

	public static TaskJurisdictionPredicateValidator of(CriteriaBuilder cb, TaskJoins joins, User currentUser) {
		return new TaskJurisdictionPredicateValidator(cb, joins, currentUser);
	}

	private TaskJurisdictionPredicateValidator(CriteriaBuilder cb, TaskJoins joins, User currentUser) {
		this.cb = cb;
		this.joins = joins;
		this.currentUser = currentUser;
	}

	@Override
	protected Predicate whenNotAllowed() {
		return cb.disjunction();
	}

	@Override
	protected Predicate whenNationalLevel() {
		return cb.conjunction();
	}

	@Override
	protected Predicate whenRegionalLevel() {
		return cb.disjunction();
	}

	@Override
	protected Predicate whenDistrictLevel() {
		return cb.disjunction();
	}

	@Override
	protected Predicate whenCommunityLevel() {
		return cb.disjunction();
	}

	@Override
	protected Predicate whenFacilityLevel() {
		return cb.disjunction();
	}

	@Override
	protected Predicate whenPointOfEntryLevel() {
		return cb.disjunction();
	}

	@Override
	protected Predicate whenLaboratoryLevel() {
		return cb.disjunction();
	}

}
