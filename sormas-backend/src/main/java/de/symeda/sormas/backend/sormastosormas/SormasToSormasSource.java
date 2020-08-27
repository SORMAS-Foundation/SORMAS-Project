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

import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity(name = "sormastosormassource")
public class SormasToSormasSource extends AbstractDomainObject {

	private static final long serialVersionUID = -842917698322793413L;

	private String healthDepartment;

	private String senderName;

	private String senderEmail;

	private String senderPhoneNumber;

	private String comment;

	@Column(length = COLUMN_LENGTH_DEFAULT, nullable = false)
	public String getHealthDepartment() {
		return healthDepartment;
	}

	public void setHealthDepartment(String healthDepartment) {
		this.healthDepartment = healthDepartment;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT, nullable = false)
	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getSenderPhoneNumber() {
		return senderPhoneNumber;
	}

	public void setSenderPhoneNumber(String senderPhoneNumber) {
		this.senderPhoneNumber = senderPhoneNumber;
	}

	@Column(length = COLUMN_LENGTH_BIG)
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
