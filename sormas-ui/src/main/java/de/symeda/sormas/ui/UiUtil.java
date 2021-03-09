package de.symeda.sormas.ui;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.user.UserRight;

public class UiUtil {

	private UiUtil() {
	}

	public static boolean permitted(SormasUI ui, FeatureType feature, UserRight userRight) {
		return (feature == null || !FacadeProvider.getFeatureConfigurationFacade().isFeatureDisabled(feature))
			&& (userRight == null || ui.getUserProvider().hasUserRight(userRight));
	}

	public static boolean permitted(SormasUI ui, UserRight userRight) {
		return permitted(ui,null, userRight);
	}
}
