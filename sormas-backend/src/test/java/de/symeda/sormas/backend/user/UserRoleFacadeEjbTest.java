package de.symeda.sormas.backend.user;

import static org.junit.Assert.assertThrows;

import org.junit.Test;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.AbstractBeanTest;

public class UserRoleFacadeEjbTest extends AbstractBeanTest {

    @Test
    public void testSaveUserRole() {

        UserRoleDto userRole = UserRoleDto.build();
        userRole.setJurisdictionLevel(JurisdictionLevel.NATION);
        userRole.setCaption("Test user role");
        userRole = getUserRoleFacade().saveUserRole(userRole);

        creator.createUser(creator.createRDCF(), userRole.toReference());

        // Test to change jurisdiction of assigned UserRole
        userRole.setJurisdictionLevel(JurisdictionLevel.COMMUNITY);
        UserRoleDto finalUserRole = userRole;
        assertThrows(I18nProperties.getValidationError(Validations.jurisdictionChangeUserAssignment), ValidationRuntimeException.class, () -> getUserRoleFacade().saveUserRole(finalUserRole));
    }
}