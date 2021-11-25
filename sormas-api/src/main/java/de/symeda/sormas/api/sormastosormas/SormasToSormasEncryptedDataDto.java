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

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.io.Serializable;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.i18n.Validations;

public class SormasToSormasEncryptedDataDto implements Serializable {

	private static final long serialVersionUID = 8658507076136806951L;

	@Size(max = CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String senderId;

	private byte[] data;

	public SormasToSormasEncryptedDataDto() {
	}

	public SormasToSormasEncryptedDataDto(String senderId, byte[] data) {
		this.senderId = senderId;
		this.data = data;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
