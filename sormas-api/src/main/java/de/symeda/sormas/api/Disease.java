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
	
	CHOLERA(true, true, true, 5),
	CONGENITAL_RUBELLA(true, true, true, 21),
	CSM(true, true, false, 10),
	DENGUE(true, true, false, 14),
	EVD(true, true, true, 21),
	LASSA(true, true, true, 21),
	MEASLES(true, true, false, 21),
	MONKEYPOX(true, true, true, 21),
	NEW_INFLUENCA(true, true, true, 17),
	PLAGUE(true, true, true, 7),
	UNSPECIFIED_VHF(true, true, true, 21),
	WEST_NILE_FEVER(true, false, false, 0),
	YELLOW_FEVER(true, true, false, 6),
	OTHER(true, true, true, 21),
	UNDEFINED(true, true, true, 0);
	
	private boolean defaultActive;
	private boolean defaultPrimary;
	private boolean defaultFollowUpEnabled;
	private int defaultFollowUpDuration;
	
	private Disease(boolean defaultActive, boolean defaultPrimary, boolean defaultFollowUpEnabled, int defaultFollowUpDuration) {
		this.defaultActive = defaultActive;
		this.defaultPrimary = defaultPrimary;
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
