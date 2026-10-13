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

import java.util.regex.Pattern;

/**
 * Utility class for validating system configuration keys and values.
 */
public class SystemConfigurationValueHelper {

    /**
     * Regular expression pattern for valid system configuration keys.
     * Keys must consist of uppercase letters, digits, underscores, dots.
     */
    private static final String KEY_PATTERN = "[A-Z0-9_.]+";

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private SystemConfigurationValueHelper() {
    }

    /**
     * Checks if both the configuration key and value are valid.
     *
     * @param key
     *            The system configuration key to validate.
     * @param value
     *            The system configuration value to validate.
     * @return {@code true} if both the key and value are valid, {@code false} otherwise.
     */
    public static final boolean isConfigurationValid(final String key, final String value) {
        return isConfigurationKeyValid(key) && isConfigurationValueValid(value);
    }

    /**
     * Checks if the given system configuration key is valid.
     *
     * @param key
     *            The system configuration key to validate.
     * @return {@code true} if the key is valid, {@code false} otherwise.
     *         A key is considered valid if it matches the {@link #KEY_PATTERN}.
     */
    public static final boolean isConfigurationKeyValid(final String key) {
        return Pattern.matches(KEY_PATTERN, key);
    }

    /**
     * Checks if the given system configuration value is valid.
     *
     * @param value
     *            The system configuration value to validate.
     * @return {@code true} if the value is valid, {@code false} otherwise.
     *         A value is considered valid if it is not {@code null}.
     */
    public static final boolean isConfigurationValueValid(final String value) {
        return null != value;
    }

    /**
     * Checks if the given system configuration value matches the provided pattern.
     *
     * @param value
     *            The system configuration value to validate.
     * @param pattern
     *            The pattern to match the value against.
     * @return {@code true} if the value matches the pattern, {@code false} otherwise.
     */
    public static final boolean isConfigurationValueMatchingPattern(final String value, final String pattern) {
        return Pattern.matches(pattern, value);
    }
}
