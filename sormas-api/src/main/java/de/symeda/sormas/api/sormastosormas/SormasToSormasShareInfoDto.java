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

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class SormasToSormasShareInfoDto extends EntityDto {

	private static final long serialVersionUID = -1478467237560439811L;

	private CaseReferenceDto caze;
	private ContactReferenceDto contact;
	private SampleReferenceDto sample;
	private ServerAccessDataReferenceDto target;
	private UserReferenceDto sender;
	private boolean ownershipHandedOver;
	private boolean withAssociatedContacts;
	private boolean withSamples;
	private boolean pseudonymizedPersonalData;
	private boolean pseudonymizedSensitiveData;
	private String comment;

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public void setCaze(CaseReferenceDto caze) {
		this.caze = caze;
	}

	public ContactReferenceDto getContact() {
		return contact;
	}

	public void setContact(ContactReferenceDto contact) {
		this.contact = contact;
	}

	public SampleReferenceDto getSample() {
		return sample;
	}

	public void setSample(SampleReferenceDto sample) {
		this.sample = sample;
	}

	public ServerAccessDataReferenceDto getTarget() {
		return target;
	}

	public void setTarget(ServerAccessDataReferenceDto target) {
		this.target = target;
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
}
