/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.caze;

import de.symeda.sormas.backend.AbstractBeanTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CaseUserFilterCriteriaTest extends AbstractBeanTest {
    @Test
    public void testRestrictAccessToAssignedEntities() {

        CaseUserFilterCriteria criteria = new CaseUserFilterCriteria();

        // Test default value
        assertFalse(criteria.isRestrictAccessToAssignedEntities());

        // Set and test new value
        criteria.setRestrictAccessToAssignedEntities(true);
        assertTrue(criteria.isRestrictAccessToAssignedEntities());

        // Set and test another value
        criteria.setRestrictAccessToAssignedEntities(false);
        assertFalse(criteria.isRestrictAccessToAssignedEntities());
    }

    @Test
    public void testExcludeSharedCases() {

        CaseUserFilterCriteria criteria = new CaseUserFilterCriteria();

        // Test default value
        assertFalse(criteria.isExcludeSharedCases());

        // Set and test new value using the setter method
        criteria.excludeSharedCases(true);
        assertTrue(criteria.isExcludeSharedCases());

        // Set and test another value using the setter method
        criteria.excludeSharedCases(false);
        assertFalse(criteria.isExcludeSharedCases());
    }
}