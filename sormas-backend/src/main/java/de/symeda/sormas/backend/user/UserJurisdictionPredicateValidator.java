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

package de.symeda.sormas.backend.user;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;

public class UserJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

    private final UserJoins joins;

    public UserJurisdictionPredicateValidator(CriteriaBuilder cb, User user, Path<User> userPath, UserJoins joins) {
        super(cb, user, userPath, null);
        this.joins = joins;
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
        return user != null
                ? cb.equal(joins.getRoot().get(User.DISTRICT).get(District.ID), user.getDistrict().getId())
                : cb.equal(joins.getRoot().get(User.DISTRICT).get(District.ID), userPath.get(User.DISTRICT).get(District.ID));
    }

    @Override
    protected Predicate whenCommunityLevel() {
        return user != null
                ? cb.equal(joins.getRoot().get(User.COMMUNITY).get(Community.ID), user.getCommunity().getId())
                : cb.equal(joins.getRoot().get(User.COMMUNITY).get(Community.ID), userPath.get(User.COMMUNITY).get(Community.ID));

    }

    @Override
    protected Predicate whenFacilityLevel() {
        return user != null
                ? cb.equal(joins.getRoot().get(User.HEALTH_FACILITY).get(Facility.ID), user.getHealthFacility().getId())
                : cb.equal(joins.getRoot().get(User.HEALTH_FACILITY).get(Facility.ID), userPath.get(User.HEALTH_FACILITY).get(Facility.ID));
    }

    @Override
    protected Predicate whenPointOfEntryLevel() {
        return user != null
                ? cb.equal(joins.getRoot().get(User.POINT_OF_ENTRY).get(PointOfEntry.ID), user.getPointOfEntry().getId())
                : cb.equal(joins.getRoot().get(User.POINT_OF_ENTRY).get(PointOfEntry.ID), userPath.get(User.POINT_OF_ENTRY).get(PointOfEntry.ID));
    }

    @Override
    protected Predicate whenLaboratoryLevel() {
        return user != null
                ? cb.equal(joins.getRoot().get(User.LABORATORY).get(Facility.ID), user.getLaboratory().getId())
                : cb.equal(joins.getRoot().get(User.LABORATORY).get(Facility.ID), userPath.get(User.LABORATORY).get(Facility.ID));
    }
}
