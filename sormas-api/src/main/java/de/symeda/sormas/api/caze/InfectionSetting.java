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

package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum InfectionSetting {

	UNKNOWN(null),
	AMBULATORY(null),
	MEDICAL_PRACTICE(AMBULATORY),
	OPERATIVE_1200(AMBULATORY),
	HOSPITAL_1300(AMBULATORY),
	OTHER_OUTPATIENT_FACILITY(AMBULATORY),
	STATIONARY(null),
	HOSPITAL_2100(STATIONARY),
	NORMAL_WARD(HOSPITAL_2100),
	OPERATIVE_2111(NORMAL_WARD),
	NOT_OPERATIVE(NORMAL_WARD),
	HEMATOLOGICAL_ONCOLOGY(NORMAL_WARD),
	CHILDREN_WARD(HOSPITAL_2100),
	NEONATOLOGY(HOSPITAL_2100),
	INTENSIVE_CARE_UNIT(HOSPITAL_2100),
	OTHER_STATION(HOSPITAL_2100),
	NURSING_HOME(STATIONARY),
	REHAB_FACILITY(STATIONARY),
	OTHER_STATIONARY_FACILITY(STATIONARY);

	private final InfectionSetting parent;

	InfectionSetting(InfectionSetting parent) {
		this.parent = parent;
	}

	public InfectionSetting getParent() {
		return this.parent;
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
