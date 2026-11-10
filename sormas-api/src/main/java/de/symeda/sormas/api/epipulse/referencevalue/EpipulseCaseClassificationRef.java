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

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * EpiPulse {@code CaseClassification} codes and the SORMAS classifications behind them.
 *
 * <p>
 * Supports both CSV rendering and inclusion logic. {@code CONF} intentionally covers plain
 * confirmed plus both symptom-qualified confirmed classifications.
 *
 * <p>
 * {@code DISCARDED} is reported only by the subject codes that declare it in
 * {@link de.symeda.sormas.api.epipulse.EpipulseCaseInclusion}; every other code reports a
 * ruled-out case blank, as its value set has no such code.
 */
public enum EpipulseCaseClassificationRef {

	CONF(CaseClassification.CONFIRMED, CaseClassification.CONFIRMED_NO_SYMPTOMS, CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS),
	DISCARDED(CaseClassification.NO_CASE),
	POSS(CaseClassification.SUSPECT),
	PROB(CaseClassification.PROBABLE);

	private final List<CaseClassification> caseClassifications;

	EpipulseCaseClassificationRef(CaseClassification... caseClassifications) {
		this.caseClassifications = Collections.unmodifiableList(Arrays.asList(caseClassifications));
	}

	/** @return SORMAS classifications represented by this code */
	public List<CaseClassification> getCaseClassifications() {
		return caseClassifications;
	}

	public static EpipulseCaseClassificationRef getByCaseClassification(CaseClassification classification) {
		if (classification == null) {
			return null;
		}

		for (EpipulseCaseClassificationRef classificationRef : values()) {
			if (classificationRef.caseClassifications.contains(classification)) {
				return classificationRef;
			}
		}
		return null;
	}

	/**
	 * Collects all SORMAS classifications represented by the given code set.
	 */
	public static Set<CaseClassification> caseClassificationsOf(Set<EpipulseCaseClassificationRef> codes) {

		Set<CaseClassification> classifications = EnumSet.noneOf(CaseClassification.class);
		for (EpipulseCaseClassificationRef code : codes) {
			classifications.addAll(code.caseClassifications);
		}

		return Collections.unmodifiableSet(classifications);
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
