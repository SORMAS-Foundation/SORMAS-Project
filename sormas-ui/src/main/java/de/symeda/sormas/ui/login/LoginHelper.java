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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.login;

import java.security.Principal;

import javax.servlet.ServletException;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.utils.UserRightsException;

public final class LoginHelper {

    public static boolean isUserSignedIn() {
    	Principal principal = VaadinServletService.getCurrentServletRequest().getUserPrincipal();
    	return principal != null;
    }
    
    public static boolean login(String username, String password) throws UserRightsException {

        if (username == null || username.isEmpty())
            return false;
        
        try {
			VaadinServletService.getCurrentServletRequest().login(username, password);
			// check user role
			if (!VaadinServletService.getCurrentServletRequest().isUserInRole(UserRole._USER)) {
				VaadinServletService.getCurrentServletRequest().logout();
				throw new UserRightsException(I18nProperties.getString(Strings.errorNoAccessToWeb));
			}
		} catch (ServletException e) {
			return false;
		}

        return true;
    }
    
    public static boolean logout() {
        try {
			VaadinServletService.getCurrentServletRequest().logout();
		} catch (ServletException e) {
			return false;
		}
        
        VaadinSession.getCurrent().getSession().invalidate();
        Page.getCurrent().reload();
        
        return true;
    }
}
