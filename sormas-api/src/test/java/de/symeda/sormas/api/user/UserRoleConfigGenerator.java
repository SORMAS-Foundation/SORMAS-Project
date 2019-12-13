package de.symeda.sormas.api.user;

import org.junit.Test;

import de.symeda.sormas.api.utils.DataHelper;

public class UserRoleConfigGenerator {

	@Test
	public void generateDefaultUserRoleConfig() {
		UserRole userRole = UserRole.HOSPITAL_INFORMANT;
		
		StringBuilder sqlLineBuilder = new StringBuilder();
		sqlLineBuilder.append("INSERT INTO userrolesconfig VALUES (nextval('entity_seq'), ")
			.append("'").append(DataHelper.createUuid()).append("', ")
			.append("now(), now(), ")
			.append("'").append(userRole.name()).append("');");

		System.out.println(sqlLineBuilder.toString());

		for (UserRight userRight : userRole.getDefaultUserRights()) {
			sqlLineBuilder = new StringBuilder();
			sqlLineBuilder.append("INSERT INTO userroles_userrights (userright, userrole_id) VALUES (")
			.append("'").append(userRight.name()).append("', ")
			.append("(SELECT id FROM userrolesconfig WHERE userrole = '").append(userRole.name()).append("'));");
			
			System.out.println(sqlLineBuilder.toString());
		}
		
		
		
	}
	
}
