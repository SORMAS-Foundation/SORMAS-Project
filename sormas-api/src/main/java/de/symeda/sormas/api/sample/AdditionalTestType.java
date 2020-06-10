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
package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum AdditionalTestType {

	HAEMOGLOBINURIA(SampleMaterial.URINE),
	PROTEINURIA(SampleMaterial.URINE),
	HEMATURIA(SampleMaterial.URINE),
	ARTERIAL_VENOUS_BLOOD_GAS(SampleMaterial.BLOOD),
	ALT_SGPT(SampleMaterial.BLOOD),
	AST_SGOT(SampleMaterial.BLOOD),
	CREATININE(SampleMaterial.BLOOD),
	POTASSIUM(SampleMaterial.BLOOD),
	UREA(SampleMaterial.BLOOD),
	HAEMOGLOBIN(SampleMaterial.BLOOD),
	TOTAL_BILIRUBIN(SampleMaterial.BLOOD),
	CONJ_BILIRUBIN(SampleMaterial.BLOOD),
	WBC_COUNT(SampleMaterial.BLOOD),
	PLATELETS(SampleMaterial.BLOOD),
	PROTHROMBIN_TIME(SampleMaterial.BLOOD);

	private SampleMaterial sampleMaterial;

	AdditionalTestType(SampleMaterial sampleMaterial) {
		this.sampleMaterial = sampleMaterial;
	}

	public SampleMaterial getSampleMaterial() {
		return sampleMaterial;
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public AdditionalTestType[] values(SampleMaterial sampleMaterial) {

		AdditionalTestType[] values = new AdditionalTestType[] {};
		int valuesIndex = 0;
		for (AdditionalTestType type : values()) {
			if (type.getSampleMaterial() == sampleMaterial) {
				values[valuesIndex++] = type;
			}
		}

		return values;
	}
}
