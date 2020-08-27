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
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class SormasToSormasShareInfoDto extends EntityDto {

	private static final long serialVersionUID = -1478467237560439811L;

	private CaseReferenceDto caze;
	private ContactReferenceDto contact;
	private HealthDepartmentServerReferenceDto healthDepartment;
	private UserReferenceDto sender;
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

	public HealthDepartmentServerReferenceDto getHealthDepartment() {
		return healthDepartment;
	}

	public void setHealthDepartment(HealthDepartmentServerReferenceDto healthDepartment) {
		this.healthDepartment = healthDepartment;
	}

	public UserReferenceDto getSender() {
		return sender;
	}

	public void setSender(UserReferenceDto sender) {
		this.sender = sender;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public static SormasToSormasShareInfoDto forCase(CaseReferenceDto caze) {
		SormasToSormasShareInfoDto shareInfo = new SormasToSormasShareInfoDto();
		shareInfo.setUuid(DataHelper.createUuid());
		shareInfo.setCaze(caze);

		return shareInfo;
	}

	public static SormasToSormasShareInfoDto forContact(ContactReferenceDto contact) {
		SormasToSormasShareInfoDto shareInfo = new SormasToSormasShareInfoDto();
		shareInfo.setUuid(DataHelper.createUuid());
		shareInfo.setContact(contact);

		return shareInfo;
	}
}
