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

package de.symeda.sormas.backend.sample;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.api.utils.jurisdiction.JurisdictionValidator;
import de.symeda.sormas.backend.caze.CaseJurisdictionPredicateValidator;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.contact.ContactJurisdictionPredicateValidator;
import de.symeda.sormas.backend.contact.ContactQueryContext;
import de.symeda.sormas.backend.event.EventParticipantJurisdictionPredicateValidator;
import de.symeda.sormas.backend.event.EventParticipantQueryContext;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;

public class SampleJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

	private final SampleJoins joins;

	private SampleJurisdictionPredicateValidator(
		CriteriaBuilder cb,
		SampleJoins joins,
		User user,
		List<PredicateJurisdictionValidator> associatedJurisdictionValidators) {
		super(cb, user, null, associatedJurisdictionValidators);
		this.joins = joins;
	}

	private SampleJurisdictionPredicateValidator(
		CriteriaBuilder cb,
		SampleJoins joins,
		Path userPath,
		List<PredicateJurisdictionValidator> associatedJurisdictionValidators) {
		super(cb, null, userPath, associatedJurisdictionValidators);
		this.joins = joins;
	}

	public static SampleJurisdictionPredicateValidator of(SampleQueryContext qc, User user) {
		final List<PredicateJurisdictionValidator> associatedJurisdictionValidators = new ArrayList<>();

		final CriteriaBuilder cb = qc.getCriteriaBuilder();
		final SampleJoins joins = qc.getJoins();

		associatedJurisdictionValidators
			.add(CaseJurisdictionPredicateValidator.of(new CaseQueryContext(cb, qc.getQuery(), joins.getCaseJoins()), user));
		associatedJurisdictionValidators
			.add(ContactJurisdictionPredicateValidator.of(new ContactQueryContext(cb, qc.getQuery(), joins.getContactJoins()), user));
		associatedJurisdictionValidators.add(
			EventParticipantJurisdictionPredicateValidator
				.of(new EventParticipantQueryContext(cb, qc.getQuery(), joins.getEventParticipantJoins()), user));

		return new SampleJurisdictionPredicateValidator(cb, joins, user, associatedJurisdictionValidators);
	}

	public static SampleJurisdictionPredicateValidator withoutAssociations(CriteriaBuilder cb, SampleJoins joins, User user) {
		return new SampleJurisdictionPredicateValidator(cb, joins, user, null);
	}

	public static SampleJurisdictionPredicateValidator withoutAssociations(CriteriaBuilder cb, SampleJoins joins, Path userPath) {
		return new SampleJurisdictionPredicateValidator(cb, joins, userPath, null);
	}

	@Override
	public Predicate isRootInJurisdictionOrOwned() {
		final Predicate reportedByCurrentUser = cb.and(
			cb.isNotNull(joins.getRoot().get(Sample.REPORTING_USER)),
			user != null
				? cb.equal(joins.getRoot().get(Sample.REPORTING_USER).get(User.ID), user.getId())
				: cb.equal(joins.getRoot().get(Sample.REPORTING_USER).get(User.ID), userPath.get(User.ID)));
		return cb.or(reportedByCurrentUser, isRootInJurisdiction());
	}

	@Override
	public Predicate inJurisdictionOrOwned() {
//		Predicate rootInJurisdictionOrOwned = isRootInJurisdictionOrOwned();
		Predicate rootInJurisdictionOrOwned = isRootAccessible();
		Predicate rootHasLimitedDisease = hasUserLimitedDisease();
		if (associatedJurisdictionValidators != null && !associatedJurisdictionValidators.isEmpty()) {
			final List<Predicate> jurisdictionTypes = new ArrayList<>();
			final List<Predicate> diseaseJurisdictionTypes = new ArrayList<>();
			jurisdictionTypes.add(rootInJurisdictionOrOwned);
			diseaseJurisdictionTypes.add(rootHasLimitedDisease);
			for (JurisdictionValidator<Predicate> jurisdictionValidator : associatedJurisdictionValidators) {
				if (jurisdictionValidator != null) {
					Predicate associatedInJurisdictionOrOwned = jurisdictionValidator.isRootAccessible();
					Predicate associatedHasLimitedDisease = jurisdictionValidator.hasUserLimitedDisease();
					jurisdictionTypes.add(associatedInJurisdictionOrOwned);
					diseaseJurisdictionTypes.add(associatedHasLimitedDisease);
				}
			}
			return and(or(jurisdictionTypes), or(diseaseJurisdictionTypes));
		} else {
			return and(rootInJurisdictionOrOwned, rootHasLimitedDisease);
		}
	}

	public Predicate inJurisdiction() {
		Predicate rootInJurisdiction = isRootInJurisdiction();
		Predicate rootHasLimitedDisease = hasUserLimitedDisease();
		if (associatedJurisdictionValidators != null && !associatedJurisdictionValidators.isEmpty()) {
			final List<Predicate> jurisdictionTypes = new ArrayList<>();
			final List<Predicate> diseaseJurisdictionTypes = new ArrayList<>();
			jurisdictionTypes.add(rootInJurisdiction);
			diseaseJurisdictionTypes.add(rootHasLimitedDisease);
			for (JurisdictionValidator<Predicate> jurisdictionValidator : associatedJurisdictionValidators) {
				if (jurisdictionValidator != null) {
					Predicate associatedInJurisdiction = jurisdictionValidator.isRootInJurisdiction();
					Predicate associatedHasLimitedDisease = jurisdictionValidator.hasUserLimitedDisease();
					jurisdictionTypes.add(associatedInJurisdiction);
					diseaseJurisdictionTypes.add(associatedHasLimitedDisease);
				}
			}
			return and(or(jurisdictionTypes), or(diseaseJurisdictionTypes));
		} else {
			return and(rootInJurisdiction, rootHasLimitedDisease);
		}
	}

	@Override
	public Predicate isRootInJurisdictionForRestrictedAccess() {
		List<Predicate> associationPredicates = new ArrayList<>(associatedJurisdictionValidators.size());
		for (JurisdictionValidator<Predicate> associatedJurisdictionValidator : associatedJurisdictionValidators) {
			associationPredicates.add(associatedJurisdictionValidator.isRootInJurisdictionForRestrictedAccess());
		}
		return or(associationPredicates);
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
		return user != null
			? cb.equal(joins.getRoot().get(Sample.LAB).get(Facility.ID), user.getLaboratory().getId())
			: cb.equal(joins.getRoot().get(Sample.LAB).get(Facility.ID), userPath.get(User.LABORATORY).get(Facility.ID));
	}
}
