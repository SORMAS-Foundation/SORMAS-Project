package de.symeda.sormas.ui;

import java.util.Objects;
import java.util.Set;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.PseudonymizableDataAccessLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleDto;

public class UiUtil {

	private UiUtil() {
	}

	public static UserDto getUser() {
		return Objects.nonNull(getCurrentUserProvider()) ? getCurrentUserProvider().getUser() : null;
	}

	public static UserReferenceDto getUserReference() {
		return Objects.nonNull(getCurrentUserProvider()) ? getCurrentUserProvider().getUserReference() : null;
	}

	public static JurisdictionLevel getJurisdictionLevel() {
		return Objects.nonNull(getCurrentUserProvider()) ? getCurrentUserProvider().getJurisdictionLevel() : null;
	}

	public static String getUserName() {
		return Objects.nonNull(getCurrentUserProvider()) ? getCurrentUserProvider().getUserName() : null;
	}

	public static Set<UserRoleDto> getUserRoles() {
		return Objects.nonNull(getCurrentUserProvider()) ? getCurrentUserProvider().getUserRoles() : null;
	}

	public static Set<UserRight> getUserRights() {
		return Objects.nonNull(getCurrentUserProvider()) ? getCurrentUserProvider().getUserRights() : null;
	}

	public static PseudonymizableDataAccessLevel getPseudonymizableDataAccessLevel(boolean inJurisdiction) {
		return Objects.nonNull(getCurrentUserProvider()) ? getCurrentUserProvider().getPseudonymizableDataAccessLevel(inJurisdiction) : null;
	}

	public static boolean hasUserAccess() {
		return Objects.nonNull(getCurrentUserProvider()) && getCurrentUserProvider().hasUserAccess();
	}

	public static boolean hasOptionalHealthFacility() {
		return Objects.nonNull(getCurrentUserProvider()) && getCurrentUserProvider().hasOptionalHealthFacility();
	}

	public static boolean hasConfigurationAccess() {
		return Objects.nonNull(getCurrentUserProvider()) && getCurrentUserProvider().hasConfigurationAccess();
	}

	public static boolean hasRegionJurisdictionLevel() {
		return Objects.nonNull(getCurrentUserProvider()) && getCurrentUserProvider().hasRegionJurisdictionLevel();
	}

	public static boolean hasNationJurisdictionLevel() {
		return Objects.nonNull(getCurrentUserProvider()) && getCurrentUserProvider().hasNationJurisdictionLevel();
	}

	public static boolean hasNoneJurisdictionLevel() {
		return Objects.nonNull(getCurrentUserProvider()) && getCurrentUserProvider().hasNoneJurisdictionLevel();
	}

	public static boolean isPortHealthUser() {
		return Objects.nonNull(getCurrentUserProvider()) && getCurrentUserProvider().isPortHealthUser();
	}

	public static UserProvider getCurrentUserProvider() {
		return UserProvider.getCurrent();
	}

	public static boolean permitted(FeatureType feature, UserRight userRight) {
		return (feature == null || enabled(feature)) && (userRight == null || permitted(userRight));
	}

	public static boolean permitted(Set<FeatureType> features, UserRight userRight) {
		return enabled(features) && permitted(userRight);
	}

	public static boolean permitted(FeatureType feature, UserRight... userRights) {
		return enabled(feature) && permitted(userRights);
	}

	public static boolean permitted(UserRight userRight) {
		return Objects.nonNull(getCurrentUserProvider()) && getCurrentUserProvider().hasUserRight(userRight);
	}

	public static boolean permitted(UserRight... userRights) {
		return Objects.nonNull(getCurrentUserProvider()) && getCurrentUserProvider().hasAllUserRights(userRights);
	}

	//TODO: refactor this to hasUserRightWithAllowedFlag
	public static boolean permitted(boolean isEditAllowed, UserRight userRight) {
		return Objects.nonNull(getCurrentUserProvider()) && getCurrentUserProvider().hasUserRightWithEditAllowedFlag(isEditAllowed, userRight);
	}

	public static boolean permitted(boolean isEditAllowed, UserRight... userRights) {
		return Objects.nonNull(getCurrentUserProvider()) && getCurrentUserProvider().hasAllUserRightsWithEditAllowedFlag(isEditAllowed, userRights);
	}

	public static boolean permitted(EditPermissionType editPermissionType, UserRight userRight) {
		return Objects.nonNull(getCurrentUserProvider())
			&& getCurrentUserProvider().hasUserRightWithEditPermissionType(editPermissionType, userRight);
	}

	public static boolean enabled(FeatureType featureType) {
		return !disabled(featureType);
	}

	public static boolean enabled(FeatureType featureType, DeletableEntityType entityType) {
		return FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(featureType, entityType);
	}

	public static boolean disabled(FeatureType featureType) {
		return FacadeProvider.getFeatureConfigurationFacade().isFeatureDisabled(featureType);
	}

	public static boolean enabled(Set<FeatureType> features) {
		return FacadeProvider.getFeatureConfigurationFacade().areAllFeatureEnabled(features.toArray(new FeatureType[] {}));
	}
}
