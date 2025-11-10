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

package de.symeda.sormas.api.epipulse;

import java.util.Arrays;
import java.util.Comparator;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;

public enum EpipulseSubjectCode {

	PERT(true, Disease.PERTUSSIS, false);

	private final boolean diseaseModel;
	private final Disease disease;
	private final boolean aggregatedReporting;

	EpipulseSubjectCode(boolean diseaseModel, Disease disease, boolean aggregatedReporting) {
		this.diseaseModel = diseaseModel;
		this.disease = disease;
		this.aggregatedReporting = aggregatedReporting;
	}

	public boolean isDiseaseModel() {
		return diseaseModel;
	}

	public Disease getDisease() {
		return disease;
	}

	public boolean isAggregatedReporting() {
		return aggregatedReporting;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public static Disease[] getSormasDiseases() {
		return Arrays.stream(values()).map(EpipulseSubjectCode::getDisease).sorted(Comparator.comparing(Disease::toString)).toArray(Disease[]::new);
	}
}
