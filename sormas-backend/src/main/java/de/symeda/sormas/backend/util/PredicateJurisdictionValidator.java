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

package de.symeda.sormas.backend.util;

import de.symeda.sormas.api.utils.jurisdiction.JurisdictionValidator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.List;

public abstract class PredicateJurisdictionValidator extends JurisdictionValidator<Predicate> {

    protected final CriteriaBuilder cb;

    public PredicateJurisdictionValidator(CriteriaBuilder cb, List<PredicateJurisdictionValidator> jurisdictionValidators) {
        super(jurisdictionValidators);
        this.cb = cb;
    }

    @Override
    protected Predicate or(List<Predicate> jurisdictionTypes) {
        return cb.or(jurisdictionTypes.toArray(new Predicate[jurisdictionTypes.size()]));
    }
}
