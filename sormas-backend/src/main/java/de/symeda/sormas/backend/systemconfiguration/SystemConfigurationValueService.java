/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.systemconfiguration;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueCriteria;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;

/**
 * Service class for managing system configuration values.
 */
@Stateless
@LocalBean
public class SystemConfigurationValueService extends AdoServiceWithUserFilterAndJurisdiction<SystemConfigurationValue> {

    /**
     * Default constructor.
     */
    public SystemConfigurationValueService() {
        this(SystemConfigurationValue.class);
    }

    /**
     * Constructor with element class.
     *
     * @param elementClass
     *            the class of the element
     */
    protected SystemConfigurationValueService(Class<SystemConfigurationValue> elementClass) {
        super(elementClass);
    }

    /**
     * Creates a user filter predicate.
     *
     * @param cb
     *            the criteria builder
     * @param cq
     *            the criteria query
     * @param from
     *            the from clause
     * @return the predicate
     */
    @Override
    public Predicate createUserFilter(CriteriaBuilder cb, @SuppressWarnings("rawtypes") CriteriaQuery cq, From<?, SystemConfigurationValue> from) {
        return null;
    }

    /**
     * Builds a criteria filter based on the provided criteria.
     *
     * @param criteria
     *            the criteria
     * @param cb
     *            the criteria builder
     * @param from
     *            the root
     * @return the predicate
     */
    public Predicate buildCriteriaFilter(
        SystemConfigurationValueCriteria criteria,
        CriteriaBuilder cb,
        Root<SystemConfigurationValue> from,
        SystemConfigurationValueJoins joins) {
        Predicate filter = cb.conjunction();

        if (criteria.getFreeTextFilter() != null) {
            String[] textFilters = criteria.getFreeTextFilter().split("\\s+");
            for (String textFilter : textFilters) {
                if (DataHelper.isNullOrEmpty(textFilter)) {
                    continue;
                }

                Predicate likeFilters = cb.or(
                    CriteriaBuilderHelper.unaccentedIlike(cb, from.get(SystemConfigurationValue.KEY_FIELD_NAME), textFilter),
                    CriteriaBuilderHelper.unaccentedIlike(cb, from.get(SystemConfigurationValue.VALUE_FIELD_NAME), textFilter));
                filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
            }
        }

        if (criteria.getCategory() != null) {
            filter =
                CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getCategory().get(AbstractDomainObject.UUID), criteria.getCategory().getUuid()));
        }

        return filter;
    }
}
