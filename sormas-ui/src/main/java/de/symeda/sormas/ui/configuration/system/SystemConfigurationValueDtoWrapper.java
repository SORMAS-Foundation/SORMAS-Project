/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.configuration.system;

import java.util.Objects;

import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDto;

/**
 * Wrapper class for SystemConfigurationValueDto.
 */
public class SystemConfigurationValueDtoWrapper {

    public static final String WRAPPED_OBJECT_PROPERTY_NAME = "wrappedObject";

    private final SystemConfigurationValueDto wrappedObject;

    /**
     * Constructor to initialize the wrapper with a SystemConfigurationValueDto object.
     *
     * @param wrappedObject
     *            the SystemConfigurationValueDto object to be wrapped
     */
    public SystemConfigurationValueDtoWrapper(final SystemConfigurationValueDto wrappedObject) {

        Objects.requireNonNull(wrappedObject, "wrappedObject must not be null");
        this.wrappedObject = wrappedObject;
    }

    /**
     * Gets the wrapped SystemConfigurationValueDto object.
     *
     * @return the wrapped SystemConfigurationValueDto object
     */
    public SystemConfigurationValueDto getWrappedObject() {
        return wrappedObject;
    }

}
