package de.symeda.sormas.app.backend.caze;

import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.JurisdictionHelper;

public class CaseEditAuthorization {

	public static Boolean isCaseEditAllowed(Case caze) {

		if (caze.getSormasToSormasOriginInfo() != null) {
			return caze.getSormasToSormasOriginInfo().isOwnershipHandedOver();
		}

		final User user = ConfigProvider.getUser();
		final CaseJurisdictionBooleanValidator caseJurisdictionBooleanValidator =
				CaseJurisdictionBooleanValidator.of(JurisdictionHelper.createCaseJurisdictionDto(caze), JurisdictionHelper.createUserJurisdiction(user));
		return !caze.isOwnershipHandedOver() && caseJurisdictionBooleanValidator.inJurisdictionOrOwned();
	}
}
