/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.selfreport;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.QueryJoins;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.location.LocationJoins;
import de.symeda.sormas.backend.user.User;

public class SelfReportJoins extends QueryJoins<SelfReport> {

	private Join<SelfReport, Location> address;
	private LocationJoins addressJoins;
	private Join<SelfReport, User> responsibleUser;

	private Join<SelfReport, Case> caseJoin;

	private Join<SelfReport, Contact> contactJoin;

	public SelfReportJoins(From<?, SelfReport> root) {
		super(root);
	}

	public Join<SelfReport, Location> getAddress() {
		return getOrCreate(address, SelfReport.ADDRESS, JoinType.LEFT, this::setAddress);
	}

	private void setAddress(Join<SelfReport, Location> address) {
		this.address = address;
	}

	public LocationJoins getAddressJoins() {
		return getOrCreate(addressJoins, () -> new LocationJoins(getAddress()), this::setAddressJoins);
	}

	private void setAddressJoins(LocationJoins addressJoins) {
		this.addressJoins = addressJoins;
	}

	public Join<SelfReport, User> getResponsibleUser() {
		return getOrCreate(responsibleUser, SelfReport.RESPONSIBLE_USER, JoinType.LEFT, this::setResponsibleUser);
	}

	private void setResponsibleUser(Join<SelfReport, User> responsibleUser) {
		this.responsibleUser = responsibleUser;
	}

	public Join<SelfReport, Case> getCaseJoin() {
		return getOrCreate(caseJoin, SelfReport.RESULTING_CASE, JoinType.LEFT, this::setCaseJoin);
	}

	public void setCaseJoin(Join<SelfReport, Case> caseJoin) {
		this.caseJoin = caseJoin;
	}

	public Join<SelfReport, Contact> getContactJoin() {
		return getOrCreate(contactJoin, SelfReport.RESULTING_CONTACT, JoinType.LEFT, this::setContactJoin);
	}

	public void setContactJoin(Join<SelfReport, Contact> contactJoin) {
		this.contactJoin = contactJoin;
	}
}
