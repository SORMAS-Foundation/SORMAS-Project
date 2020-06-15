package de.symeda.sormas.app.backend.caze;

import de.symeda.sormas.api.utils.jurisdiction.CaseJurisdictionHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.JurisdictionHelper;

public class CaseEditAuthorization {

	public static Boolean isCaseEditAllowed(Case caze) {
		User user = ConfigProvider.getUser();
		return CaseJurisdictionHelper.isInJurisdiction(
			ConfigProvider::hasRole,
			JurisdictionHelper.createUserJurisdiction(user),
			JurisdictionHelper.createCaseJurisdictionDto(caze));
	}
}
