/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.immunization;

import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.sample.SampleJurisdictionBooleanValidator;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.JurisdictionHelper;

public class ImmunizationEditAuthorization {

    public static boolean isImmunizationEditAllowed(Immunization immunization) {

        if (immunization.getSormasToSormasOriginInfo() != null) {
            return immunization.getSormasToSormasOriginInfo().isOwnershipHandedOver();
        }

        final User user = ConfigProvider.getUser();
        final ImmunizationJurisdictionBooleanValidator jurisdictionBooleanValidator =
                ImmunizationJurisdictionBooleanValidator.of(JurisdictionHelper.createImmunizationJurisdictionDto(immunization), JurisdictionHelper.createUserJurisdiction(user));
        return !immunization.isOwnershipHandedOver() && jurisdictionBooleanValidator.inJurisdictionOrOwned();

    }
}
