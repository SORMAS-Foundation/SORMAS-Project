/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ColumnDefault;

import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity(name = SystemConfigurationValue.TABLE_NAME)
public class SystemConfigurationValue extends AbstractDomainObject {

    private static final long serialVersionUID = 1L;

    public static final String TABLE_NAME = "systemconfigurationvalue";

    public static final String VALUE_FIELD_NAME = "value";
    public static final String KEY_FIELD_NAME = "key";
    public static final String CATEGORY_FIELD_NAME = "category";
    public static final String OPTIONAL_FIELD_NAME = "optional";
    public static final String PATTERN_FIELD_NAME = "pattern";
    public static final String ENCRYPT_FIELD_NAME = "encrypt";
    public static final String DATA_PROVIDER_FIELD_NAME = "dataProvider";
    public static final String DESCRIPTION = "description";

    private String value;
    private String key;
    private String description;
    private SystemConfigurationCategory category;
    private Boolean optional;
    private String pattern;
    private Boolean encrypt;
    private String dataProvider;
    private String validationMessage;

    @Column(nullable = true, name = "config_value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Column(nullable = false, name = "config_key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Column(nullable = true, name = "value_description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    public SystemConfigurationCategory getCategory() {
        return category;
    }

    public void setCategory(SystemConfigurationCategory category) {
        this.category = category;
    }

    @Column(name = "value_optional")
    @ColumnDefault("false")
    public Boolean getOptional() {
        return optional;
    }

    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

    @Column(name = "value_pattern")
    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Column(name = "value_encrypt")
    @ColumnDefault("false")
    public Boolean getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(Boolean encrypt) {
        this.encrypt = encrypt;
    }

    @Column(name = "data_provider")
    public String getDataProvider() {
        return dataProvider;
    }

    public void setDataProvider(String dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Column(name = "validation_message")
    public String getValidationMessage() {
        return validationMessage;
    }

    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
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
