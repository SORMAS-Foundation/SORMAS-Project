package de.symeda.sormas.api.user;

import org.junit.Test;

import de.symeda.sormas.api.utils.DataHelper;

public class UserRoleConfigGenerator {

	/**
	 * Prints the SQL needed to create the userrolesconfig for a user role with it's default user rights
	 */
	@SuppressWarnings("unused")
	@Test
	public void printDefaultUserRoleConfig() {

		UserRole userRole = null;// UserRole.HOSPITAL_INFORMANT;

		if (userRole != null) {
			StringBuilder sqlLineBuilder = new StringBuilder();
			sqlLineBuilder.append("INSERT INTO userrolesconfig VALUES (nextval('entity_seq'), ")
				.append("'")
				.append(DataHelper.createUuid())
				.append("', ")
				.append("now(), now(), ")
				.append("'")
				.append(userRole.name())
				.append("');");

			System.out.println(sqlLineBuilder.toString());

			for (UserRight userRight : userRole.getDefaultUserRights()) {
				sqlLineBuilder = new StringBuilder();
				sqlLineBuilder.append("INSERT INTO userroles_userrights (userright, userrole_id) VALUES (")
					.append("'")
					.append(userRight.name())
					.append("', ")
					.append("(SELECT id FROM userrolesconfig WHERE userrole = '")
					.append(userRole.name())
					.append("'));");

				System.out.println(sqlLineBuilder.toString());
			}
		}
	}
}
