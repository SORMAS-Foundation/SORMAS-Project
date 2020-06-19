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

package de.symeda.sormas.backend.sample;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.contact.ContactJurisdictionDto;
import de.symeda.sormas.api.utils.jurisdiction.SampleJurisdictionHelper;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.JurisdictionHelper;

@Stateless(name = "SampleJurisdictionChecker")
@LocalBean
public class SampleJurisdictionChecker {

	@EJB
	private UserService userService;

	public boolean isInJurisdiction(Sample sample) {
		return isInJurisdiction(
			JurisdictionHelper.createCaseJurisdictionDto(sample.getAssociatedCase()),
			JurisdictionHelper.createContactJurisdictionDto(sample.getAssociatedContact()));
	}

	public boolean isInJurisdiction(CaseJurisdictionDto sampleCaseJurisdiction, ContactJurisdictionDto sampleContactJurisdiction) {
		User user = userService.getCurrentUser();

		return SampleJurisdictionHelper.isInJurisdiction(
			user.getJurisdictionLevel(),
			JurisdictionHelper.createUserJurisdiction(user),
			sampleCaseJurisdiction,
			sampleContactJurisdiction);
	}
}
