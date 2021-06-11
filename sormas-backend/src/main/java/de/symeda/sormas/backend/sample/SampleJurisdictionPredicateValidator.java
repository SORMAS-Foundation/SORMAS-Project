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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.api.utils.jurisdiction.JurisdictionValidator;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.user.User;

public class SampleJurisdictionPredicateValidator extends JurisdictionValidator<Predicate> {

    private final CriteriaBuilder cb;
    private final SampleJoins<?> joins;
    private final User currentUser;

    private SampleJurisdictionPredicateValidator(CriteriaBuilder cb, SampleJoins<?> joins, User currentUser) {
        this.cb = cb;
        this.joins = joins;
        this.currentUser = currentUser;
    }

    public static SampleJurisdictionPredicateValidator of(CriteriaBuilder cb, SampleJoins<?> joins, User currentUser) {
        return new SampleJurisdictionPredicateValidator(cb, joins, currentUser);
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
