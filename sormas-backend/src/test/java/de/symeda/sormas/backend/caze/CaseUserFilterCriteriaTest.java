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
