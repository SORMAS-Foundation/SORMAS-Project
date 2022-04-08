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
package de.symeda.sormas.ui.user;

import java.util.Collection;
import java.util.stream.Collectors;

import com.vaadin.v7.data.Validator;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.api.user.UserRoleReferenceDto;

@SuppressWarnings("serial")
public final class UserRolesValidator implements Validator {

	@SuppressWarnings("unchecked")
	@Override
	public void validate(Object value) throws InvalidValueException {
		try {
			Collection<UserRoleReferenceDto> values = (Collection<UserRoleReferenceDto>) value;
			FacadeProvider.getUserRoleFacade()
				.validateUserRoleCombination(
					values.stream().map(u -> FacadeProvider.getUserRoleFacade().getByUuid(u.getUuid())).collect(Collectors.toSet()));
		} catch (UserRoleDto.UserRoleValidationException e) {
			throw new InvalidValueException(e.getMessage());
		}
	}
}
