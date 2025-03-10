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

package de.symeda.sormas.backend.systemconfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDiseasesProvider;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDto;

/**
 * Unit tests for {@link SystemConfigurationValueDiseasesProvider}.
 */
class SystemConfigurationValueDiseasesProviderTest {

    private SystemConfigurationValueDiseasesProvider provider;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        provider = new SystemConfigurationValueDiseasesProvider();
    }

    /**
     * Tests the {@link SystemConfigurationValueDiseasesProvider#getKeys()} method.
     */
    @Test
    void testGetKeys() {

        final Set<String> keys = provider.getKeys();
        assertThat(keys, is(Set.of(Disease.values()).stream().map(Disease::getName).collect(Collectors.toSet())));
    }

    /**
     * Tests the {@link SystemConfigurationValueDiseasesProvider#getOptions()} method.
     */
    @Test
    void testGetOptions() {

        final Map<String, String> options = provider.getOptions();
        assertThat(options, is(Arrays.stream(Disease.values()).collect(Collectors.toMap(Disease::getName, Disease::toShortString))));
    }

    /**
     * Tests the {@link SystemConfigurationValueDiseasesProvider#applyValues(Map, SystemConfigurationValueDto)} method.
     */
    @Test
    void testApplyValues() {

        final SystemConfigurationValueDto dto = new SystemConfigurationValueDto();
        final Map<String, String> values = Map.of(Disease.TUBERCULOSIS.getName(), Disease.TUBERCULOSIS.toShortString());

        provider.applyValues(values, dto);

        assertThat(dto.getValue(), is(Disease.TUBERCULOSIS.getName()));
    }

    /**
     * Tests the {@link SystemConfigurationValueDiseasesProvider#getMappedValues(SystemConfigurationValueDto)} method.
     */
    @Test
    void testGetMappedValues() {

        final SystemConfigurationValueDto dto = new SystemConfigurationValueDto();
        dto.setValue(Disease.TUBERCULOSIS.getName());

        final Map<String, String> mappedValues = provider.getMappedValues(dto);

        assertThat(mappedValues, is(Map.of(Disease.TUBERCULOSIS.getName(), Disease.TUBERCULOSIS.toShortString())));
    }
}
