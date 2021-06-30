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

package de.symeda.sormas.backend.immunization;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.JurisdictionHelper;

@Stateless
@LocalBean
public class ImmunizationService extends AbstractCoreAdoService<Immunization> {

    @EJB
    private UserService userService;

    public ImmunizationService() {
        super(Immunization.class);
    }

    public boolean inJurisdictionOrOwned(Immunization immunization) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Boolean> cq = cb.createQuery(Boolean.class);
        Root<Immunization> root = cq.from(Immunization.class);
        cq.multiselect(JurisdictionHelper.booleanSelector(cb, inJurisdictionOrOwned(new ImmunizationQueryContext(cb, cq, root))));
        cq.where(cb.equal(root.get(Immunization.UUID), immunization.getUuid()));
        return em.createQuery(cq).getSingleResult();
    }

    public Predicate inJurisdictionOrOwned(ImmunizationQueryContext qc) {
        final User currentUser = userService.getCurrentUser();
        return ImmunizationJurisdictionPredicateValidator.of(qc, currentUser).inJurisdictionOrOwned();
    }

    @Override
    public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Immunization> from) {
        return null;
    }

    public Predicate buildCriteriaFilter(ImmunizationCriteria criteria, CriteriaBuilder cb, Root<Immunization> from) {
        return cb.conjunction();
    }
}
