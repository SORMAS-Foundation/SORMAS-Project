/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.environment.environmentsample;

import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.JurisdictionHelper;

public class EnvironmentSampleEditAuthorization {

	public static boolean isEnvironmentSampleEditAllowed(EnvironmentSample sample) {

		final User user = ConfigProvider.getUser();
		final EnvironmentSampleJurisdictionBooleanValidator environmentSampleJurisdictionBooleanValidator =
			EnvironmentSampleJurisdictionBooleanValidator
				.of(JurisdictionHelper.createEnvironmentSampleJurisdictionDto(sample), JurisdictionHelper.createUserJurisdiction(user));

		return environmentSampleJurisdictionBooleanValidator.inJurisdictionOrOwned();
	}
}
