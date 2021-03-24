/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.disease;

import java.util.Collections;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;

@Stateless
@LocalBean
public class DiseaseVariantService extends AdoServiceWithUserFilter<DiseaseVariant> {

    public DiseaseVariantService() {
        super(DiseaseVariant.class);
    }

    public List<DiseaseVariant> getAllByDisease(Disease disease) {
        if (disease == null || !disease.isVariantAllowed()) {
            return Collections.emptyList();
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DiseaseVariant> cq = cb.createQuery(DiseaseVariant.class);
        Root<DiseaseVariant> variantPath = cq.from(DiseaseVariant.class);

        ParameterExpression<Disease> diseaseParam = cb.parameter(Disease.class);
        cq.where(cb.equal(variantPath.get(DiseaseVariant.DISEASE), diseaseParam));

        cq.select(variantPath);
        cq.orderBy(cb.asc(variantPath.get(DiseaseVariant.NAME)));
        return em.createQuery(cq)
                .setParameter(diseaseParam, disease)
                .getResultList();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, DiseaseVariant> from) {
        return null;
    }

    public List<DiseaseVariant> getByName(String name, Disease disease) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DiseaseVariant> cq = cb.createQuery(getElementClass());
        Root<DiseaseVariant> from = cq.from(getElementClass());

        Predicate filter = cb.or(
                cb.equal(cb.trim(from.get(DiseaseVariant.NAME)), name.trim()),
                cb.equal(cb.lower(cb.trim(from.get(DiseaseVariant.NAME))), name.trim().toLowerCase()));
        if (disease != null) {
            filter = cb.and(filter, cb.equal(from.get(DiseaseVariant.DISEASE), disease));
        }

        cq.where(filter);
        return em.createQuery(cq).getResultList();
    }
}
