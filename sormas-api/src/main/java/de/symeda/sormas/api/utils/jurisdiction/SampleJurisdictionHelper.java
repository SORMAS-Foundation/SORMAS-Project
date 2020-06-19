/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.utils.jurisdiction;

import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.contact.ContactJurisdictionDto;
import de.symeda.sormas.api.user.JurisdictionLevel;

public class SampleJurisdictionHelper {

	public static boolean isInJurisdiction(
		JurisdictionLevel jurisdictionLevel,
		UserJurisdiction userJurisdiction,
		CaseJurisdictionDto associatedCaseJurisdiction,
		ContactJurisdictionDto associatedContactJurisdiction) {
		if (associatedCaseJurisdiction != null) {
			return CaseJurisdictionHelper.isInJurisdiction(jurisdictionLevel, userJurisdiction, associatedCaseJurisdiction);
		}

		if (associatedContactJurisdiction != null) {
			return ContactJurisdictionHelper.isInJurisdiction(jurisdictionLevel, userJurisdiction, associatedContactJurisdiction);
		}

		return true;
	}
}
