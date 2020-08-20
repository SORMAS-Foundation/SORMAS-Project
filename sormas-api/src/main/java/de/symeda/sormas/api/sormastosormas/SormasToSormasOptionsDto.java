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

import java.io.Serializable;

public class SormasToSormasOptionsDto implements Serializable {

	public static final String I18N_PREFIX = "SormasToSormasOptions";

	public static final String HEALTH_DEPARTMENT = "healthDepartment";
	public static final String PSEUDONYMIZE_PERSONAL_DATA = "pseudonymizePersonalData";
	public static final String PSEUDONYMIZE_SENSITIVE_DATA = "pseudonymizeSensitiveData";

	private String healthDepartment;

	private boolean pseudonymizePersonalData;

	private boolean pseudonymizeSensitiveData;

	public String getHealthDepartment() {
		return healthDepartment;
	}

	public void setHealthDepartment(String healthDepartment) {
		this.healthDepartment = healthDepartment;
	}

	public boolean isPseudonymizePersonalData() {
		return pseudonymizePersonalData;
	}

	public void setPseudonymizePersonalData(boolean pseudonymizePersonalData) {
		this.pseudonymizePersonalData = pseudonymizePersonalData;
	}

	public boolean isPseudonymizeSensitiveData() {
		return pseudonymizeSensitiveData;
	}

	public void setPseudonymizeSensitiveData(boolean pseudonymizeSensitiveData) {
		this.pseudonymizeSensitiveData = pseudonymizeSensitiveData;
	}
}
