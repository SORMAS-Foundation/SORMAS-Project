/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.manualmessagelog;

import java.util.Arrays;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.api.person.PersonAssociation;
import de.symeda.sormas.backend.person.PersonJoins;
import de.symeda.sormas.backend.person.PersonJurisdictionPredicateValidator;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;

public class ManualMessageJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

    public ManualMessageJurisdictionPredicateValidator(
            CriteriaQuery<?> cq,
            CriteriaBuilder cb,
            User user,
            PersonJoins personJoins,
            Set<PersonAssociation> permittedPersonAssociations) {
        super(cb, user, null, Arrays.asList(PersonJurisdictionPredicateValidator.of(cq, cb, personJoins, user, permittedPersonAssociations)));
    }

    @Override
    public Predicate isRootInJurisdictionOrOwned() {
        return isRootInJurisdiction();
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
        return cb.conjunction();
    }

    @Override
    protected Predicate whenDistrictLevel() {
        return cb.conjunction();
    }

    @Override
    protected Predicate whenCommunityLevel() {
        return cb.conjunction();
    }

    @Override
    protected Predicate whenFacilityLevel() {
        return cb.conjunction();
    }

    @Override
    protected Predicate whenPointOfEntryLevel() {
        return cb.conjunction();
    }

    @Override
    protected Predicate whenLaboratoryLevel() {
        return cb.conjunction();
    }
}
