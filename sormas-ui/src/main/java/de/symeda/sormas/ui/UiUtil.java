package de.symeda.sormas.ui;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.user.UserRight;

public class UiUtil {

	private UiUtil() {
	}

	public static boolean permitted(FeatureType feature, UserRight userRight) {
		return (feature == null || !FacadeProvider.getFeatureConfigurationFacade().isFeatureDisabled(feature))
			&& (userRight == null || UserProvider.getCurrent().hasUserRight(userRight));
	}

	public static boolean permitted(UserRight userRight) {
		return permitted(null, userRight);
	}
}
