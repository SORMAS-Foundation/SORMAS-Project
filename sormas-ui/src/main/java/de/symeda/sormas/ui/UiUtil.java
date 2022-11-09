package de.symeda.sormas.ui;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.user.FormAccess;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.user.UserType;

public class UiUtil {

	private UiUtil() {
	}

	public static boolean permitted(FeatureType feature, UserRight userRight) {
		return (feature == null || !FacadeProvider.getFeatureConfigurationFacade().isFeatureDisabled(feature))
				&& (userRight == null || UserProvider.getCurrent().hasUserRight(userRight));
	}

	public static boolean permitted(UserType userType) {
		boolean check = false;
		if (UserProvider.getCurrent().hasUserType(userType)) {
			check = true;
			return check;
		} else {
			return check;
		}
	}
	
	public static boolean permitted(FormAccess formAccess) {
		boolean check = false;
		if (UserProvider.getCurrent().hasFormAccess(formAccess)) {
			check = true;
			return check;
		} else {
			return check;
		}
	}
	
	public static boolean permitted(UserRole userrole) {
		boolean check = false;
		if (UserProvider.getCurrent().hasUserRole(userrole)) {
			check = true;
			return check;
		} else {
			return check;
		}
	}

	public static boolean permitted(UserRight userRight) {
		return permitted(null, userRight);
	}

}
