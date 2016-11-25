package de.symeda.sormas.ui.login;

import java.security.Principal;
import java.util.Set;

import javax.servlet.ServletException;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;


/**
 * Helper to retr
 * 
 * @author Martin Wahnschaffe
 */
public class LoginHelper {

    public static boolean login(String username, String password) {
        if (username == null || username.isEmpty())
            return false;
        
        try {
			VaadinServletService.getCurrentServletRequest().login(username, password);
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
    
    public static boolean isUserSignedIn() {
    	Principal principal = VaadinServletService.getCurrentServletRequest().getUserPrincipal();
    	return principal != null;
    }
    
    public static String getCurrentUserName() {
    	
		Principal activeUserPrincipal = VaadinServletService.getCurrentServletRequest().getUserPrincipal();
		if (activeUserPrincipal != null) {
			return activeUserPrincipal.getName();
		}
		return null;
    }
    
    public static UserDto getCurrentUser() {
    	String userName = getCurrentUserName();
    	if (userName != null) {
    		return FacadeProvider.getUserFacade().getByUserName(userName);
    	}
    	return null;
    }
    
    public static Set<UserRole> getCurrentUserRoles() {
    	return getCurrentUser().getUserRoles();
    }
    
    public static UserReferenceDto getCurrentUserAsReference() {
    	String userName = getCurrentUserName();
    	if (userName != null) {
    		return FacadeProvider.getUserFacade().getByUserNameAsReference(userName);
    	}
    	return null;
    }
    
	public static boolean isCurrentUser(UserDto user) {

		return isCurrentUser(user.getUserEmail());
	}

	public static boolean isCurrentUser(String userName) {

		Principal activeUserPrincipal = VaadinServletService.getCurrentServletRequest().getUserPrincipal();
		return isCurrentUser(userName, activeUserPrincipal);
	}

	private static boolean isCurrentUser(String userName, Principal activeUserPrincipal) {

		if (activeUserPrincipal == null) {
			return false;
		} else {
			return activeUserPrincipal.getName().equalsIgnoreCase(userName);
		}
	}
}
