package de.symeda.sormas.app.backend.sample;

import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.JurisdictionHelper;

public class SampleEditAuthorization {

	public static boolean isSampleEditAllowed(Sample sample) {

		if (sample.getSormasToSormasOriginInfo() != null) {
			return sample.getSormasToSormasOriginInfo().isOwnershipHandedOver();
		}

		final User user = ConfigProvider.getUser();
		final SampleJurisdictionBooleanValidator validator =
				SampleJurisdictionBooleanValidator.of(JurisdictionHelper.createSampleJurisdictionDto(sample), JurisdictionHelper.createUserJurisdiction(user));
		return !sample.isOwnershipHandedOver() && validator.inJurisdictionOrOwned();
	}
}
