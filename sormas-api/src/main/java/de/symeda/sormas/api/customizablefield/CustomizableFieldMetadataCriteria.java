/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.api.customizablefield;

import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

/**
 * Criteria for filtering customizable field metadata in admin views.
 */
public class CustomizableFieldMetadataCriteria extends BaseCriteria {

    private static final long serialVersionUID = 1L;

    private String freeTextFilter;
    private CustomizableFieldContext contextClass;
    private CustomizableFieldType fieldType;
    private Boolean active;

    public String getFreeTextFilter() {
        return freeTextFilter;
    }

    public CustomizableFieldMetadataCriteria freeTextFilter(String freeTextFilter) {
        this.freeTextFilter = freeTextFilter;
        return this;
    }

    @IgnoreForUrl
    public CustomizableFieldContext getContextClass() {
        return contextClass;
    }

    public void setContextClass(CustomizableFieldContext contextClass) {
        this.contextClass = contextClass;
    }

    @IgnoreForUrl
    public CustomizableFieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(CustomizableFieldType fieldType) {
        this.fieldType = fieldType;
    }

    @IgnoreForUrl
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
