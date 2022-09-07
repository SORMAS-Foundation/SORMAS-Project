package de.symeda.sormas.api.user;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

public interface FormAccessConfigFacade {


	List<String> getAllUuids();

	List<String> getDeletedUuids(Date date);

	FormAccessConfigDto getByUuid(String uuid);

	FormAccessConfigDto saveUserRoleConfig(@Valid FormAccessConfigDto dto);

	void deleteUserRoleConfig(FormAccessConfigDto dto);

	/**
	 * Will fallback to default user rights for each role that has no configuration defined
	 */
	//Set<UserRight> getEffectiveUserRights(UserRole... userRoles);

	//Set<UserRole> getEnabledUserRoles();

	//Map<UserRole, Set<UserRight>> getAllAsMap();
}
