package de.symeda.sormas.api.user;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

public interface UserTypeConfigFacade {


	List<String> getAllUuids();

	List<String> getDeletedUuids(Date date);

	UserTypeConfigDto getByUuid(String uuid);

	UserTypeConfigDto saveUserRoleConfig(@Valid UserTypeConfigDto dto);

	void deleteUserRoleConfig(UserTypeConfigDto dto);

	/**
	 * Will fallback to default user rights for each role that has no configuration defined
	 */
	//Set<UserRight> getEffectiveUserRights(UserRole... userRoles);

	//Set<UserRole> getEnabledUserRoles();

	//Map<UserRole, Set<UserRight>> getAllAsMap();
}
