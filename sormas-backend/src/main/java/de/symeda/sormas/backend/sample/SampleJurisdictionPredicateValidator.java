/*
 *  SORMAS® - Surveillance Outbreak Response Management & Analysis System
 *  Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.symeda.sormas.backend.sample;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

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
			.add(CaseJurisdictionPredicateValidator.of(new CaseQueryContext(cb, qc.getQuery(), joins.getCaze()), user));
		associatedJurisdictionValidators
			.add(ContactJurisdictionPredicateValidator.of(new ContactQueryContext(cb, qc.getQuery(), joins.getContact()), user));
		associatedJurisdictionValidators.add(
			EventParticipantJurisdictionPredicateValidator
				.of(new EventParticipantQueryContext(cb, qc.getQuery(), joins.getEventParticipant()), user));

		return new SampleJurisdictionPredicateValidator(cb, joins, user, associatedJurisdictionValidators);
	}

	public static SampleJurisdictionPredicateValidator withoutAssociations(CriteriaBuilder cb, SampleJoins joins, User user) {
		return new SampleJurisdictionPredicateValidator(cb, joins, user, null);
	}

	public static SampleJurisdictionPredicateValidator withoutAssociations(CriteriaBuilder cb, SampleJoins joins, Path userPath) {
		return new SampleJurisdictionPredicateValidator(cb, joins, userPath, null);
	}

	@Override
	protected Predicate isInJurisdictionOrOwned() {
		final Predicate reportedByCurrentUser = cb.and(
			cb.isNotNull(joins.getReportingUser()),
			user != null
				? cb.equal(joins.getReportingUser().get(User.ID), user.getId())
				: cb.equal(joins.getReportingUser().get(User.ID), userPath.get(User.ID)));
		return cb.or(reportedByCurrentUser, isInJurisdiction());
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
			? cb.equal(joins.getLab().get(Facility.ID), user.getLaboratory().getId())
			: cb.equal(joins.getLab().get(Facility.ID), userPath.get(User.LABORATORY).get(Facility.ID));
	}
}
