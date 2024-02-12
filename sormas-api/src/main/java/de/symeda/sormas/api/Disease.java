/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;
@AuditedClass
public enum Disease
	implements
	StatisticsGroupingKey {

	AFP(true, true, true, false, false, 0, true, false, false),
	CHOLERA(true, true, true, false, true, 5, true, false, false),
	CONGENITAL_RUBELLA(true, true, true, false, true, 21, true, false, false),
	CSM(true, true, true, false, false, 10, true, false, false),
	DENGUE(true, true, true, false, false, 14, true, false, false),
	EVD(true, true, true, false, true, 21, true, false, false),
	GUINEA_WORM(true, true, true, false, false, 0, true, false, false),
	LASSA(true, true, true, false, true, 21, true, false, false),
	MEASLES(true, true, true, false, false, 21, true, true, false),
	MONKEYPOX(true, true, true, false, true, 21, true, false, false),
	NEW_INFLUENZA(true, true, true, false, true, 17, true, false, false),
	PLAGUE(true, true, true, false, true, 7, true, false, false),
	POLIO(true, true, true, false, false, 0, true, false, false),
	UNSPECIFIED_VHF(true, true, true, false, true, 21, true, false, false),
	WEST_NILE_FEVER(true, false, true, false, false, 0, true, false, false),
	YELLOW_FEVER(true, true, true, false, false, 6, true, false, false),
	RABIES(true, true, true, false, true, 6, true, false, false),
	ANTHRAX(true, true, true, false, false, 0, true, false, false),
	CORONAVIRUS(true, true, true, false, true, 14, true, true, true),
	PNEUMONIA(true, false, true, false, false, 0, true, false, false),
	MALARIA(true, false, false, true, false, 0, true, false, false),
	TYPHOID_FEVER(true, false, false, true, false, 0, true, false, false),
	ACUTE_VIRAL_HEPATITIS(true, false, false, true, false, 0, true, false, false),
	NON_NEONATAL_TETANUS(true, false, false, true, false, 0, true, false, false),
	HIV(true, false, false, true, false, 0, true, false, false),
	SCHISTOSOMIASIS(true, false, false, true, false, 0, true, false, false),
	SOIL_TRANSMITTED_HELMINTHS(true, false, false, true, false, 0, true, false, false),
	TRYPANOSOMIASIS(true, false, false, true, false, 0, true, false, false),
	DIARRHEA_DEHYDRATION(true, false, false, true, false, 0, true, false, false),
	DIARRHEA_BLOOD(true, false, false, true, false, 0, true, false, false),
	SNAKE_BITE(true, false, false, true, false, 0, true, false, false),
	RUBELLA(true, false, false, true, false, 0, true, false, false),
	TUBERCULOSIS(true, false, false, true, false, 0, true, false, false),
	LEPROSY(true, false, false, true, false, 0, true, false, false),
	LYMPHATIC_FILARIASIS(true, false, false, true, false, 0, true, false, false),
	BURULI_ULCER(true, false, false, true, false, 0, true, false, false),
	PERTUSSIS(true, false, false, true, false, 0, true, false, false),
	NEONATAL_TETANUS(true, false, false, true, false, 0, true, false, false),
	ONCHOCERCIASIS(true, false, false, true, false, 0, true, false, false),
	DIPHTERIA(true, false, false, true, false, 0, true, false, false),
	TRACHOMA(true, false, false, true, false, 0, true, false, false),
	YAWS_ENDEMIC_SYPHILIS(true, false, false, true, false, 0, true, false, false),
	MATERNAL_DEATHS(true, false, false, true, false, 0, true, false, false),
	PERINATAL_DEATHS(true, false, false, true, false, 0, true, false, false),
	INFLUENZA_A(true, false, true, false, false, 0, true, false, false),
	INFLUENZA_B(true, false, true, false, false, 0, true, false, false),
	H_METAPNEUMOVIRUS(true, false, true, false, false, 0, true, false, false),
	RESPIRATORY_SYNCYTIAL_VIRUS(true, false, true, false, false, 0, true, false, false),
	PARAINFLUENZA_1_4(true, false, true, false, false, 0, true, false, false),
	ADENOVIRUS(true, false, true, false, false, 0, true, false, false),
	RHINOVIRUS(true, false, true, false, false, 0, true, false, false),
	ENTEROVIRUS(true, false, true, false, false, 0, true, false, false),
	M_PNEUMONIAE(true, false, true, false, false, 0, true, false, false),
	C_PNEUMONIAE(true, false, true, false, false, 0, true, false, false),
	ARI(true, false, false, true, false, 0, true, false, false),
	CHIKUNGUNYA(true, false, false, true, false, 0, true, false, false),
	POST_IMMUNIZATION_ADVERSE_EVENTS_MILD(true, false, false, true, false, 0, true, false, false),
	POST_IMMUNIZATION_ADVERSE_EVENTS_SEVERE(true, false, false, true, false, 0, true, false, false),
	FHA(true, false, false, true, false, 0, true, false, false),
	OTHER(true, true, true, false, true, 21, false, false, false),
	UNDEFINED(true, true, true, false, true, 0, false, false, false);

	private final boolean defaultActive;
	private final boolean defaultPrimary;
	private final boolean defaultCaseSurveillanceEnabled;
	private final boolean defaultAggregateReportingEnabled;
	private final boolean defaultFollowUpEnabled;
	private final int defaultFollowUpDuration;
	private final boolean variantAllowed;
	private final boolean defaultExtendedClassification;
	private final boolean defaultExtendedClassificationMulti;

	Disease(
		boolean defaultActive,
		boolean defaultPrimary,
		boolean defaultCaseSurveillanceEnabled,
		boolean defaultAggregateReportingEnabled,
		boolean defaultFollowUpEnabled,
		int defaultFollowUpDuration,
		boolean variantAllowed,
		boolean defaultExtendedClassification,
		boolean defaultExtendedClassificationMulti) {

		this.defaultActive = defaultActive;
		this.defaultPrimary = defaultPrimary;
		this.defaultCaseSurveillanceEnabled = defaultCaseSurveillanceEnabled;
		this.defaultAggregateReportingEnabled = defaultAggregateReportingEnabled;
		this.defaultFollowUpEnabled = defaultFollowUpEnabled;
		this.defaultFollowUpDuration = defaultFollowUpDuration;
		this.variantAllowed = variantAllowed;
		this.defaultExtendedClassification = defaultExtendedClassification;
		this.defaultExtendedClassificationMulti = defaultExtendedClassificationMulti;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public String toShortString() {
		return I18nProperties.getEnumCaptionShort(this);
	}

	public String getName() {
		return this.name();
	}

	public boolean usesSimpleViewForOutbreaks() {

		switch (this) {
		case CSM:
			return true;
		//$CASES-OMITTED$
		default:
			return false;
		}
	}

	public boolean isDefaultActive() {
		return defaultActive;
	}

	public boolean isDefaultPrimary() {
		return defaultPrimary;
	}

	public boolean isDefaultCaseSurveillanceEnabled() {
		return defaultCaseSurveillanceEnabled;
	}

	public boolean isDefaultAggregateReportingEnabled() {
		return defaultAggregateReportingEnabled;
	}

	public boolean isDefaultFollowUpEnabled() {
		return defaultFollowUpEnabled;
	}

	public int getDefaultFollowUpDuration() {
		return defaultFollowUpDuration;
	}

	public boolean isDiseaseGroup() {
		return this == UNSPECIFIED_VHF;
	}

	public boolean isVariantAllowed() {
		return variantAllowed;
	}

	public boolean isDefaultExtendedClassification() {
		return defaultExtendedClassification;
	}

	public boolean isDefaultExtendedClassificationMulti() {
		return defaultExtendedClassificationMulti;
	}

	@Override
	public int keyCompareTo(StatisticsGroupingKey o) {

		if (o == null) {
			throw new NullPointerException("Can't compare to null.");
		}
		if (o.getClass() != this.getClass()) {
			throw new UnsupportedOperationException(
				"Can't compare to class " + o.getClass().getName() + " that differs from " + this.getClass().getName());
		}

		return this.toString().compareTo(o.toString());
	}

	public static List<Disease> DISEASE_LIST = Arrays.asList(Disease.values());
}
