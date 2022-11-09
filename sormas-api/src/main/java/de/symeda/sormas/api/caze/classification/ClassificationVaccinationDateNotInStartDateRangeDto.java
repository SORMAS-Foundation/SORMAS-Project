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

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.utils.DateHelper;

/**
 * Classification criteria that is applicable when any immunization associated with the case person
 * with the same disease as the case has a vaccination with a vaccination date within the date range
 * specified by daysBeforeStartDate and the start date of the case
 */
public class ClassificationVaccinationDateNotInStartDateRangeDto extends ClassificationCaseCriteriaDto {

	private static final long serialVersionUID = -8817472226784147694L;

	private int daysBeforeStartDate;

	public ClassificationVaccinationDateNotInStartDateRangeDto(int daysBeforeStartDate) {
		this.daysBeforeStartDate = daysBeforeStartDate;
	}

	@Override
	public boolean eval(CaseDataDto caze, PersonDto person, List<PathogenTestDto> sampleTests, List<EventDto> events, Date lastVaccinationDate) {

		Date startDate = CaseLogic.getStartDate(caze.getSymptoms().getOnsetDate(), caze.getReportDate());
		Date lowerThresholdDate = DateHelper.subtractDays(startDate, daysBeforeStartDate);

		return lastVaccinationDate == null
			|| !(lastVaccinationDate.equals(lowerThresholdDate)
				|| lastVaccinationDate.equals(startDate)
				|| (lastVaccinationDate.after(lowerThresholdDate) && lastVaccinationDate.before(startDate)));
	}

	@Override
	public String buildDescription() {

		return I18nProperties.getString(Strings.classificationLastVaccinationDateWithin) + " " + daysBeforeStartDate + " "
			+ I18nProperties.getString(Strings.classificationDaysBeforeCaseStart);
	}

	public int getDaysBeforeStartDate() {
		return daysBeforeStartDate;
	}

	public void setDaysBeforeStartDate(int daysBeforeStartDate) {
		this.daysBeforeStartDate = daysBeforeStartDate;
	}
}
