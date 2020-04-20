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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public enum Disease implements StatisticsGroupingKey {
	
	AFP(true, true, true, false, 0),
	CHOLERA(true, true, true, true, 5),
	CONGENITAL_RUBELLA(true, true, true, true, 21),
	CSM(true, true, true, false, 10),
	DENGUE(true, true, true, false, 14),
	EVD(true, true, true, true, 21),
	GUINEA_WORM(true, true, true, false, 0),
	LASSA(true, true, true, true, 21),
	MEASLES(true, true, true, false, 21),
	MONKEYPOX(true, true, true, true, 21),
	NEW_INFLUENZA(true, true, true, true, 17),
	PLAGUE(true, true, true, true, 7),
	POLIO(true, true, true, false, 0),
	UNSPECIFIED_VHF(true, true, true, true, 21),
	WEST_NILE_FEVER(true, false, true, false, 0),
	YELLOW_FEVER(true, true, true, false, 6),
	RABIES(true, true, true, true, 6),
	ANTHRAX(true, true, true, false, 0),
	CORONAVIRUS(true, true, true, true, 14),
	PNEUMONIA(true, false, true, false, 0),
	MALARIA(true, false, false, false, 0),
	TYPHOID_FEVER(true, false, false, false, 0),
	ACUTE_VIRAL_HEPATITIS(true, false, false, false, 0),
	NON_NEONATAL_TETANUS(true, false, false, false, 0),
	HIV(true, false, false, false, 0),
	SCHISTOSOMIASIS(true, false, false, false, 0),
	SOIL_TRANSMITTED_HELMINTHS(true, false, false, false, 0),
	TRYPANOSOMIASIS(true, false, false, false, 0),
	DIARRHEA_DEHYDRATION(true, false, false, false, 0),
	DIARRHEA_BLOOD(true, false, false, false, 0),
	SNAKE_BITE(true, false, false, false, 0),
	RUBELLA(true, false, false, false, 0),
	TUBERCULOSIS(true, false, false, false, 0),
	LEPROSY(true, false, false, false, 0),
	LYMPHATIC_FILARIASIS(true, false, false, false, 0),
	BURULI_ULCER(true, false, false, false, 0),
	PERTUSSIS(true, false, false, false, 0),
	NEONATAL_TETANUS(true, false, false, false, 0),
	ONCHOCERCIASIS(true, false, false, false, 0),
	DIPHTERIA(true, false, false, false, 0),
	TRACHOMA(true, false, false, false, 0),
	YAWS_ENDEMIC_SYPHILIS(true, false, false, false, 0),
	MATERNAL_DEATHS(true, false, false, false, 0),
	PERINATAL_DEATHS(true, false, false, false, 0),
	INFLUENZA_A(true, false, true, false, 0),
	INFLUENZA_B(true, false, true, false, 0),
	H_METAPNEUMOVIRUS(true, false, true, false, 0),
	RESPIRATORY_SYNCYTIAL_VIRUS(true, false, true, false, 0),
	PARAINFLUENZA_1_4(true, false, true, false, 0),
	ADENOVIRUS(true, false, true, false, 0),
	RHINOVIRUS(true, false, true, false, 0),
	ENTEROVIRUS(true, false, true, false, 0),
	M_PNEUMONIAE(true, false, true, false, 0),
	C_PNEUMONIAE(true, false, true, false, 0),
	OTHER(true, true, true, true, 21),
	UNDEFINED(true, true, true, true, 0);
	
	private boolean defaultActive;
	private boolean defaultPrimary;
	private boolean defaultCaseBased;
	private boolean defaultFollowUpEnabled;
	private int defaultFollowUpDuration;

	Disease(boolean defaultActive, boolean defaultPrimary, boolean defaultCaseBased, boolean defaultFollowUpEnabled, int defaultFollowUpDuration) {

		this.defaultActive = defaultActive;
		this.defaultPrimary = defaultPrimary;
		this.defaultCaseBased = defaultCaseBased;
		this.defaultFollowUpEnabled = defaultFollowUpEnabled;
		this.defaultFollowUpDuration = defaultFollowUpDuration;
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
		switch(this) {
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
	
	@Override
	public int keyCompareTo(StatisticsGroupingKey o) {
		if (o == null) {
			throw new NullPointerException("Can't compare to null.");
		}
		if (o.getClass() != this.getClass()) {
			throw new UnsupportedOperationException("Can't compare to class " + o.getClass().getName() + " that differs from " + this.getClass().getName());
		}
		
		return this.toString().compareTo(o.toString());
	}
}
