/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.manualmessagelog;

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

    private PersonJurisdictionPredicateValidator personJurisdictionValidator;

    public ManualMessageJurisdictionPredicateValidator(
            CriteriaQuery<?> cq,
            CriteriaBuilder cb,
            User user,
            PersonJoins personJoins,
            Set<PersonAssociation> permittedPersonAssociations) {
        super(cb, user, null, null);
        personJurisdictionValidator = PersonJurisdictionPredicateValidator.of(cq, cb, personJoins, user, permittedPersonAssociations);
    }

    @Override
    public Predicate inJurisdictionOrOwned() {
        return personJurisdictionValidator.inJurisdictionOrOwned();
    }

    @Override
    public Predicate inJurisdiction() {
        return personJurisdictionValidator.inJurisdiction();
    }

    @Override
    public Predicate isRootInJurisdictionOrOwned() {
        throw new UnsupportedOperationException("Jurisdiction validation should woprk through person");
    }

	@Override
	public Predicate isRootInJurisdictionForRestrictedAccess() {
        throw new UnsupportedOperationException("Jurisdiction validation should work through person");
	}

    @Override
    protected Predicate whenNotAllowed() {
        throw new UnsupportedOperationException("Jurisdiction validation should work through person");
    }

    @Override
    protected Predicate whenNationalLevel() {
        throw new UnsupportedOperationException("Jurisdiction validation should woprk through person");
    }

    @Override
    protected Predicate whenRegionalLevel() {
        throw new UnsupportedOperationException("Jurisdiction validation should woprk through person");
    }

    @Override
    protected Predicate whenDistrictLevel() {
        throw new UnsupportedOperationException("Jurisdiction validation should woprk through person");
    }

    @Override
    protected Predicate whenCommunityLevel() {
        throw new UnsupportedOperationException("Jurisdiction validation should woprk through person");
    }

    @Override
    protected Predicate whenFacilityLevel() {
        throw new UnsupportedOperationException("Jurisdiction validation should woprk through person");
    }

    @Override
    protected Predicate whenPointOfEntryLevel() {
        throw new UnsupportedOperationException("Jurisdiction validation should woprk through person");
    }

    @Override
    protected Predicate whenLaboratoryLevel() {
        throw new UnsupportedOperationException("Jurisdiction validation should woprk through person");
    }
}
