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

package de.symeda.sormas.app.epidata;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.epidata.EpiData;

public class EpiDataFragmentHelper {

	private EpiDataFragmentHelper() {
	}

	public static Disease getDiseaseOfCaseOrContact(AbstractDomainObject abstractDomainObject) {
		if (abstractDomainObject instanceof Case) {
			Case caze = (Case) abstractDomainObject;
			return caze.getDisease();
		} else {
			Contact contact = (Contact) abstractDomainObject;
			return contact.getDisease();
		}
	}

	public static EpiData getEpiDataOfCaseOrContact(AbstractDomainObject abstractDomainObject) {
		if (abstractDomainObject instanceof Case) {
			Case caze = (Case) abstractDomainObject;
			return caze.getEpiData();
		} else {
			Contact contact = (Contact) abstractDomainObject;
			return contact.getEpiData();
		}
	}
}
