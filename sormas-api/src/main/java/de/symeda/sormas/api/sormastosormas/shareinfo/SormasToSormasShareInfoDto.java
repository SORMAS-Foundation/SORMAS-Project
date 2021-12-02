/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.sormastosormas.shareinfo;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sormastosormas.SormasServerDescriptor;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestStatus;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.FieldConstraints;

public class SormasToSormasShareInfoDto extends EntityDto {

	private static final long serialVersionUID = -1478467237560439811L;

	private SormasServerDescriptor targetDescriptor;
	private UserReferenceDto sender;
	private boolean ownershipHandedOver;
	private boolean withAssociatedContacts;
	private boolean withSamples;
	private boolean withEvenParticipants;
	private boolean withImmunizations;
	private boolean pseudonymizedPersonalData;
	private boolean pseudonymizedSensitiveData;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String comment;
	private ShareRequestStatus requestStatus;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String responseComment;

	/**
	 * @return Get the target server of the share operation.
	 */
	public SormasServerDescriptor getTargetDescriptor() {
		return targetDescriptor;
	}

	/**
	 * @param targetDescriptor
	 *            The target server of the share operation.
	 */
	public void setTargetDescriptor(SormasServerDescriptor targetDescriptor) {
		this.targetDescriptor = targetDescriptor;
	}

	public UserReferenceDto getSender() {
		return sender;
	}

	public void setSender(UserReferenceDto sender) {
		this.sender = sender;
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

	public boolean isWithEvenParticipants() {
		return withEvenParticipants;
	}

	public void setWithEvenParticipants(boolean withEvenParticipants) {
		this.withEvenParticipants = withEvenParticipants;
	}

	public boolean isWithImmunizations() {
		return withImmunizations;
	}

	public void setWithImmunizations(boolean withImmunizations) {
		this.withImmunizations = withImmunizations;
	}

	public boolean isPseudonymizedPersonalData() {
		return pseudonymizedPersonalData;
	}

	public void setPseudonymizedPersonalData(boolean pseudonymizedPersonalData) {
		this.pseudonymizedPersonalData = pseudonymizedPersonalData;
	}

	public boolean isPseudonymizedSensitiveData() {
		return pseudonymizedSensitiveData;
	}

	public void setPseudonymizedSensitiveData(boolean pseudonymizedSensitiveData) {
		this.pseudonymizedSensitiveData = pseudonymizedSensitiveData;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public ShareRequestStatus getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(ShareRequestStatus requestStatus) {
		this.requestStatus = requestStatus;
	}

	public String getResponseComment() {
		return responseComment;
	}

	public void setResponseComment(String responseComment) {
		this.responseComment = responseComment;
	}
}
