package de.symeda.sormas.ui.login;

import java.security.Principal;

import javax.servlet.ServletException;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;

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
				throw new UserRightsException("Your user account does not have access to the web application.");
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
