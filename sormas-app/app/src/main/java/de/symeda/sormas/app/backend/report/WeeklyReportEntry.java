/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.report;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.EmbeddedAdo;

/**
 * Created by Mate Strysewske on 07.09.2017.
 */
@Entity(name = WeeklyReportEntry.TABLE_NAME)
@DatabaseTable(tableName = WeeklyReportEntry.TABLE_NAME)
@EmbeddedAdo(parentAccessor = WeeklyReportEntry.WEEKLY_REPORT)
public class WeeklyReportEntry extends AbstractDomainObject {

	private static final long serialVersionUID = -4161597011857710604L;

	public static final String TABLE_NAME = "weeklyreportentry";
	public static final String I18N_PREFIX = "WeeklyReportEntry";

	public static final String WEEKLY_REPORT = "weeklyReport";
	public static final String DISEASE = "disease";
	public static final String NUMBER_OF_CASES = "numberOfCases";

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private WeeklyReport weeklyReport;

	@Enumerated(EnumType.STRING)
	@Column
	private Disease disease;

	@Column
	private Integer numberOfCases;

	public WeeklyReport getWeeklyReport() {
		return weeklyReport;
	}

	public void setWeeklyReport(WeeklyReport weeklyReport) {
		this.weeklyReport = weeklyReport;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public Integer getNumberOfCases() {
		return numberOfCases;
	}

	public void setNumberOfCases(Integer numberOfCases) {
		this.numberOfCases = numberOfCases;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
