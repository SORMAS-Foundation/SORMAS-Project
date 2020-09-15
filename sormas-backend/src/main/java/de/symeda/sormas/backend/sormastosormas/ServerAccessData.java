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

import de.symeda.sormas.api.sormastosormas.ServerAccessDataReferenceDto;

public class ServerAccessData {

	private String healthDepartmentId;
	private String healthDepartmentName;
	private String restUserPassword;

	public String getHealthDepartmentId() {
		return healthDepartmentId;
	}

	public void setHealthDepartmentId(String healthDepartmentId) {
		this.healthDepartmentId = healthDepartmentId;
	}

	public String getHealthDepartmentName() {
		return healthDepartmentName;
	}

	public void setHealthDepartmentName(String healthDepartmentName) {
		this.healthDepartmentName = healthDepartmentName;
	}

	public String getRestUserPassword() {
		return restUserPassword;
	}

	public void setRestUserPassword(String restUserPassword) {
		this.restUserPassword = restUserPassword;
	}

	public ServerAccessDataReferenceDto toReference() {
		return new ServerAccessDataReferenceDto(healthDepartmentId, healthDepartmentName);
	}
}
