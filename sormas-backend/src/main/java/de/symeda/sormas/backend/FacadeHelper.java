package de.symeda.sormas.backend;

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
		UserRight editRight) {
		if (existingEntity == null && !userService.hasRight(createRight) || existingEntity != null && !userService.hasRight(editRight)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorForbidden));
		}
	}
}
