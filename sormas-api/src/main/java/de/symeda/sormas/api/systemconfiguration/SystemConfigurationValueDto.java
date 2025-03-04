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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.FieldConstraints;

public class SystemConfigurationValueDto extends EntityDto {

    private static final long serialVersionUID = 1L;

    public static final String I18N_PREFIX = "SystemConfigurationValue";

    public static final String VALUE_PROPERTY_NAME = "value";
    public static final String KEY_PROPERTY_NAME = "key";
    public static final String CATEGORY_PROPERTY_NAME = "category";
    public static final String PATTERN_PROPERTY_NAME = "pattern";
    public static final String ENCRYPT_PROPERTY_NAME = "encrypt";

    @NotNull(message = Validations.required)
    @Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
    private String value;

    @NotNull(message = Validations.required)
    @Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
    private String key;

    private SystemConfigurationCategoryReferenceDto category;

    private String pattern;

    @NotNull(message = Validations.required)
    private Boolean encrypt;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public SystemConfigurationCategoryReferenceDto getCategory() {
        return category;
    }

    public void setCategory(SystemConfigurationCategoryReferenceDto category) {
        this.category = category;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Boolean getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(Boolean encrypt) {
        this.encrypt = encrypt;
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
