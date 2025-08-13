/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.ui.caze.notifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.YesNoUnknown;

/**
 * Represents treatment options with predefined values for UI selection.
 * Provides a comparable wrapper around YesNoUnknown enum with additional NOT_APPLICABLE option.
 */
public class TreatmentOption implements Comparable<TreatmentOption> {

    public static final TreatmentOption YES = new TreatmentOption(YesNoUnknown.YES.toString(), I18nProperties.getEnumCaption(YesNoUnknown.YES));
    public static final TreatmentOption NO = new TreatmentOption(YesNoUnknown.NO.toString(), I18nProperties.getEnumCaption(YesNoUnknown.NO));
    public static final TreatmentOption NOT_APPLICABLE = new TreatmentOption("NA", I18nProperties.getString(Strings.notApplicable));
    public static final TreatmentOption UNKNOWN =
        new TreatmentOption(YesNoUnknown.UNKNOWN.toString(), I18nProperties.getEnumCaption(YesNoUnknown.UNKNOWN));

    public static final Set<TreatmentOption> ALL_OPTIONS =
        Collections.unmodifiableSet(new TreeSet<>(Arrays.asList(YES, NO, NOT_APPLICABLE, UNKNOWN)));

    private String value;
    private String caption;

    /**
     * Creates a TreatmentOption from a YesNoUnknown enum value.
     * 
     * @param yesNoUnknown
     *            the enum value to convert, null returns NOT_APPLICABLE
     * @return corresponding TreatmentOption
     */
    public static final TreatmentOption forValue(YesNoUnknown yesNoUnknown) {
        if (yesNoUnknown == null) {
            return NOT_APPLICABLE; // Default case for not applicable
        }
        switch (yesNoUnknown) {
        case YES:
            return YES;
        case NO:
            return NO;
        case UNKNOWN:
            return UNKNOWN;
        default:
            return NOT_APPLICABLE; // Default case for not applicable
        }
    }

    /**
     * Creates a new TreatmentOption.
     * 
     * @param value
     *            the internal value
     * @param caption
     *            the display caption
     */
    public TreatmentOption(String value, String caption) {
        this.value = value;
        this.caption = caption;
    }

    public String getValue() {
        return value;
    }

    public String getCaption() {
        return caption;
    }

    @Override
    public String toString() {
        return caption; // Use caption as the default display
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TreatmentOption that = (TreatmentOption) obj;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * Compares treatment options in the order: YES, NO, UNKNOWN, NOT_APPLICABLE.
     */
    @Override
    public int compareTo(TreatmentOption other) {
        if (other == null) {
            return 1;
        }

        // Define the desired order: YES, NO, UNKNOWN, NOT_APPLICABLE
        int thisOrder = getOrder(this);
        int otherOrder = getOrder(other);

        return Integer.compare(thisOrder, otherOrder);
    }

    private int getOrder(TreatmentOption option) {
        if (option.equals(YES)) {
            return 1;
        } else if (option.equals(NO)) {
            return 2;
        } else if (option.equals(UNKNOWN)) {
            return 3;
        } else if (option.equals(NOT_APPLICABLE)) {
            return 4;
        } else {
            return 5; // Any other unknown options go last
        }
    }
}
