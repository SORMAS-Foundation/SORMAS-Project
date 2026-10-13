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

package de.symeda.sormas.api.systemconfiguration;

import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

/**
 * Criteria class for filtering SystemConfigurationValue entities.
 */
public class SystemConfigurationValueCriteria extends BaseCriteria {

    private static final long serialVersionUID = 1L;

    public static final String FREE_TEXT_PROPERTY_NAME = "freeText";
    public static final String CATEGORY_PROPERTY_NAME = "category";

    private String freeTextFilter;

    private SystemConfigurationCategoryReferenceDto category;

    /**
     * Get the free text filter.
     *
     * @return the free text filter
     */
    public String getFreeTextFilter() {
        return freeTextFilter;
    }

    /**
     * Set the free text filter.
     *
     * @param freeTextFilter
     *            the free text filter to set
     * @return the updated criteria
     */
    public SystemConfigurationValueCriteria freeTextFilter(final String freeTextFilter) {
        this.freeTextFilter = freeTextFilter;
        return this;
    }

    /**
     * Get the category filter.
     *
     * @return the category filter
     */
    @IgnoreForUrl
    public SystemConfigurationCategoryReferenceDto getCategory() {
        return category;
    }

    /**
     * Set the category filter.
     *
     * @param category
     *            the category filter to set
     */
    public void setCategory(final SystemConfigurationCategoryReferenceDto category) {
        this.category = category;
    }
}
