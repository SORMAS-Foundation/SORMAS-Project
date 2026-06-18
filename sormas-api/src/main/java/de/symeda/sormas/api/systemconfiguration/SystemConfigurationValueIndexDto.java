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

/**
 * Data Transfer Object for system configuration values.
 */
public class SystemConfigurationValueIndexDto extends EntityDto {

    public static final String I18N_PREFIX = "SystemConfigurationValue";

    private static final long serialVersionUID = 1L;

    public static final String KEY_PROPERTY_NAME = "key";
    public static final String VALUE_PROPERTY_NAME = "value";
    public static final String DESCRIPTION_PROPERTY_NAME = "description";
    public static final String ENCRYPTED_PROPERTY_NAME = "encrypted";
    public static final String CATEGORY_NAME_PROPERTY_NAME = "category";
    public static final String CATEGORY_CAPTION_PROPERTY_NAME = "categoryCaption";
    public static final String CATEGORY_DESCRIPTION_PROPERTY_NAME = "categoryDescription";

    private String value;
    private String key;
    private String description;
    private boolean encrypted;
    private String category;
    private String categoryCaption;
    private String categoryDescription;

    /**
     * Gets the key of the configuration.
     * 
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key of the configuration.
     * 
     * @param key
     *            the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the value of the configuration.
     * 
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the configuration.
     * 
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Checks if the configuration value is encrypted.
     * 
     * @return true if encrypted, false otherwise
     */
    public boolean isEncrypted() {
        return encrypted;
    }

    /**
     * Sets the encryption status of the configuration value.
     * 
     * @param encrypted
     *            the encryption status to set
     */
    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    /**
     * Gets the description of the configuration.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the configuration.
     * 
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the category name of the configuration.
     * 
     * @return the category name
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category name of the configuration.
     * 
     * @param category
     *            the category name to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the category caption of the configuration.
     * 
     * @return the category caption
     */
    public String getCategoryCaption() {
        return categoryCaption;
    }

    /**
     * Sets the category caption of the configuration.
     * 
     * @param categoryCaption
     *            the category caption to set
     */
    public void setCategoryCaption(String categoryCaption) {
        this.categoryCaption = categoryCaption;
    }

    /**
     * Gets the category description of the configuration.
     * 
     * @return the category description
     */
    public String getCategoryDescription() {
        return categoryDescription;
    }

    /**
     * Sets the category description of the configuration.
     * 
     * @param categoryDescription
     *            the category description to set
     */
    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
