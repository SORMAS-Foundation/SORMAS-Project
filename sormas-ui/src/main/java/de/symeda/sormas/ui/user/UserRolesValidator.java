package de.symeda.sormas.ui.user;

import java.util.Collection;

import com.vaadin.data.Validator;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.user.UserRole.UserRoleValidationException;

@SuppressWarnings("serial")
public final class UserRolesValidator implements Validator {
	
	@SuppressWarnings("unchecked")
	@Override
	public void validate(Object value) throws InvalidValueException {
		try {
			UserRole.validate((Collection<UserRole>) value);
		} catch (UserRoleValidationException e) {
			throw new InvalidValueException(e.getMessage());
		}
	}
}