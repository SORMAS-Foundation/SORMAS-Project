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
package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.I18nProperties;

public enum SampleTestType {

	ANTIGEN_DETECTION,
	RAPID_TEST,
	CULTURE,
	DENGUE_FEVER_IGM,
	DENGUE_FEVER_ANTIBODIES,
	HISTOPATHOLOGY,
	ISOLATION,
	IGM_SERUM_ANTIBODY,
	IGG_SERUM_ANTIBODY,
	MICROSCOPY,
	NEUTRALIZING_ANTIBODIES,
	PCR_RT_PCR,
	WEST_NILE_FEVER_IGM,
	WEST_NILE_FEVER_ANTIBODIES,
	YELLOW_FEVER_IGM,
	YELLOW_FEVER_ANTIBODIES,
	YERSINIA_PESTIS_ANTIGEN,
	OTHER,
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
}
