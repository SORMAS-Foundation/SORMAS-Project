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

import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.i18n.I18nProperties;

public enum EpipulseCaseOutcomeRef {

	A(CaseOutcome.RECOVERED, CaseOutcome.NO_OUTCOME),
	D(CaseOutcome.DECEASED);

	private final CaseOutcome[] caseOutcomes;

	EpipulseCaseOutcomeRef(CaseOutcome... caseOutcomes) {
		this.caseOutcomes = caseOutcomes;
	}

	public CaseOutcome[] getCaseOutcomes() {
		return caseOutcomes;
	}

	public static EpipulseCaseOutcomeRef getByCaseOutcome(CaseOutcome outcome) {
		if (outcome == null) {
			return null;
		}

		for (EpipulseCaseOutcomeRef ref : EpipulseCaseOutcomeRef.values()) {
			for (CaseOutcome caseOutcome : ref.caseOutcomes) {
				if (caseOutcome == outcome) {
					return ref;
				}
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
