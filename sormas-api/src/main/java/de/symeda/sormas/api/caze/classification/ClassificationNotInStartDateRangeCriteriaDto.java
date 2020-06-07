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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.utils.DateHelper;

/**
 * Classification criteria that is applicable when the given property, which needs to be date, is within
 * the range specified by the case start date and a number of days before this case start date. The case
 * start date is either the symptom onset date or case report date, depending on which
 * of this date types is available. The number of days before the case start date will usually be the
 * incubation period of the respective disease.
 */
public class ClassificationNotInStartDateRangeCriteriaDto extends ClassificationCaseCriteriaDto {

	private static final long serialVersionUID = -8817472226784147694L;

	private int daysBeforeStartDate;

	public ClassificationNotInStartDateRangeCriteriaDto() {
		super();
	}

	public ClassificationNotInStartDateRangeCriteriaDto(String propertyId, int daysBeforeStartDate) {
		super(propertyId);
		this.daysBeforeStartDate = daysBeforeStartDate;
	}

	@Override
	public boolean eval(CaseDataDto caze, PersonDto person, List<PathogenTestDto> sampleTests) {

		try {
			Method method = getInvokeClass().getMethod("get" + propertyId.substring(0, 1).toUpperCase() + propertyId.substring(1));
			Object value = method.invoke(getInvokeObject(caze));
			if (value instanceof Date) {
				Date startDate = CaseLogic.getStartDate(caze.getSymptoms().getOnsetDate(), caze.getReportDate());
				Date lowerThresholdDate = DateHelper.subtractDays(startDate, daysBeforeStartDate);

				return !(((Date) value).equals(lowerThresholdDate)
					|| ((Date) value).equals(startDate)
					|| (((Date) value).after(lowerThresholdDate) && ((Date) value).before(startDate)));
			} else {
				return true;
			}
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String buildDescription() {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, propertyId));
		stringBuilder.append(" ")
			.append(I18nProperties.getString(Strings.classificationNotWithin))
			.append(" ")
			.append(daysBeforeStartDate)
			.append(" ")
			.append(I18nProperties.getString(Strings.classificationDaysBeforeCaseStart));
		return stringBuilder.toString();
	}

	public int getDaysBeforeStartDate() {
		return daysBeforeStartDate;
	}

	public void setDaysBeforeStartDate(int daysBeforeStartDate) {
		this.daysBeforeStartDate = daysBeforeStartDate;
	}
}
