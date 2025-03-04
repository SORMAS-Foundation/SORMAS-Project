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
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.FieldConstraints;

/**
 * Data Transfer Object for System Configuration Category.
 */
public class SystemConfigurationCategoryDto extends EntityDto {

    private static final long serialVersionUID = 1L;

    public static final String I18N_PREFIX = "SystemConfigurationCategory";

    public static final String NAME_PROPERTY_NAME = "name";
    public static final String DESCRIPTION_PROPERTY_NAME = "description";
    public static final String CAPTION_PROPERTY_NAME = "caption";

    @NotNull(message = Validations.required)
    @Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
    private String name;

    @Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
    private String description;

    @Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
    private String caption;

    /**
     * Gets the name of the system configuration category.
     *
     * @return the name of the system configuration category.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the system configuration category.
     *
     * @param name the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the system configuration category.
     *
     * @return the description of the system configuration category.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the system configuration category.
     *
     * @param description the description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the caption of the system configuration category.
     *
     * @return the caption of the system configuration category.
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the caption of the system configuration category.
     *
     * @param caption the caption to set.
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return I18nProperties.getPrefixCaption(SystemConfigurationCategoryDto.I18N_PREFIX, this.getName(), this.getCaption());
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
