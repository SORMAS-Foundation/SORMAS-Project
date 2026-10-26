/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.api.epipulse.referencevalue;

import de.symeda.sormas.api.epidata.CaseImportedStatus;

/**
 * The {@code ImportedStatus} variable: where a case was acquired, as EpiPulse reports it.
 *
 * <p>
 * Declared by DIPH, MEAS, MENI, and RUBE for the same four values; currently emitted by MEAS
 * and MENI. This is epidemiological data, not laboratory data, so it belongs here rather than in
 * {@code EpipulseLaboratoryMapper}.
 *
 * <p>
 * The enum constant name is the EpiPulse code, as elsewhere in this package.
 *
 * <p>
 * {@link CaseImportedStatus} has exactly these four constants, so every recorded value maps and
 * there is no {@code OTH} fallback. A missing record reports nothing.
 */
public enum EpipulseImportedStatusRef {

	IMP(CaseImportedStatus.IMPORTED_CASE),
	IMPREL(CaseImportedStatus.IMPORT_RELATED_CASE),
	IMPUNK(CaseImportedStatus.UNKNOWN_IMPORTATION_STATUS),
	NOTIMP(CaseImportedStatus.NOT_IMPORTED_CASE);

	private final CaseImportedStatus importedStatus;

	EpipulseImportedStatusRef(CaseImportedStatus importedStatus) {
		this.importedStatus = importedStatus;
	}

	public CaseImportedStatus getImportedStatus() {
		return importedStatus;
	}

	/** The code for a recorded importation status. {@code null} in, {@code null} out. */
	public static String codeFor(CaseImportedStatus importedStatus) {

		if (importedStatus == null) {
			return null;
		}

		for (EpipulseImportedStatusRef ref : values()) {
			if (ref.importedStatus == importedStatus) {
				return ref.name();
			}
		}

		return null;
	}
}
