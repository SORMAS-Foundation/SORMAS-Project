package de.symeda.sormas.backend;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.user.UserService;

public class FacadeHelper {

	public static void checkCreateAndEditRights(
		AbstractDomainObject existingEntity,
		UserService userService,
		UserRight createRight,
		UserRight editRight,
		UserRight... additionalEditRights) {
		// add editRight and additionalEditRights into a set to check if the user has any of the edit rights
		Set<UserRight> editRights = new HashSet<>();
		editRights.add(editRight);
		editRights.addAll(Arrays.asList(additionalEditRights));

		if (existingEntity == null && !userService.hasRight(createRight) || existingEntity != null && !userService.hasAnyRight(editRights)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorForbidden));
		}
	}

	public static void checkCreateAndEditRights(
		AbstractDomainObject existingEntity,
		UserService userService,
		Set<UserRight> createRights,
		Set<UserRight> editRights) {

		if (existingEntity == null && !userService.hasAnyRight(createRights) || existingEntity != null && !userService.hasAnyRight(editRights)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorForbidden));
		}
	}

	public static void setUuidIfDtoExists(AbstractDomainObject target, EntityDto dto) {
		if (dto != null) {
			target.setUuid(dto.getUuid());
		}
	}

}
