/*
 *  SORMAS® - Surveillance Outbreak Response Management & Analysis System
 *  Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.symeda.sormas.api.event;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum LaboratoryDiagnosticEvidenceDetail {

	VERIFICATION_OF_AT_LEAST_TWO_INFECTED(null),
	COMPLIANT_PATHOGEN_FINE_TYPING(VERIFICATION_OF_AT_LEAST_TWO_INFECTED),

	VERIFICATION_ON_MATERIALS(null),
	IMPRESSION_TEST(VERIFICATION_ON_MATERIALS),
	WATER_SAMPLE(VERIFICATION_ON_MATERIALS),
	OTHER(VERIFICATION_ON_MATERIALS),
	PATHOGEN_FINE_TYPING_COMPLIANT_WITH_CASE(VERIFICATION_ON_MATERIALS);

	private LaboratoryDiagnosticEvidenceDetail parent;

	LaboratoryDiagnosticEvidenceDetail(LaboratoryDiagnosticEvidenceDetail parent) {
		this.parent = parent;
	}

	public LaboratoryDiagnosticEvidenceDetail getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
