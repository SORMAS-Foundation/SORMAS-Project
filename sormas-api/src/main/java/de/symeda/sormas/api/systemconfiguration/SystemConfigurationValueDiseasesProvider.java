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

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.symeda.sormas.api.Disease;

/**
 * Provides system configuration values for diseases.
 * The implementation will transform the applied values to a pipe separated list string value.
 * The implementation will use {@link Disease#getName()} as value for the items in the list.
 */
public class SystemConfigurationValueDiseasesProvider implements SystemConfigurationValueDataProvider {

    private static final long serialVersionUID = 1L;

    /**
     * Retrieves the set of disease keys.
     *
     * @return a set of disease names.
     */
    @Override
    public Set<String> getKeys() {
        return Collections.unmodifiableSet(Arrays.stream(Disease.values()).distinct().map(Disease::getName).collect(Collectors.toSet()));
    }

    /**
     * Retrieves the options for diseases.
     * The keys used are the {@link Disease#getName()} maped to {@link Disease#toShortString()}
     *
     * @return a map of disease names to their short string representations.
     */
    @Override
    public Map<String, String> getOptions() {

        return Collections
            .unmodifiableMap(Arrays.stream(Disease.values()).distinct().collect(Collectors.toMap(Disease::getName, Disease::toShortString)));
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

        if (null == values || values.isEmpty()) {
            return;
        }
        final String pipeSeparatedValues = values.keySet().stream().collect(Collectors.joining("|"));
        dto.setValue(pipeSeparatedValues);
    }

    /**
     * Retrieves the mapped values from the given DTO.
     * It will map all the values from the configuration similart to {@link #getOptions()}.
     *
     * @param dto
     *            the DTO containing the values.
     * @return a map of disease names with short string.
     */
    @Override
    public Map<String, String> getMappedValues(final SystemConfigurationValueDto dto) {

        if (null == dto || null == dto.getValue() || dto.getValue().isBlank()) {
            return Collections.emptyMap();
        }

        final Set<String> valuesSet = Stream.of(dto.getValue().split("\\|")).collect(Collectors.toSet());

        return Collections.unmodifiableMap(
            getOptions().entrySet()
                .stream()
                .filter(entry -> valuesSet.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }
}
