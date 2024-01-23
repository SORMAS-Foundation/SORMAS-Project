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

package de.symeda.sormas.backend.task;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.api.utils.jurisdiction.JurisdictionValidator;
import de.symeda.sormas.backend.caze.CaseJurisdictionPredicateValidator;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.contact.ContactJurisdictionPredicateValidator;
import de.symeda.sormas.backend.contact.ContactQueryContext;
import de.symeda.sormas.backend.event.EventJurisdictionPredicateValidator;
import de.symeda.sormas.backend.event.EventQueryContext;
import de.symeda.sormas.backend.immunization.ImmunizationJurisdictionPredicateValidator;
import de.symeda.sormas.backend.travelentry.TravelEntryJurisdictionPredicateValidator;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;

public class TaskJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

	private final TaskJoins joins;

	private TaskJurisdictionPredicateValidator(
		CriteriaBuilder cb,
		TaskJoins joins,
		User user,
		List<PredicateJurisdictionValidator> associatedJurisdictionValidators) {
		super(cb, user, null, associatedJurisdictionValidators);
		this.joins = joins;
	}

	public static TaskJurisdictionPredicateValidator of(TaskQueryContext qc, User user) {
		final List<PredicateJurisdictionValidator> associatedJurisdictionValidators = new ArrayList<>();

		final CriteriaBuilder cb = qc.getCriteriaBuilder();
		final TaskJoins joins = qc.getJoins();

		associatedJurisdictionValidators
			.add(CaseJurisdictionPredicateValidator.of(new CaseQueryContext(cb, qc.getQuery(), joins.getCaseJoins()), user));
		associatedJurisdictionValidators
			.add(ContactJurisdictionPredicateValidator.of(new ContactQueryContext(cb, qc.getQuery(), joins.getContactJoins()), user));
		associatedJurisdictionValidators
			.add(EventJurisdictionPredicateValidator.of(new EventQueryContext(cb, qc.getQuery(), joins.getEventJoins()), user));

		return new TaskJurisdictionPredicateValidator(cb, joins, user, associatedJurisdictionValidators);
	}

	@Override
	public Predicate isRootInJurisdictionOrOwned() {
		final Predicate createdByCurrentUser = getCreatedByCurrentUser();
		final Predicate assignedToCurrentUser = getAssignedToCurrentUser();
		return cb.or(createdByCurrentUser, assignedToCurrentUser, isRootInJurisdiction());
	}

	private Predicate getAssignedToCurrentUser() {
		final Predicate assignedToCurrentUser =
			cb.and(cb.isNotNull(joins.getAssignee()), cb.equal(joins.getAssignee().get(User.UUID), user.getUuid()));
		return assignedToCurrentUser;
	}

	private Predicate getCreatedByCurrentUser() {
		return cb.and(cb.isNotNull(joins.getCreator()), cb.equal(joins.getCreator().get(User.UUID), user.getUuid()));
	}

	@Override
	public Predicate isRootInJurisdiction() {
		return isInJurisdictionByJurisdictionLevel(user.getJurisdictionLevel());
	}

	@Override
	public Predicate isRootInJurisdictionForRestrictedAccess() {
		final Predicate createdByCurrentUser = getCreatedByCurrentUser();
		final Predicate assignedToCurrentUser = getAssignedToCurrentUser();

		List<Predicate> associationPredicates = new ArrayList<>(associatedJurisdictionValidators.size());
		for (JurisdictionValidator<Predicate> associatedJurisdictionValidator : associatedJurisdictionValidators) {
				associationPredicates.add(associatedJurisdictionValidator.isRootInJurisdictionForRestrictedAccess());
		}
		return cb.or(createdByCurrentUser, assignedToCurrentUser, or(associationPredicates));
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
