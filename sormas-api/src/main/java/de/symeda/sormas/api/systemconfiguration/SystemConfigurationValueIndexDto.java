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

import de.symeda.sormas.api.EntityDto;

public class SystemConfigurationValueIndexDto extends EntityDto {

    public static final String I18N_PREFIX = "SystemConfigurationValue";

    private static final long serialVersionUID = 1L;

    public static final String KEY_PROPERTY_NAME = "key";
    public static final String VALUE_PROPERTY_NAME = "value";
    public static final String ENCRYPTED_PROPERTY_NAME = "encrypted";
    public static final String CATEGORY_NAME_PROPERTY_NAME = "categoryName";
    public static final String CATEGORY_CAPTION_PROPERTY_NAME = "categoryCaption";
    public static final String CATEGORY_DESCRIPTION_PROPERTY_NAME = "categoryDescription";

    private String value;
    private String key;
    private boolean encrypted;
    private String categoryName;
    private String categoryCaption;
    private String categoryDescription;

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(final boolean encrypted) {
        this.encrypted = encrypted;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(final String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryCaption() {
        return categoryCaption;
    }

    public void setCategoryCaption(final String categoryCaption) {
        this.categoryCaption = categoryCaption;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(final String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
