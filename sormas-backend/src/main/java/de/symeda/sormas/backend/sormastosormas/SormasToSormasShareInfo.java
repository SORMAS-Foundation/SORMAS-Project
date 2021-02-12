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

package de.symeda.sormas.backend.sormastosormas;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_BIG;
import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.user.User;

@Entity(name = "sormastosormasshareinfo")
public class SormasToSormasShareInfo extends AbstractDomainObject {

	private static final long serialVersionUID = -8368155805122562791L;

	public static final String CAZE = "caze";
	public static final String CONTACT = "contact";
	public static final String SAMPLE = "sample";
	public static final String EVENT = "event";
	public static final String OWNERSHIP_HANDED_OVER = "ownershipHandedOver";
	public static final String ORGANIZATION_ID = "organizationId";

	private Case caze;

	private Contact contact;

	private Sample sample;

	private Event event;

	private String organizationId;

	private User sender;

	private boolean ownershipHandedOver;

	private boolean withAssociatedContacts;

	private boolean withSamples;

	private boolean pseudonymizedPersonalData;

	private boolean pseudonymizedSensitiveData;

	private String comment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	public Case getCaze() {
		return caze;
	}

	public void setCaze(Case caze) {
		this.caze = caze;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT, nullable = false)
	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	@ManyToOne
	@JoinColumn(nullable = false)
	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
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

	public void setWithAssociatedContacts(boolean withAssociatedContacts) {
		this.withAssociatedContacts = withAssociatedContacts;
	}

	@Column
	public boolean isWithSamples() {
		return withSamples;
	}

	public void setWithSamples(boolean withSamples) {
		this.withSamples = withSamples;
	}

	@Column
	public boolean isPseudonymizedPersonalData() {
		return pseudonymizedPersonalData;
	}

	public void setPseudonymizedPersonalData(boolean pseudonymizedPersonalData) {
		this.pseudonymizedPersonalData = pseudonymizedPersonalData;
	}

	@Column
	public boolean isPseudonymizedSensitiveData() {
		return pseudonymizedSensitiveData;
	}

	public void setPseudonymizedSensitiveData(boolean pseudonymizedSensitiveData) {
		this.pseudonymizedSensitiveData = pseudonymizedSensitiveData;
	}

	public String getComment() {
		return comment;
	}

	@Column(length = COLUMN_LENGTH_BIG)
	public void setComment(String comment) {
		this.comment = comment;
	}
}
