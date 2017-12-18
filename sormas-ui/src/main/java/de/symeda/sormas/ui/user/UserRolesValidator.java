package de.symeda.sormas.ui.user;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.data.validator.AbstractValidator;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.user.UserRole.UserRoleValidationException;

@SuppressWarnings("serial")
public final class UserRolesValidator extends AbstractValidator<Set<UserRole>> {
	
	UserRolesValidator() {
		super("");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<Set<UserRole>> getType() {
		return (Class<Set<UserRole>>) new HashSet<UserRole>().getClass();
	}

	@Override
	protected boolean isValidValue(Set<UserRole> value) {
		try {
			UserRole.validate(value);
		} catch (UserRoleValidationException e) {
			setErrorMessage(e.getMessage());
			return false;
		}
		return true;
	}
}