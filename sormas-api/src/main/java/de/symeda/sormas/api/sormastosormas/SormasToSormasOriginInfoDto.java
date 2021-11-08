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

package de.symeda.sormas.api.sormastosormas;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.FieldConstraints;

public class SormasToSormasOriginInfoDto extends EntityDto {

	private static final long serialVersionUID = -4921614789451304041L;

	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String organizationId;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String senderName;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String senderEmail;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String senderPhoneNumber;
	private boolean ownershipHandedOver;
	private boolean withAssociatedContacts;
	private boolean withSamples;
	private boolean withEventParticipants;
	private boolean withImmunizations;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String comment;

	public SormasToSormasOriginInfoDto() {
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public String getSenderPhoneNumber() {
		return senderPhoneNumber;
	}

	public void setSenderPhoneNumber(String senderPhoneNumber) {
		this.senderPhoneNumber = senderPhoneNumber;
	}

	public boolean isOwnershipHandedOver() {
		return ownershipHandedOver;
	}

	public void setOwnershipHandedOver(boolean ownershipHandedOver) {
		this.ownershipHandedOver = ownershipHandedOver;
	}

	public boolean isWithAssociatedContacts() {
		return withAssociatedContacts;
	}

	public void setWithAssociatedContacts(boolean withAssociatedContacts) {
		this.withAssociatedContacts = withAssociatedContacts;
	}

	public boolean isWithSamples() {
		return withSamples;
	}

	public void setWithSamples(boolean withSamples) {
		this.withSamples = withSamples;
	}

	public boolean isWithEventParticipants() {
		return withEventParticipants;
	}

	public void setWithEventParticipants(boolean withEventParticipants) {
		this.withEventParticipants = withEventParticipants;
	}

	public boolean isWithImmunizations() {
		return withImmunizations;
	}

	public void setWithImmunizations(boolean withImmunizations) {
		this.withImmunizations = withImmunizations;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
