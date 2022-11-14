package de.symeda.sormas.backend.user;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.AbstractBeanTest;

public class UserRoleFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testChangeJurisdictionOfAssignedUserRole() {

		UserRoleDto userRole = UserRoleDto.build();
		userRole.setJurisdictionLevel(JurisdictionLevel.NATION);
		userRole.setCaption("Test user role");
		userRole = getUserRoleFacade().saveUserRole(userRole);

		creator.createUser(creator.createRDCF(), userRole.toReference());

		userRole.setJurisdictionLevel(JurisdictionLevel.COMMUNITY);
		UserRoleDto finalUserRole = userRole;
		assertThrowsWithMessage(ValidationRuntimeException.class,
			I18nProperties.getValidationError(Validations.jurisdictionChangeUserAssignment),
			() -> getUserRoleFacade().saveUserRole(finalUserRole));
	}
}
