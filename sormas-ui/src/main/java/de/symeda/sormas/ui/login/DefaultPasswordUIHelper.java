/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.ui.login;

import java.util.List;

import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DefaultEntityHelper;

public class DefaultPasswordUIHelper {

	/**
	 * Intercepts the default login
	 * If the logged in user is a default user with the default password or
	 * if the user is a privileged user and there is at least one user with a default password
	 * the according dialogs will be shown.
	 *
	 * @param originalLoginListener
	 *            the default login listener that will be invoked when the
	 *            default password actions are finished
	 * @param vaadinUI
	 *            the vaadin UI element that will be used to add the vaadin windows to
	 * @return a new LoginListener that will intercept the login procedure
	 *         and will redirect to the requested target afterwards
	 */
	static LoginScreen.LoginListener getInterceptionLoginListener(LoginScreen.LoginListener originalLoginListener, UI vaadinUI) {
		return () -> {
			List<UserDto> usersWithDefaultPassword;
			if (FacadeProvider.getConfigFacade().isSkipDefaultPasswordCheck()
				|| (usersWithDefaultPassword = DefaultPasswordUIHelper.getUsersWithDefaultPassword()).isEmpty()) {
				originalLoginListener.loginSuccessful();
			} else {
				UserDto currentUser = FacadeProvider.getUserFacade().getCurrentUser();
				boolean currentUserUsesDefaultPassword = DefaultEntityHelper.currentUserUsesDefaultPassword(usersWithDefaultPassword, currentUser);
				boolean otherUsersWithDefaultPassword = DefaultEntityHelper.otherUsersUseDefaultPassword(usersWithDefaultPassword, currentUser);
				if (currentUserUsesDefaultPassword) {
					vaadinUI.addWindow(new ChangeDefaultUserPasswordWindow(otherUsersWithDefaultPassword ? () -> {
						vaadinUI.addWindow(
							new ChangeDefaultPasswordsWindow(originalLoginListener::loginSuccessful, without(usersWithDefaultPassword, currentUser)));
					} : originalLoginListener::loginSuccessful, currentUser));
				} else {
					vaadinUI.addWindow(
						new ChangeDefaultPasswordsWindow(originalLoginListener::loginSuccessful, without(usersWithDefaultPassword, currentUser)));
				}
			}
		};
	}

	private static List<UserDto> getUsersWithDefaultPassword() {
		return FacadeProvider.getUserFacade().getUsersWithDefaultPassword();
	}

	/**
	 * Removes an item from the given list
	 * If the list is null, the list (null) will be returned
	 * 
	 * @param list
	 *            the list where the element should be removed from, can be null
	 * @param element
	 *            the element that should be removed from the list
	 * @param <T>
	 *            the type of elements the list can contain
	 * @return the original list without the specified element
	 * @throws NullPointerException
	 *             if the specified element is null and this list does not permit null elements
	 * @throws UnsupportedOperationException
	 *             if the removal of an element is not supported on the provided list
	 */
	private static <T> List<T> without(List<T> list, T element) throws NullPointerException, UnsupportedOperationException {
		if (list == null) {
			return null;
		} else {
			list.remove(element);
			return list;
		}
	}
}
