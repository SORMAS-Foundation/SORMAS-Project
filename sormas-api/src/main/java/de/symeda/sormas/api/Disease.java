/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.api;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public enum Disease
	implements
	StatisticsGroupingKey {

	AFP(true, true, true, false, 0, true),
	CHOLERA(true, true, true, true, 5, true),
	CONGENITAL_RUBELLA(true, true, true, true, 21, true),
	CSM(true, true, true, false, 10, true),
	DENGUE(true, true, true, false, 14, true),
	EVD(true, true, true, true, 21, true),
	GUINEA_WORM(true, true, true, false, 0, true),
	LASSA(true, true, true, true, 21, true),
	MEASLES(true, true, true, false, 21, true),
	MONKEYPOX(true, true, true, true, 21, true),
	NEW_INFLUENZA(true, true, true, true, 17, true),
	PLAGUE(true, true, true, true, 7, true),
	POLIO(true, true, true, false, 0, true),
	UNSPECIFIED_VHF(true, true, true, true, 21, true),
	WEST_NILE_FEVER(true, false, true, false, 0, true),
	YELLOW_FEVER(true, true, true, false, 6, true),
	RABIES(true, true, true, true, 6, true),
	ANTHRAX(true, true, true, false, 0, true),
	CORONAVIRUS(true, true, true, true, 14, true),
	PNEUMONIA(true, false, true, false, 0, true),
	MALARIA(true, false, false, false, 0, true),
	TYPHOID_FEVER(true, false, false, false, 0, true),
	ACUTE_VIRAL_HEPATITIS(true, false, false, false, 0, true),
	NON_NEONATAL_TETANUS(true, false, false, false, 0, true),
	HIV(true, false, false, false, 0, true),
	SCHISTOSOMIASIS(true, false, false, false, 0, true),
	SOIL_TRANSMITTED_HELMINTHS(true, false, false, false, 0, true),
	TRYPANOSOMIASIS(true, false, false, false, 0, true),
	DIARRHEA_DEHYDRATION(true, false, false, false, 0, true),
	DIARRHEA_BLOOD(true, false, false, false, 0, true),
	SNAKE_BITE(true, false, false, false, 0, true),
	RUBELLA(true, false, false, false, 0, true),
	TUBERCULOSIS(true, false, false, false, 0, true),
	LEPROSY(true, false, false, false, 0, true),
	LYMPHATIC_FILARIASIS(true, false, false, false, 0, true),
	BURULI_ULCER(true, false, false, false, 0, true),
	PERTUSSIS(true, false, false, false, 0, true),
	NEONATAL_TETANUS(true, false, false, false, 0, true),
	ONCHOCERCIASIS(true, false, false, false, 0, true),
	DIPHTERIA(true, false, false, false, 0, true),
	TRACHOMA(true, false, false, false, 0, true),
	YAWS_ENDEMIC_SYPHILIS(true, false, false, false, 0, true),
	MATERNAL_DEATHS(true, false, false, false, 0, true),
	PERINATAL_DEATHS(true, false, false, false, 0, true),
	INFLUENZA_A(true, false, true, false, 0, true),
	INFLUENZA_B(true, false, true, false, 0, true),
	H_METAPNEUMOVIRUS(true, false, true, false, 0, true),
	RESPIRATORY_SYNCYTIAL_VIRUS(true, false, true, false, 0, true),
	PARAINFLUENZA_1_4(true, false, true, false, 0, true),
	ADENOVIRUS(true, false, true, false, 0, true),
	RHINOVIRUS(true, false, true, false, 0, true),
	ENTEROVIRUS(true, false, true, false, 0, true),
	M_PNEUMONIAE(true, false, true, false, 0, true),
	C_PNEUMONIAE(true, false, true, false, 0, true),
	OTHER(true, true, true, true, 21, false),
	UNDEFINED(true, true, true, true, 0, false);

	private boolean defaultActive;
	private boolean defaultPrimary;
	private boolean defaultCaseBased;
	private boolean defaultFollowUpEnabled;
	private int defaultFollowUpDuration;
	private boolean variantAllowed;

	Disease(boolean defaultActive, boolean defaultPrimary, boolean defaultCaseBased, boolean defaultFollowUpEnabled, int defaultFollowUpDuration, boolean variantAllowed) {

		this.defaultActive = defaultActive;
		this.defaultPrimary = defaultPrimary;
		this.defaultCaseBased = defaultCaseBased;
		this.defaultFollowUpEnabled = defaultFollowUpEnabled;
		this.defaultFollowUpDuration = defaultFollowUpDuration;
		this.variantAllowed = variantAllowed;
	}

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

	public boolean isDefaultCaseBased() {
		return defaultCaseBased;
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
}
