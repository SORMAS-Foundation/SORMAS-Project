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

package de.symeda.sormas.backend.person;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.caze.CaseJurisdictionPredicateValidator;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.contact.ContactJurisdictionPredicateValidator;
import de.symeda.sormas.backend.contact.ContactQueryContext;
import de.symeda.sormas.backend.event.EventParticipantJurisdictionPredicateValidator;
import de.symeda.sormas.backend.event.EventParticipantQueryContext;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;

public class PersonJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

    private final PersonJoins joins;
    private final User currentUser;

    private PersonJurisdictionPredicateValidator(CriteriaBuilder cb, PersonJoins joins, User currentUser, List<PredicateJurisdictionValidator> associatedJurisdictionValidators) {
        super(cb, associatedJurisdictionValidators);
        this.joins = joins;
        this.currentUser = currentUser;
    }

	public static PersonJurisdictionPredicateValidator of(CriteriaQuery<?> cq, CriteriaBuilder cb, PersonJoins joins , User currentUser) {
		final List<PredicateJurisdictionValidator> associatedJurisdictionValidators = new ArrayList<>();

		associatedJurisdictionValidators.add(CaseJurisdictionPredicateValidator.of(new CaseQueryContext(cb, cq, joins.getCaze()), currentUser));
		associatedJurisdictionValidators.add(ContactJurisdictionPredicateValidator.of(new ContactQueryContext<>(cb, cq, joins.getContact()), currentUser));
		associatedJurisdictionValidators
			.add(EventParticipantJurisdictionPredicateValidator.of(new EventParticipantQueryContext<>(cb, cq, joins.getEventParticipant()), currentUser));

		return new PersonJurisdictionPredicateValidator(cb, joins, currentUser, associatedJurisdictionValidators);
	}

	@Override
	protected Predicate isInJurisdictionOrOwned() {
		return isInJurisdiction();
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
        return cb.disjunction();
    }
}
