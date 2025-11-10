/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.epipulse.referencevalue;

import static de.symeda.sormas.api.epipulse.EpipulseSubjectCode.PERT;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.PathogenTestType;

public enum EpipulsePathogenTestTypeRef {

	CULT(PathogenTestType.CULTURE, PERT),
	ORALIgG(null, PERT),
	PCR(PathogenTestType.PCR_RT_PCR, PERT),
	SERO(null, PERT);

	private final PathogenTestType testType;
	private final EpipulseSubjectCode[] subjectCodes;

	EpipulsePathogenTestTypeRef(PathogenTestType testType, EpipulseSubjectCode... subjectCodes) {
		this.testType = testType;
		this.subjectCodes = subjectCodes;
	}

	public PathogenTestType getTestType() {
		return testType;
	}

	public EpipulseSubjectCode[] getSubjectCodes() {
		return subjectCodes;
	}

	public static EpipulsePathogenTestTypeRef getByPathogenTestType(PathogenTestType pathogenTestType) {
		if (pathogenTestType == null) {
			return null;
		}

		for (EpipulsePathogenTestTypeRef ref : EpipulsePathogenTestTypeRef.values()) {
			if (ref.getTestType() == pathogenTestType) {
				return ref;
			}
		}

		return null;
	}

	public static List<PathogenTestType> getPathogenTestTypesByDisease(EpipulseSubjectCode subjectCode) {
		if (subjectCode == null) {
			return new ArrayList<>();
		}

		List<PathogenTestType> testTypes = new ArrayList<>();
		for (EpipulsePathogenTestTypeRef ref : EpipulsePathogenTestTypeRef.values()) {
			if (ref.testType != null && ref.hasSubjectCode(subjectCode)) {
				testTypes.add(ref.testType);
			}
		}

		return testTypes;
	}

	private boolean hasSubjectCode(EpipulseSubjectCode disease) {
		for (EpipulseSubjectCode d : subjectCodes) {
			if (d == disease) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
