/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.caze.classification;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class ClassificationExposureCriteriaDto extends ClassificationCaseCriteriaDto {

	private ExposureType exposureType;

	public ClassificationExposureCriteriaDto() {
		super();
	}

	public ClassificationExposureCriteriaDto(String propertyId, ExposureType exposureType, Object... propertyValues) {

		super(propertyId, propertyValues);
		this.exposureType = exposureType;
	}

	@Override
	protected Class<? extends EntityDto> getInvokeClass() {
		return ExposureDto.class;
	}

	@Override
	public boolean eval(CaseDataDto caze, PersonDto person, List<PathogenTestDto> pathogenTests, List<EventDto> events, Date lastVaccinationDate) {

		for (ExposureDto exposure : caze.getEpiData().getExposures()) {
			if (exposureType != null && exposure.getExposureType() != exposureType) {
				continue;
			}

			Method method;
			try {
				method = getInvokeClass().getMethod("get" + propertyId.substring(0, 1).toUpperCase() + propertyId.substring(1));
			} catch (NoSuchMethodException e) {
				try {
					method = getInvokeClass().getMethod("is" + propertyId.substring(0, 1).toUpperCase() + propertyId.substring(1));
				} catch (NoSuchMethodException newE) {
					throw new RuntimeException(newE);
				}
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			}

			try {
				Object value = method.invoke(exposure);
				if (propertyValues.contains(value) || CollectionUtils.isEmpty(propertyValues) && YesNoUnknown.YES.equals(value)) {
					return true;
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
				throw new RuntimeException(e);
			}
		}

		return false;
	}

	@Override
	public String buildDescription() {

		StringBuilder sb = new StringBuilder();
		sb.append(I18nProperties.getPrefixCaption(ExposureDto.I18N_PREFIX, propertyId));
		if (exposureType != null) {
			sb.append(" ").append(I18nProperties.getString(Strings.classificationCriteriaForExposureType)).append(exposureType.toString());
		}

		return sb.toString();
	}

}
