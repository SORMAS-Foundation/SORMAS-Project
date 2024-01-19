package de.symeda.sormas.backend.user;

public class UserHelper {

	private UserHelper() {
	}

	public static boolean isRestrictedToAssignEntities(User user) {
		if (user != null && !user.getUserRoles().isEmpty()) {
			return user.getUserRoles().stream().allMatch(UserRole::isRestrictAccessToAssignedEntities);
		}
		return false;
	}

}
