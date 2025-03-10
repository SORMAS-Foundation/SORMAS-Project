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

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueBooleanProvider;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDto;

/**
 * Test class for {@link SystemConfigurationValueBooleanProvider}.
 */
class SystemConfigurationValueBooleanProviderTest {

    private SystemConfigurationValueBooleanProvider provider;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        provider = new SystemConfigurationValueBooleanProvider();
    }

    /**
     * Tests the {@link SystemConfigurationValueBooleanProvider#getKeys()} method.
     */
    @Test
    void testGetKeys() {

        final Set<String> keys = provider.getKeys();
        assertThat(keys, is(Set.of("v1")));
    }

    /**
     * Tests the {@link SystemConfigurationValueBooleanProvider#getOptions()} method.
     */
    @Test
    void testGetOptions() {

        final Map<String, String> options = provider.getOptions();
        assertThat(options, is(Map.of("v1", "true", "v2", "false")));
    }

    /**
     * Tests the {@link SystemConfigurationValueBooleanProvider#applyValues(Map, SystemConfigurationValueDto)} method.
     */
    @Test
    void testApplyValues() {

        final SystemConfigurationValueDto dto = new SystemConfigurationValueDto();
        final Map<String, String> values = Map.of("v1", "true");

        provider.applyValues(values, dto);

        assertThat(dto.getValue(), is("true"));
    }

    /**
     * Tests the {@link SystemConfigurationValueBooleanProvider#getMappedValues(SystemConfigurationValueDto)} method.
     */
    @Test
    void testGetMappedValues() {

        final SystemConfigurationValueDto dto = new SystemConfigurationValueDto();
        dto.setValue("true");

        final Map<String, String> mappedValues = provider.getMappedValues(dto);

        assertThat(mappedValues, is(Map.of("v1", "true")));
    }
}
