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

package de.symeda.sormas.api.systemconfiguration;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Interface for providing system configuration values.
 * Implementations of this interface are responsible for retrieving data, applying
 * and transforming configuration values for the system configuration.
 */
public interface SystemConfigurationValueDataProvider extends Serializable {

    /**
     * Retrieves the set of keys for the provided options.
     * Key names could be used for example in selects.
     *
     * @return a set of key names.
     */
    public Set<String> getKeys();

    /**
     * Retrieves the available options for the provided data.
     * Each key name should have an corresponding value that will be used at selection.
     *
     * @return a map of options where the key is the option name and the value is the option value.
     */
    public Map<String, String> getOptions();

    /**
     * Applies the provided values to the system configuration.
     * With this method any conversion necessary should be performed.
     * For ex. a set of options converted to comma separated value.
     *
     * @param values
     *            a map of values to be applied where the key is the configuration key and the value is the configuration value.
     * @param dto
     *            the data transfer object containing the system configuration values.
     */
    public void applyValues(@Nonnull Map<String, String> values, @Nonnull SystemConfigurationValueDto dto);

    /**
     * Retrieves the current values from the system configuration.
     * This method should return the values that are currently set in the system configuration.
     * The values should be mapped in the same way as {@link #getOptions()}.
     *
     * @param dto
     *            the data transfer object containing the system configuration value.
     * @return a map where the key is the mapped keyname and the value is the corresponding option value.
     */
    public Map<String, String> getMappedValues(@Nonnull SystemConfigurationValueDto dto);

}
