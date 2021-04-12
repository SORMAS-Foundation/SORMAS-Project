package de.symeda.sormas.backend.user;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DefaultUserHelper;
import de.symeda.sormas.api.utils.PasswordHelper;

public class UserTestHelper {

	/**
	 * Generate <code>count</code> random users with UUID as firstname, lastname, username and random password. The
	 * role is determined by the countposition % UserRole.values().length
	 *
	 * @param count
	 *            The count of random users to generate
	 * @return A Set with randomly generated Users
	 */
	public static Set<User> generateRandomUsers(int count) {
		Set<User> randomUsers = new HashSet<>();
		for (int i = 0; i < count; i++) {
			User u = new User();
			u.setFirstName(UUID.randomUUID().toString());
			u.setLastName(UUID.randomUUID().toString());
			u.setUserName(UUID.randomUUID().toString());
			u.setSeed(PasswordHelper.createPass(16));
			u.setPassword(PasswordHelper.encodePassword(PasswordHelper.createPass(12), u.getSeed()));
			u.setUserRoles(Collections.singleton(UserRole.values()[i % UserRole.values().length]));
			randomUsers.add(u);
		}
		return randomUsers;
	}

	/**
	 * Generate but <b>not</b> persist default users
	 *
	 * @param generateDefaultAdmin
	 *            Should the returned Set contains the default admin
	 * @return All existent default users for test purposes
	 */
	public static Set<User> generateDefaultUsers(boolean generateDefaultAdmin) {
		Set<User> defaultUsers = new HashSet<>();
		if (generateDefaultAdmin) {
			defaultUsers.add(createDefaultUser(UserRole.ADMIN, DefaultUserHelper.ADMIN_USERNAME_AND_PASSWORD));
		}
		defaultUsers.add(createDefaultUser(UserRole.SURVEILLANCE_SUPERVISOR, DefaultUserHelper.SURV_SUP_USERNAME_AND_PASSWORD));
		defaultUsers.add(createDefaultUser(UserRole.CASE_SUPERVISOR, DefaultUserHelper.CASE_SUP_USERNAME_AND_PASSWORD));
		defaultUsers.add(createDefaultUser(UserRole.CONTACT_SUPERVISOR, DefaultUserHelper.CONT_SUP_USERNAME_AND_PASSWORD));
		defaultUsers.add(createDefaultUser(UserRole.POE_SUPERVISOR, DefaultUserHelper.POE_SUP_USERNAME_AND_PASSWORD));
		defaultUsers.add(createDefaultUser(UserRole.LAB_USER, DefaultUserHelper.LAB_OFF_USERNAME_AND_PASSWORD));
		defaultUsers.add(createDefaultUser(UserRole.EVENT_OFFICER, DefaultUserHelper.EVE_OFF_USERNAME_AND_PASSWORD));
		defaultUsers.add(createDefaultUser(UserRole.NATIONAL_USER, DefaultUserHelper.NAT_USER_USERNAME_AND_PASSWORD));
		defaultUsers.add(createDefaultUser(UserRole.NATIONAL_CLINICIAN, DefaultUserHelper.NAT_CLIN_USERNAME_AND_PASSWORD));
		defaultUsers.add(createDefaultUser(UserRole.SURVEILLANCE_OFFICER, DefaultUserHelper.SURV_OFF_USERNAME_AND_PASSWORD));
		return defaultUsers;
	}

	public static User createDefaultUser(UserRole role, DataHelper.Pair<String, String> userpass) {
		User user = new User();
		user.setFirstName(userpass.getElement0());
		user.setLastName(userpass.getElement0());
		user.setUserName(userpass.getElement0());
		user.setSeed(PasswordHelper.createPass(16));
		user.setPassword(PasswordHelper.encodePassword(userpass.getElement1(), user.getSeed()));
		user.setUserRoles(Collections.singleton(role));
		return user;
	}

}
