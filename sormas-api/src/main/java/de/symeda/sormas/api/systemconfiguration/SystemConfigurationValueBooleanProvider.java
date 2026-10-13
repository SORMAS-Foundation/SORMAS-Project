/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at any later version).
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.systemconfiguration;

import java.util.Map;
import java.util.Set;

/**
 * Provides boolean values for system configuration.
 */
public class SystemConfigurationValueBooleanProvider implements SystemConfigurationValueDataProvider {

    private static final String KEY_V1 = "v1";
    private static final String OPTION_TRUE = "true";
    private static final String OPTION_FALSE = "false";

    /**
     * Gets the keys for the boolean values.
     *
     * @return a set of keys.
     */
    @Override
    public Set<String> getKeys() {
        return Set.of(KEY_V1);
    }

    /**
     * Gets the options for the boolean values.
     *
     * @return a map of options.
     */
    @Override
    public Map<String, String> getOptions() {
        return Map.of(KEY_V1, OPTION_TRUE, "v2", OPTION_FALSE);
    }

    /**
     * Applies the provided values to the given DTO.
     *
     * @param values
     *            the values to apply.
     * @param dto
     *            the DTO to apply the values to.
     */
    @Override
    public void applyValues(final Map<String, String> values, final SystemConfigurationValueDto dto) {
        if (null == dto) {
            return;
        }
        if (null == values || values.isEmpty()) {
            return;
        }
        values.values().stream().findFirst().ifPresent(dto::setValue);
    }

    /**
     * Gets the mapped values from the given DTO.
     *
     * @param dto
     *            the DTO to get the values from.
     * @return a map of the values.
     */
    @Override
    public Map<String, String> getMappedValues(final SystemConfigurationValueDto dto) {
        if (null == dto) {
            return Map.of();
        }
        return Map.of(KEY_V1, dto.getValue());
    }

}
