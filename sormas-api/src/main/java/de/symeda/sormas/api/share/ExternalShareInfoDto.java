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

package de.symeda.sormas.api.share;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class ExternalShareInfoDto extends EntityDto {

	private UserReferenceDto sender;

	private ExternalShareStatus status;

	public static ExternalShareInfoDto build() {
		ExternalShareInfoDto shareInfo = new ExternalShareInfoDto();
		shareInfo.setUuid(DataHelper.createUuid());

		return shareInfo;
	}

	public UserReferenceDto getSender() {
		return sender;
	}

	public void setSender(UserReferenceDto sender) {
		this.sender = sender;
	}

	public ExternalShareStatus getStatus() {
		return status;
	}

	public void setStatus(ExternalShareStatus status) {
		this.status = status;
	}
}
