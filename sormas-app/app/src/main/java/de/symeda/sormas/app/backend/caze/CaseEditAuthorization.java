package de.symeda.sormas.app.backend.caze;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.jurisdiction.CaseJurisdictionHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.JurisdictionHelper;

public class CaseEditAuthorization {

	public static Boolean isCaseEditAllowed(Case caze) {
		User user = ConfigProvider.getUser();
		return CaseJurisdictionHelper.isInJurisdiction(
			UserRole.getJurisdictionLevel(user.getUserRoles()),
			JurisdictionHelper.createUserJurisdiction(user),
			JurisdictionHelper.createCaseJurisdictionDto(caze));
	}
}
