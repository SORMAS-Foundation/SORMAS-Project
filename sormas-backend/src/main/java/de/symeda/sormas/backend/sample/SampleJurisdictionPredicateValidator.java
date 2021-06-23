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
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;
import de.symeda.sormas.backend.caze.CaseJurisdictionPredicateValidator;
import de.symeda.sormas.backend.contact.ContactJoins;
import de.symeda.sormas.backend.contact.ContactJurisdictionPredicateValidator;
import de.symeda.sormas.backend.event.EventParticipantJurisdictionPredicateValidator;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.utils.CaseJoins;
import de.symeda.sormas.utils.EventParticipantJoins;

public class SampleJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

    private final SampleJoins<?> joins;
    private final User currentUser;

    private SampleJurisdictionPredicateValidator(CriteriaBuilder cb, SampleJoins<?> joins, User currentUser, List<PredicateJurisdictionValidator> associatedJurisdictionValidators) {
        super(cb, associatedJurisdictionValidators);
        this.joins = joins;
        this.currentUser = currentUser;
    }

	public static SampleJurisdictionPredicateValidator of(CriteriaBuilder cb, SampleJoins<?> joins, User currentUser) {
		final List<PredicateJurisdictionValidator> associatedJurisdictionValidators = new ArrayList<>();

		associatedJurisdictionValidators.add(CaseJurisdictionPredicateValidator.of(cb, new CaseJoins<>(joins.getCaze()), currentUser));
		associatedJurisdictionValidators.add(ContactJurisdictionPredicateValidator.of(cb, new ContactJoins<>(joins.getContact()), currentUser));
		associatedJurisdictionValidators
			.add(EventParticipantJurisdictionPredicateValidator.of(cb, new EventParticipantJoins(joins.getEventParticipant()), currentUser));

		return new SampleJurisdictionPredicateValidator(cb, joins, currentUser, associatedJurisdictionValidators);
	}

	@Override
	protected Predicate isInJurisdictionOrOwned() {
		final Predicate reportedByCurrentUser =
			cb.and(cb.isNotNull(joins.getReportingUser()), cb.equal(joins.getReportingUser().get(User.UUID), currentUser.getUuid()));
		return cb.or(reportedByCurrentUser, isInJurisdiction());
	}

    @Override
    protected Predicate isInJurisdiction() {
        return isInJurisdictionByJurisdictionLevel(currentUser.getJurisdictionLevel());
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
        return cb.equal(joins.getLab().get(Facility.ID), currentUser.getLaboratory().getId());
    }
}
