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

package de.symeda.sormas.api.adverseeventsfollowingimmunization;

import static de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiClassification.RELATED_TO_VACCINE_OR_VACCINATION;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum AefiClassificationSubType {

	VACCINE_PRODUCT_RELATED(RELATED_TO_VACCINE_OR_VACCINATION),
	VACCINE_QUALITY_DEFECT_RELATED(RELATED_TO_VACCINE_OR_VACCINATION),
	IMMUNIZATION_ERROR_RELATED(RELATED_TO_VACCINE_OR_VACCINATION),
	IMMUNIZATION_ANXIETY_RELATED(RELATED_TO_VACCINE_OR_VACCINATION);

	private AefiClassification aefiClassification;

	AefiClassificationSubType(AefiClassification aefiClassification) {
		this.aefiClassification = aefiClassification;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
