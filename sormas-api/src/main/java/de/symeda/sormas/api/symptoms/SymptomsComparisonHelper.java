/*******************************************************************************
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
 *******************************************************************************/
package de.symeda.sormas.api.symptoms;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

/**
 * Helper class for comparing SymptomsDto objects.
 * Provides methods to extract comparable symptom values and check for mismatches between two SymptomsDto instances.
 */
public final class SymptomsComparisonHelper {

    private static final List<String> NON_COMPARABLE_SYMPTOM_PROPERTIES =
        List.of(PseudonymizableDto.PSEUDONYMIZED, PseudonymizableDto.IN_JURISDICTION, SymptomsDto.SYMPTOMATIC);

    private SymptomsComparisonHelper() {
        // Utility class
    }

    /**
     * Checks if there is a mismatch between two SymptomsDto objects.
     *
     * @param symptomsA
     *            The symptoms from the case
     * @param symptomsB
     *            The symptoms from the external message
     * @return true if there is a mismatch between the two symptom objects, false otherwise
     */
    public static boolean hasCaseSymptomsMismatch(SymptomsDto symptomsA, SymptomsDto symptomsB) {
        Map<String, Object> externalMessageSymptomsMap = getComparableSymptomsValues(symptomsB);
        if (externalMessageSymptomsMap.isEmpty()) {
            return false;
        }

        Map<String, Object> caseSymptomsMap = getComparableSymptomsValues(symptomsA);
        for (Map.Entry<String, Object> entry : externalMessageSymptomsMap.entrySet()) {
            Object caseValue = caseSymptomsMap.get(entry.getKey());
            if (caseValue == null || !caseValue.equals(entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extracts comparable values from a SymptomsDto object.
     * Uses reflection to get all property descriptors and filters out non-comparable properties.
     * String values are trimmed to null and excluded if empty.
     *
     * @param symptoms
     *            The SymptomsDto object to extract values from
     * @return A TreeMap containing the comparable symptom property names and their values
     * @throws RuntimeException
     *             if an exception occurs during introspection or property access
     */
    private static Map<String, Object> getComparableSymptomsValues(SymptomsDto symptoms) {
        Map<String, Object> comparableValues = new TreeMap<>();
        if (symptoms == null) {
            return comparableValues;
        }

        try {
            PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(SymptomsDto.class, EntityDto.class).getPropertyDescriptors();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                if (propertyDescriptor.getReadMethod() == null || NON_COMPARABLE_SYMPTOM_PROPERTIES.contains(propertyDescriptor.getName())) {
                    continue;
                }

                Object value = propertyDescriptor.getReadMethod().invoke(symptoms);
                if (value == null) {
                    continue;
                }

                if (value instanceof String) {
                    value = StringUtils.trimToNull((String) value);
                    if (value == null) {
                        continue;
                    }
                }

                comparableValues.put(propertyDescriptor.getName(), value);
            }
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Exception when trying to compare symptoms: " + e.getMessage(), e);
        }

        return comparableValues;
    }
}
