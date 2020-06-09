/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.location;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class LocationReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -1399197327930368752L;

	public LocationReferenceDto() {

	}

	public LocationReferenceDto(String uuid) {
		this.setUuid(uuid);
	}

	public LocationReferenceDto(String uuid, String caption) {
		this.setUuid(uuid);
		this.setCaption(caption);
	}

	public LocationReferenceDto(String uuid, String regionName, String districtName, String communityName, String city, String address) {
		this.setUuid(uuid);
		this.setCaption(buildCaption(regionName, districtName, communityName, city, address));
	}

	public static String buildCaption(String regionName, String districtName, String communityName, String city, String address) {

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(DataHelper.toStringNullable(regionName));
		if (!DataHelper.isNullOrEmpty(districtName)) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(", ");
			}
			stringBuilder.append(districtName);
		}
		if (!DataHelper.isNullOrEmpty(communityName)) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(", ");
			}
			stringBuilder.append(communityName);
		}
		if (!DataHelper.isNullOrEmpty(city)) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(", ");
			}
			stringBuilder.append(city);
		}
		if (!DataHelper.isNullOrEmpty(address)) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(", ");
			}
			stringBuilder.append(address);
		}
		return stringBuilder.toString();
	}
}
