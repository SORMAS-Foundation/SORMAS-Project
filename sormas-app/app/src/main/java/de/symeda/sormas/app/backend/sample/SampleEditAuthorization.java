package de.symeda.sormas.app.backend.sample;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.jurisdiction.SampleJurisdictionHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.JurisdictionHelper;

public class SampleEditAuthorization {

	public static boolean isSampleEditAllowed(Sample sample) {
		User user = ConfigProvider.getUser();

		return SampleJurisdictionHelper.isInJurisdiction(
			UserRole.getJurisdictionLevel(user.getUserRoles()),
			JurisdictionHelper.createUserJurisdiction(user),
			JurisdictionHelper.createSampleJurisdictionDto(sample));
	}
}
