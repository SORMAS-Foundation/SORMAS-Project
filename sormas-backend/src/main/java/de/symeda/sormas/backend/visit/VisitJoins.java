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

package de.symeda.sormas.backend.visit;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.AbstractDomainObjectJoins;

public class VisitJoins extends AbstractDomainObjectJoins<Visit, Visit> {

	private Join<Visit, Contact> contacts;
	private Join<Contact, User> contactReportingUser;
	private Join<Contact, Region> contactRegion;
	private Join<Contact, District> contactDistrict;
	private Join<Contact, Case> contactCase;
	private Join<Case, User> contactCaseReportingUser;
	private Join<Case, Region> contactCaseRegion;
	private Join<Case, District> contactCaseDistrict;
	private Join<Case, Community> contactCaseCommunity;
	private Join<Case, Facility> contactCaseHealthFacility;
	private Join<Case, PointOfEntry> contactCasePointOfEntry;

	private JoinType contactJoinType;

	public VisitJoins(From<Visit, Visit> root, JoinType contactJoinType) {
		super(root);

		this.contactJoinType = contactJoinType;
	}

	public Join<Visit, Contact> getContacts() {
		return getOrCreate(contacts, Visit.CONTACTS, contactJoinType, this::setContacts);
	}

	public Join<Contact, User> getContactReportingUser() {
		return getOrCreate(contactReportingUser, Contact.REPORTING_USER, JoinType.LEFT, getContacts(), this::setContactReportingUser);
	}

	private void setContactReportingUser(Join<Contact, User> contactReportingUser) {
		this.contactReportingUser = contactReportingUser;
	}

	private void setContacts(Join<Visit, Contact> contacts) {
		this.contacts = contacts;
	}

	public Join<Contact, Region> getContactRegion() {
		return getOrCreate(contactRegion, Contact.REGION, JoinType.LEFT, getContacts(), this::setContactRegion);
	}

	private void setContactRegion(Join<Contact, Region> contactRegion) {
		this.contactRegion = contactRegion;
	}

	public Join<Contact, District> getContactDistrict() {
		return getOrCreate(contactDistrict, Contact.DISTRICT, JoinType.LEFT, getContacts(), this::setContactDistrict);
	}

	private void setContactDistrict(Join<Contact, District> contactDistrict) {
		this.contactDistrict = contactDistrict;
	}

	public Join<Contact, Case> getContactCase() {
		return getOrCreate(contactCase, Contact.CAZE, JoinType.LEFT, getContacts(), this::setContactCase);
	}

	private void setContactCase(Join<Contact, Case> contactCase) {
		this.contactCase = contactCase;
	}

	public Join<Case, User> getContactCaseReportingUser() {
		return getOrCreate(contactCaseReportingUser, Case.REPORTING_USER, JoinType.LEFT, getContactCase(), this::setContactCaseReportingUser);
	}

	private void setContactCaseReportingUser(Join<Case, User> contactCaseReportingUser) {
		this.contactCaseReportingUser = contactCaseReportingUser;
	}

	public Join<Case, Region> getContactCaseRegion() {
		return getOrCreate(contactCaseRegion, Case.REGION, JoinType.LEFT, getContactCase(), this::setContactCaseRegion);
	}

	private void setContactCaseRegion(Join<Case, Region> contactCaseRegion) {
		this.contactCaseRegion = contactCaseRegion;
	}

	public Join<Case, District> getContactCaseDistrict() {
		return getOrCreate(contactCaseDistrict, Case.DISTRICT, JoinType.LEFT, getContactCase(), this::setContactCaseDistrict);
	}

	private void setContactCaseDistrict(Join<Case, District> contactCaseDistrict) {
		this.contactCaseDistrict = contactCaseDistrict;
	}

	public Join<Case, Community> getContactCaseCommunity() {
		return getOrCreate(contactCaseCommunity, Case.COMMUNITY, JoinType.LEFT, getContactCase(), this::setContactCaseCommunity);
	}

	private void setContactCaseCommunity(Join<Case, Community> contactCaseCommunity) {
		this.contactCaseCommunity = contactCaseCommunity;
	}

	public Join<Case, Facility> getContactCaseHealthFacility() {
		return getOrCreate(contactCaseHealthFacility, Case.HEALTH_FACILITY, JoinType.LEFT, getContactCase(), this::setContactCaseHealthFacility);
	}

	private void setContactCaseHealthFacility(Join<Case, Facility> contactCaseHealthFacility) {
		this.contactCaseHealthFacility = contactCaseHealthFacility;
	}

	public Join<Case, PointOfEntry> getContactCasePointOfEntry() {
		return getOrCreate(contactCasePointOfEntry, Case.POINT_OF_ENTRY, JoinType.LEFT, getContactCase(), this::setContactCasePointOfEntry);
	}

	private void setContactCasePointOfEntry(Join<Case, PointOfEntry> contactCasePointOfEntry) {
		this.contactCasePointOfEntry = contactCasePointOfEntry;
	}
}
