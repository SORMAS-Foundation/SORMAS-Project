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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.user.User;

@Entity(name = "sormastosormasshareinfo")
public class SormasToSormasShareInfo extends AbstractDomainObject {

	private static final long serialVersionUID = -8368155805122562791L;

	public static final String CAZE = "caze";
	public static final String CONTACT = "contact";
	public static final String SAMPLE = "sample";
	public static final String OWNERSHIP_HANDED_OVER = "ownershipHandedOver";
	public static final String ORGANIZATION_ID = "organizationId";

	private Case caze;

	private Contact contact;

	private Sample sample;

	private String organizationId;

	private boolean ownershipHandedOver;

	private User sender;

	private String comment;

	@ManyToOne
	@JoinColumn
	public Case getCaze() {
		return caze;
	}

	public void setCaze(Case caze) {
		this.caze = caze;
	}

	@ManyToOne
	@JoinColumn
	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	@ManyToOne
	@JoinColumn
	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT, nullable = false)
	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	@Column
	public boolean isOwnershipHandedOver() {
		return ownershipHandedOver;
	}

	public void setOwnershipHandedOver(boolean ownershipHandedOver) {
		this.ownershipHandedOver = ownershipHandedOver;
	}

	@ManyToOne
	@JoinColumn(nullable = false)
	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public String getComment() {
		return comment;
	}

	@Column(length = COLUMN_LENGTH_BIG)
	public void setComment(String comment) {
		this.comment = comment;
	}
}
