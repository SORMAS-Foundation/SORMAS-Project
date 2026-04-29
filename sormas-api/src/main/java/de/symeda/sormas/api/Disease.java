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

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public enum Disease
	implements
	StatisticsGroupingKey {

	AFP(true, true, true, false, true, 60, true, false, false, true, 7, 30),
	CHOLERA(true, true, true, false, true, 5, true, false, false, true, 0, 5),
	CONGENITAL_RUBELLA(true, true, true, false, true, 21, true, false, false, false, 0, 0),
	CSM(true, true, true, false, true, 10, true, false, false, false, 0, 0),
	DENGUE(true, true, true, false, false, 14, true, false, false, true, 2, 14),
	EVD(true, true, true, false, true, 21, true, false, false, true, 2, 21),
	GUINEA_WORM(true, true, true, false, false, 0, true, false, false, false, 0, 0),
	LASSA(true, true, true, false, true, 21, true, false, false, true, 3, 21),
	MEASLES(true, true, true, false, true, 21, true, true, false, true, 7, 21),
	MONKEYPOX(true, true, true, false, true, 21, true, false, false, true, 5, 21),
	NEW_INFLUENZA(true, true, true, false, true, 17, true, false, false, false, 0, 0),
	PLAGUE(true, true, true, false, true, 7, true, false, false, true, 1, 7),
	POLIO(true, true, true, false, true, 60, true, false, false, true, 7, 30),
	UNSPECIFIED_VHF(true, true, true, false, true, 21, true, false, false, true, 2, 21),
	WEST_NILE_FEVER(true, false, true, false, false, 0, true, false, false, true, 2, 14),
	YELLOW_FEVER(true, true, true, false, false, 6, true, false, false, true, 3, 6),
	RABIES(true, true, true, false, true, 6, true, false, false, true, 5, 730),
	ANTHRAX(true, true, true, false, false, 0, true, false, false, true, 1, 60),
	CORONAVIRUS(true, true, true, false, true, 14, true, true, true, true, 1, 14),
	PNEUMONIA(true, false, true, true, false, 0, true, false, false, false, 0, 0),
	MALARIA(true, true, false, true, false, 0, true, false, false, true, 7, 60),
	TYPHOID_FEVER(true, false, false, true, false, 0, true, false, false, false, 0, 0),
	ACUTE_VIRAL_HEPATITIS(true, false, false, true, false, 0, true, false, false, false, 0, 0),
	NON_NEONATAL_TETANUS(true, false, false, true, false, 0, true, false, false, false, 0, 0),
	HIV(true, false, false, true, false, 0, true, false, false, true, 10, 180),
	SCHISTOSOMIASIS(true, false, false, true, false, 0, true, false, false, false, 0, 0),
	SOIL_TRANSMITTED_HELMINTHS(true, false, false, true, false, 0, true, false, false, false, 0, 0),
	TRYPANOSOMIASIS(true, false, false, true, false, 0, true, false, false, false, 0, 0),
	DIARRHEA_DEHYDRATION(true, false, false, true, false, 0, true, false, false, false, 0, 0),
	DIARRHEA_BLOOD(true, false, false, true, false, 0, true, false, false, false, 0, 0),
	SNAKE_BITE(true, false, false, true, false, 0, true, false, false, false, 0, 0),
	RUBELLA(true, false, false, true, false, 0, true, false, false, true, 14, 23),
	TUBERCULOSIS(false, true, true, true, true, 365, true, false, false, true, 30, 730),
	LATENT_TUBERCULOSIS(false, true, true, true, true, 30, true, false, false, false, 0, 0),
	LEPROSY(true, false, false, true, false, 0, true, false, false, true, 18, 730),
	LYMPHATIC_FILARIASIS(true, false, false, true, false, 0, true, false, false, false, 0, 0),
	BURULI_ULCER(true, false, false, true, false, 0, true, false, false, false, 0, 0),
	PERTUSSIS(true, false, false, true, true, 0, true, false, false, true, 4, 21),
	NEONATAL_TETANUS(true, false, false, true, false, 0, true, false, false, true, 3, 21),
	ONCHOCERCIASIS(true, false, false, true, false, 0, true, false, false, false, 0, 0),
	DIPHTERIA(true, false, false, true, false, 0, true, false, false, true, 1, 10),
	TRACHOMA(true, false, false, true, false, 0, true, false, false, false, 0, 0),
	YAWS_ENDEMIC_SYPHILIS(true, false, false, true, false, 0, true, false, false, false, 0, 0),
	MATERNAL_DEATHS(true, false, false, true, false, 0, true, false, false, false, 0, 0),
	PERINATAL_DEATHS(true, false, false, true, false, 0, true, false, false, true, 1, 4),
	INFLUENZA(true, false, false, false, false, 0, true, false, false, true, 1, 4),
	INFLUENZA_A(false, true, true, false, false, 0, true, false, false, true, 1, 4),
	INFLUENZA_B(false, true, true, false, false, 0, true, false, false, true, 1, 4),
	H_METAPNEUMOVIRUS(true, false, true, true, false, 0, true, false, false, false, 0, 0),
	RESPIRATORY_SYNCYTIAL_VIRUS(true, false, true, false, false, 0, true, false, false, true, 2, 8),
	PARAINFLUENZA_1_4(false, false, true, true, false, 0, true, false, false, false, 0, 0),
	ADENOVIRUS(true, false, true, true, false, 0, true, false, false, false, 0, 0),
	RHINOVIRUS(true, false, true, true, false, 0, true, false, false, false, 0, 0),
	ENTEROVIRUS(true, false, true, true, false, 0, true, false, false, false, 0, 0),
	M_PNEUMONIAE(true, false, true, true, false, 0, true, false, false, false, 0, 0),
	C_PNEUMONIAE(true, false, true, true, false, 0, true, false, false, false, 0, 0),
	ARI(true, false, false, true, false, 0, true, false, false, false, 0, 0),
	CHIKUNGUNYA(true, false, false, true, false, 0, true, false, false, true, 2, 14),
	POST_IMMUNIZATION_ADVERSE_EVENTS_MILD(true, false, false, true, false, 0, true, false, false, false, 0, 0),
	POST_IMMUNIZATION_ADVERSE_EVENTS_SEVERE(true, false, false, true, false, 0, true, false, false, false, 0, 0),
	FHA(true, false, false, true, false, 0, true, false, false, false, 0, 0),
	INVASIVE_PNEUMOCOCCAL_INFECTION(true, true, true, false, false, 0, false, false, false, true, 1, 3),
	INVASIVE_MENINGOCOCCAL_INFECTION(true, true, true, false, true, 7, false, false, false, true, 1, 10),
	GIARDIASIS(true, true, true, false, true, 14, false, false, false, true, 7, 21),
	CRYPTOSPORIDIOSIS(true, true, true, false, true, 14, false, false, false, true, 2, 12),
	SALMONELLOSIS(true, true, true, false, true, 14, false, false, false, true, 1, 3),
	OTHER(true, true, true, false, true, 21, false, false, false, false, 0, 0),
	UNDEFINED(true, true, true, false, true, 0, false, false, false, false, 0, 0);

	private final boolean defaultActive;
	private final boolean defaultPrimary;
	private final boolean defaultCaseSurveillanceEnabled;
	private final boolean defaultAggregateReportingEnabled;
	private final boolean defaultFollowUpEnabled;
	private final int defaultFollowUpDuration;
	private final boolean variantAllowed;
	private final boolean defaultExtendedClassification;
	private final boolean defaultExtendedClassificationMulti;
	private final boolean defaultIncubationPeriodEnabled;
	private final int defaultMinIncubationPeriod;
	private final int defaultMaxIncubationPeriod;

	Disease(
		boolean defaultActive,
		boolean defaultPrimary,
		boolean defaultCaseSurveillanceEnabled,
		boolean defaultAggregateReportingEnabled,
		boolean defaultFollowUpEnabled,
		int defaultFollowUpDuration,
		boolean variantAllowed,
		boolean defaultExtendedClassification,
		boolean defaultExtendedClassificationMulti,
		boolean defaultIncubationPeriodEnabled,
		int defaultMinIncubationPeriod,
		int defaultMaxIncubationPeriod) {

		this.defaultActive = defaultActive;
		this.defaultPrimary = defaultPrimary;
		this.defaultCaseSurveillanceEnabled = defaultCaseSurveillanceEnabled;
		this.defaultAggregateReportingEnabled = defaultAggregateReportingEnabled;
		this.defaultFollowUpEnabled = defaultFollowUpEnabled;
		this.defaultFollowUpDuration = defaultFollowUpDuration;
		this.variantAllowed = variantAllowed;
		this.defaultExtendedClassification = defaultExtendedClassification;
		this.defaultExtendedClassificationMulti = defaultExtendedClassificationMulti;
		this.defaultIncubationPeriodEnabled = defaultIncubationPeriodEnabled;
		this.defaultMinIncubationPeriod = defaultMinIncubationPeriod;
		this.defaultMaxIncubationPeriod = defaultMaxIncubationPeriod;
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

	public boolean isDefaultIncubationPeriodEnabled() {
		return defaultIncubationPeriodEnabled;
	}

	public int getDefaultMinIncubationPeriod() {
		return defaultMinIncubationPeriod;
	}

	public int getDefaultMaxIncubationPeriod() {
		return defaultMaxIncubationPeriod;
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

	public static final List<Disease> DISEASE_LIST = Arrays.asList(Disease.values());
}
