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
package de.symeda.sormas.api.caze.classification;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;

public class ClassificationSymptomsCriteriaDto extends ClassificationCaseCriteriaDto {

	private static final long serialVersionUID = 6880120976447372375L;

	public ClassificationSymptomsCriteriaDto() {
		super();
	}

	public ClassificationSymptomsCriteriaDto(String propertyId) {
		super(propertyId, SymptomState.YES);
	}

	public ClassificationSymptomsCriteriaDto(String propertyId, Object... propertyValues) {
		super(propertyId, propertyValues);
	}

	@Override
	protected Class<? extends EntityDto> getInvokeClass() {
		return SymptomsDto.class;
	}

	@Override
	protected Object getInvokeObject(CaseDataDto caze) {
		return caze.getSymptoms();
	}

	@Override
	public String buildDescription() {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, propertyId));
		if (!(propertyValues.get(0) instanceof SymptomState)) {
			appendDescValues(stringBuilder);
		}
		return stringBuilder.toString();
	}
}
