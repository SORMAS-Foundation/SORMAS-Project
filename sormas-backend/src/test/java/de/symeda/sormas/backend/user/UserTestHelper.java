package de.symeda.sormas.backend.user;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DefaultEntityHelper;
import de.symeda.sormas.api.utils.PasswordHelper;
import de.symeda.sormas.backend.TestDataCreator;

public class UserTestHelper {

	/**
	 * Generate <code>count</code> random users with UUID as firstname, lastname, username and random password. The
	 * role is determined by the countposition % UserRole.values().length
	 *
	 * @param count
	 *            The count of random users to generate
	 * @param creator
	 * @return A Set with randomly generated Users
	 */
	public static Set<User> generateRandomUsers(int count, TestDataCreator creator) {
		Set<User> randomUsers = new HashSet<>();
		SecureRandom rand = new SecureRandom();
		for (int i = 0; i < count; i++) {
			User u = new User();
			u.setFirstName(UUID.randomUUID().toString());
			u.setLastName(UUID.randomUUID().toString());
			u.setUserName(UUID.randomUUID().toString());
			u.setSeed(PasswordHelper.createPass(16));
			u.setPassword(PasswordHelper.encodePassword(PasswordHelper.createPass(12), u.getSeed()));
			u.setUserRoles(Collections.singleton(creator.getUserRole(DefaultUserRole.values()[rand.nextInt(DefaultUserRole.values().length)])));
			u.updateJurisdictionLevel();
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
	public static Set<User> generateDefaultUsers(boolean generateDefaultAdmin, TestDataCreator creator) {
		Set<User> defaultUsers = new HashSet<>();
		if (generateDefaultAdmin) {
			defaultUsers.add(createDefaultUser(DefaultUserRole.ADMIN, DefaultEntityHelper.ADMIN_USERNAME_AND_PASSWORD, creator));
		}
		defaultUsers.add(createDefaultUser(DefaultUserRole.SURVEILLANCE_SUPERVISOR, DefaultEntityHelper.SURV_SUP_USERNAME_AND_PASSWORD, creator));
		defaultUsers.add(createDefaultUser(DefaultUserRole.CASE_SUPERVISOR, DefaultEntityHelper.CASE_SUP_USERNAME_AND_PASSWORD, creator));
		defaultUsers.add(createDefaultUser(DefaultUserRole.CONTACT_SUPERVISOR, DefaultEntityHelper.CONT_SUP_USERNAME_AND_PASSWORD, creator));
		defaultUsers.add(createDefaultUser(DefaultUserRole.POE_SUPERVISOR, DefaultEntityHelper.POE_SUP_USERNAME_AND_PASSWORD, creator));
		defaultUsers.add(createDefaultUser(DefaultUserRole.LAB_USER, DefaultEntityHelper.LAB_OFF_USERNAME_AND_PASSWORD, creator));
		defaultUsers.add(createDefaultUser(DefaultUserRole.EVENT_OFFICER, DefaultEntityHelper.EVE_OFF_USERNAME_AND_PASSWORD, creator));
		defaultUsers.add(createDefaultUser(DefaultUserRole.NATIONAL_USER, DefaultEntityHelper.NAT_USER_USERNAME_AND_PASSWORD, creator));
		defaultUsers.add(createDefaultUser(DefaultUserRole.NATIONAL_CLINICIAN, DefaultEntityHelper.NAT_CLIN_USERNAME_AND_PASSWORD, creator));
		defaultUsers.add(createDefaultUser(DefaultUserRole.SURVEILLANCE_OFFICER, DefaultEntityHelper.SURV_OFF_USERNAME_AND_PASSWORD, creator));
		return defaultUsers;
	}

	public static User createDefaultUser(DefaultUserRole role, DataHelper.Pair<String, String> userpass, TestDataCreator creator) {
		User user = new User();
		user.setFirstName(userpass.getElement0());
		user.setLastName(userpass.getElement0());
		user.setUserName(userpass.getElement0());
		user.setSeed(PasswordHelper.createPass(16));
		user.setPassword(PasswordHelper.encodePassword(userpass.getElement1(), user.getSeed()));
		user.setUserRoles(Collections.singleton(creator.getUserRole(role)));
		user.updateJurisdictionLevel();
		return user;
	}

}
