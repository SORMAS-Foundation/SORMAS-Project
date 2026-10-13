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

package de.symeda.sormas.ui.configuration.system;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class for processing internationalization (i18n) strings in system configuration captions and messages.
 */
public class SystemConfigurationI18nHelper {

    private static final Pattern I18N_PATTERN_WITH_DEFAULT = Pattern.compile("([^\\/]+)\\/([^\\/]+)");

    private static final Pattern I18N_PATTERN = Pattern.compile("([^\\/]+)\\/?");

    /**
     * Processes an i18n string and applies the given consumer to the extracted default name and name key.
     *
     * @param input
     *            the i18n string to process, expected to start with "i18n:"
     * @param consumer
     *            the consumer to apply to the extracted default name and name key
     */
    public static void processI18nString(final String input, final BiConsumer<String, String> consumer) {

        if (input == null || !input.startsWith("i18n/")) {
            return;
        }

        final String remaining = input.substring(5);
        final Matcher matcher = I18N_PATTERN_WITH_DEFAULT.matcher(remaining);

        if (matcher.find()) {
            final String defaultName = matcher.group(1);
            final String nameKey = matcher.group(2);
            consumer.accept(defaultName, nameKey);
        }
        // No else needed, as the consumer is not called if no match
    }

    /**
     * Processes an i18n string and applies the given consumer to the extracted name key.
     *
     * @param input
     *            the i18n string to process, expected to start with "i18n:"
     * @param consumer
     *            the consumer to apply to the extracted default name and name key
     */
    public static void processI18nString(final String input, final Consumer<String> consumer) {

        if (input == null || !input.startsWith("i18n/")) {
            return;
        }

        final String remaining = input.substring(5);
        final Matcher matcher = I18N_PATTERN.matcher(remaining);

        if (matcher.find()) {
            final String nameKey = matcher.group(1);
            consumer.accept(nameKey);
        }
        // No else needed, as the consumer is not called if no match
    }

    private SystemConfigurationI18nHelper() {
        // Private constructor to hide the implicit public one
    }
}
