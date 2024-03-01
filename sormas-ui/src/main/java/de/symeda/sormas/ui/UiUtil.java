package de.symeda.sormas.ui;

import java.util.Set;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.user.UserRight;

public class UiUtil {

	private UiUtil() {
	}

	public static boolean permitted(FeatureType feature, UserRight userRight) {
		return (feature == null || enabled(feature)) && (userRight == null || permitted(userRight));
	}

	public static boolean permitted(Set<FeatureType> features, UserRight userRight) {
		return enabled(features) && permitted(userRight);
	}

	public static boolean permitted(Set<FeatureType> features, Set<UserRight> userRights) {
		return enabled(features) && permitted(userRights);
	}

	public static boolean permitted(UserRight userRight) {
		return UserProvider.getCurrent().hasUserRight(userRight);
	}

	public static boolean permitted(Set<UserRight> userRights) {
		return UserProvider.getCurrent().hasAllUserRights(userRights.toArray(new UserRight[] {}));
	}

	public static boolean enabled(FeatureType featureType) {
		return !disabled(featureType);
	}

	public static boolean disabled(FeatureType featureType) {
		return FacadeProvider.getFeatureConfigurationFacade().isFeatureDisabled(featureType);
	}

	public static boolean enabled(Set<FeatureType> features) {
		return FacadeProvider.getFeatureConfigurationFacade().areAllFeatureEnabled(features.toArray(new FeatureType[] {}));
	}
}
