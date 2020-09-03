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

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

/**
 * Created by Mate Strysewske on 07.09.2017.
 */
public class WeeklyReportEntryDao extends AbstractAdoDao<WeeklyReportEntry> {

	public WeeklyReportEntryDao(Dao<WeeklyReportEntry, Long> innerDao) throws SQLException {
		super(innerDao);
	}

	@Override
	protected Class<WeeklyReportEntry> getAdoClass() {
		return WeeklyReportEntry.class;
	}

	@Override
	public String getTableName() {
		return WeeklyReportEntry.TABLE_NAME;
	}

	public List<WeeklyReportEntry> getByWeeklyReport(WeeklyReport report) {
		if (report.isSnapshot()) {
			return querySnapshotsForEq(WeeklyReportEntry.WEEKLY_REPORT + "_id", report, WeeklyReportEntry.DISEASE, true);
		}
		return queryForEq(WeeklyReportEntry.WEEKLY_REPORT + "_id", report, WeeklyReportEntry.DISEASE, true);
	}

	public WeeklyReportEntry build(EpiWeek epiWeek, Disease disease, WeeklyReport report) {
		WeeklyReportEntry entry = super.build();

		entry.setWeeklyReport(report);
		entry.setDisease(disease);

		int numberOfCases = DatabaseHelper.getCaseDao().getNumberOfCasesForEpiWeekAndDisease(epiWeek, disease, report.getReportingUser());
		entry.setNumberOfCases(numberOfCases);

		return entry;
	}
}
