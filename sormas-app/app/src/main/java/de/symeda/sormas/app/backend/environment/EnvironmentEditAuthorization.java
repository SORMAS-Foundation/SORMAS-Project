package de.symeda.sormas.app.backend.environment;

import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.JurisdictionHelper;

public class EnvironmentEditAuthorization {

	public static boolean isEnvironmentEditAllowed(Environment environment) {

		final User user = ConfigProvider.getUser();
		final EnvironmentJurisdictionBooleanValidator environmentJurisdictionBooleanValidator = EnvironmentJurisdictionBooleanValidator
			.of(JurisdictionHelper.createEnvironmentJurisdictionDto(environment), JurisdictionHelper.createUserJurisdiction(user));

		return environmentJurisdictionBooleanValidator.inJurisdictionOrOwned();
	}
}
