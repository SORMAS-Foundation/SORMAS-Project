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
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class ClassificationEpiDataCriteriaDto extends ClassificationCaseCriteriaDto {

	private static final long serialVersionUID = -1805242010549597591L;

	public ClassificationEpiDataCriteriaDto() {
		super();
	}

	public ClassificationEpiDataCriteriaDto(String propertyId) {
		super(propertyId, YesNoUnknown.YES);
	}

	public ClassificationEpiDataCriteriaDto(String propertyId, Object... propertyValues) {
		super(propertyId, propertyValues);
	}

	@Override
	protected Class<? extends EntityDto> getInvokeClass() {
		return EpiDataDto.class;
	}

	@Override
	protected Object getInvokeObject(CaseDataDto caze) {
		return caze.getEpiData();
	}

	@Override
	public String buildDescription() {
		return I18nProperties.getPrefixCaption(EpiDataDto.I18N_PREFIX, propertyId);
	}
}
