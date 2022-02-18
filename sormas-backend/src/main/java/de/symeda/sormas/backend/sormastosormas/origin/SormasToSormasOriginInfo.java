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

package de.symeda.sormas.backend.sormastosormas.origin;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_BIG;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sormastosormas.share.sharerequest.SormasToSormasShareRequest;

@Entity(name = "sormastosormasorigininfo")
public class SormasToSormasOriginInfo extends AbstractDomainObject {

	private static final long serialVersionUID = -842917698322793413L;

	public static final String TABLE_NAME = "sormastosormasorigininfo";

	public static final String ORGANIZATION_ID = "organizationId";
	public static final String SENDER_NAME = "senderName";
	public static final String SENDER_EMAIL = "senderEmail";
	public static final String SENDER_PHONE_NUMBER = "senderPhoneNumber";
	public static final String OWNERSHIP_HANDED_OVER = "ownershipHandedOver";
	public static final String COMMENT = "comment";
	public static final String CASES = "cases";
	public static final String CONTACTS = "contacts";
	public static final String EVENT_PARTICIPANTS = "eventParticipants";

	private String organizationId;

	private String senderName;

	private String senderEmail;

	private String senderPhoneNumber;

	private boolean ownershipHandedOver;

	private boolean withAssociatedContacts;

	private boolean withSamples;

	private boolean withEventParticipants;

	private boolean withImmunizations;

	private String comment;

	private SormasToSormasShareRequest request;

	private List<Case> cases;

	private List<Contact> contacts;

	private List<Event> events;

	private List<EventParticipant> eventParticipants;

	private List<Sample> samples;

	@Column(length = CHARACTER_LIMIT_DEFAULT, nullable = false)
	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT, nullable = false)
	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getSenderPhoneNumber() {
		return senderPhoneNumber;
	}

	public void setSenderPhoneNumber(String senderPhoneNumber) {
		this.senderPhoneNumber = senderPhoneNumber;
	}

	@Column
	public boolean isOwnershipHandedOver() {
		return ownershipHandedOver;
	}

	public void setOwnershipHandedOver(boolean ownershipHandedOver) {
		this.ownershipHandedOver = ownershipHandedOver;
	}

	@Column
	public boolean isWithAssociatedContacts() {
		return withAssociatedContacts;
	}

	public void setWithAssociatedContacts(boolean withContacts) {
		this.withAssociatedContacts = withContacts;
	}

	@Column
	public boolean isWithSamples() {
		return withSamples;
	}

	public void setWithSamples(boolean withSamples) {
		this.withSamples = withSamples;
	}

	@Column
	public boolean isWithEventParticipants() {
		return withEventParticipants;
	}

	public void setWithEventParticipants(boolean withEventParticipants) {
		this.withEventParticipants = withEventParticipants;
	}

	@Column
	public boolean isWithImmunizations() {
		return withImmunizations;
	}

	public void setWithImmunizations(boolean withImmunizations) {
		this.withImmunizations = withImmunizations;
	}

	@OneToOne(mappedBy = "originInfo")
	public SormasToSormasShareRequest getRequest() {
		return request;
	}

	public void setRequest(SormasToSormasShareRequest request) {
		this.request = request;
	}

	@Column(length = CHARACTER_LIMIT_BIG)
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sormasToSormasOriginInfo", orphanRemoval = true)
	@AuditedIgnore
	public List<Case> getCases() {
		return cases;
	}

	public void setCases(List<Case> cases) {
		this.cases = cases;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sormasToSormasOriginInfo", orphanRemoval = true)
	@AuditedIgnore
	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sormasToSormasOriginInfo", orphanRemoval = true)
	@AuditedIgnore
	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sormasToSormasOriginInfo", orphanRemoval = true)
	@AuditedIgnore
	public List<EventParticipant> getEventParticipants() {
		return eventParticipants;
	}

	public void setEventParticipants(List<EventParticipant> eventParticipants) {
		this.eventParticipants = eventParticipants;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sormasToSormasOriginInfo", orphanRemoval = true)
	@AuditedIgnore
	public List<Sample> getSamples() {
		return samples;
	}

	public void setSamples(List<Sample> samples) {
		this.samples = samples;
	}
}
