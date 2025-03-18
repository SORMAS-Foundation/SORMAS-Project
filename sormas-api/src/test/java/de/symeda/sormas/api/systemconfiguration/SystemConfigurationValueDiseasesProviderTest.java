/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version).
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.systemconfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;

/**
 * Unit tests for {@link SystemConfigurationValueDiseasesProvider}.
 */
class SystemConfigurationValueDiseasesProviderTest {

    private SystemConfigurationValueDiseasesProvider provider;

    @BeforeEach
    void setUp() {
        provider = new SystemConfigurationValueDiseasesProvider();
    }

    /**
     * Test to verify that the keys returned by the provider match the expected keys.
     */
    @Test
    void testGetKeys() {
        Set<String> expectedKeys = Arrays.stream(Disease.values()).map(Disease::getName).collect(Collectors.toSet());

        Set<String> keys = provider.getKeys();

        assertThat(keys, is(expectedKeys));
    }

    /**
     * Test to verify that the options returned by the provider match the expected options.
     */
    @Test
    void testGetOptions() {
        Map<String, String> expectedOptions = Arrays.stream(Disease.values()).collect(Collectors.toMap(Disease::getName, Disease::toShortString));

        Map<String, String> options = provider.getOptions();

        assertThat(options, is(expectedOptions));
    }

    /**
     * Test to verify that the provider correctly applies values to the DTO.
     */
    @Test
    void testApplyValues() {
        SystemConfigurationValueDto dto = new SystemConfigurationValueDto();
        Map<String, String> values = Map.of(Disease.TUBERCULOSIS.getName(), Disease.TUBERCULOSIS.toShortString());

        provider.applyValues(values, dto);

        assertThat(dto.getValue(), is(Disease.TUBERCULOSIS.getName()));
    }

    /**
     * Test to verify that the mapped values returned by the provider match the expected mapped values.
     */
    @Test
    void testGetMappedValues() {
        SystemConfigurationValueDto dto = new SystemConfigurationValueDto();
        dto.setValue(Disease.TUBERCULOSIS.getName());

        Map<String, String> expectedMappedValues = Map.of(Disease.TUBERCULOSIS.getName(), Disease.TUBERCULOSIS.toShortString());

        Map<String, String> mappedValues = provider.getMappedValues(dto);

        assertThat(mappedValues, is(expectedMappedValues));
    }

    /**
     * Test to verify that the provider correctly handles empty values when applying them to the DTO.
     */
    @Test
    void testApplyValuesWithEmptyValues() {
        SystemConfigurationValueDto dto = new SystemConfigurationValueDto();
        Map<String, String> values = Map.of();

        provider.applyValues(values, dto);

        assertThat(dto.getValue(), is((String) null));
    }

    /**
     * Test to verify that the provider correctly handles an empty DTO when getting mapped values.
     */
    @Test
    void testGetMappedValuesWithEmptyDto() {
        SystemConfigurationValueDto dto = new SystemConfigurationValueDto();

        Map<String, String> mappedValues = provider.getMappedValues(dto);

        assertThat(mappedValues.isEmpty(), is(true));
    }

    /**
     * Test to verify that the provider correctly handles null values when applying them to the DTO.
     */
    @Test
    void testApplyValuesWithNullValues() {
        SystemConfigurationValueDto dto = new SystemConfigurationValueDto();
        Map<String, String> values = null;

        provider.applyValues(values, dto);

        assertThat(dto.getValue(), is((String) null));
    }

    /**
     * Test to verify that the provider correctly handles a null DTO when getting mapped values.
     */
    @Test
    void testGetMappedValuesWithNullDto() {
        SystemConfigurationValueDto dto = null;

        Map<String, String> mappedValues = provider.getMappedValues(dto);

        assertThat(mappedValues.isEmpty(), is(true));
    }

}
