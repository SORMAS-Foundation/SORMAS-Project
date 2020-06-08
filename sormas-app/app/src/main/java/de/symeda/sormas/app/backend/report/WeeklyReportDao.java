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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;

public class WeeklyReportDao extends AbstractAdoDao<WeeklyReport> {

	public WeeklyReportDao(Dao<WeeklyReport, Long> innerDao) throws SQLException {
		super(innerDao);
	}

	@Override
	protected Class<WeeklyReport> getAdoClass() {
		return WeeklyReport.class;
	}

	@Override
	public String getTableName() {
		return WeeklyReport.TABLE_NAME;
	}

	@Override
	public WeeklyReport build() {
		throw new UnsupportedOperationException("Use build(Date) instead");
	}

	@Override
	public WeeklyReport queryUuid(String uuid) {
		WeeklyReport data = super.queryUuid(uuid);
		initLazyData(data);
		return data;
	}

	@Override
	public WeeklyReport querySnapshotByUuid(String uuid) {
		WeeklyReport data = super.querySnapshotByUuid(uuid);
		initLazyData(data);
		return data;
	}

	@Override
	public WeeklyReport queryForId(Long id) {
		WeeklyReport data = super.queryForId(id);
		initLazyData(data);
		return data;
	}

	public WeeklyReport initLazyData(WeeklyReport report) {
		if (report != null) {
			report.setReportEntries(DatabaseHelper.getWeeklyReportEntryDao().getByWeeklyReport(report));
		}
		return report;
	}

	@Override
	public WeeklyReport saveAndSnapshot(WeeklyReport ado) throws DaoException {

		WeeklyReport snapshot = super.saveAndSnapshot(ado);

		DatabaseHelper.getWeeklyReportEntryDao()
			.saveCollectionWithSnapshot(DatabaseHelper.getWeeklyReportEntryDao().getByWeeklyReport(ado), ado.getReportEntries(), ado);

		return snapshot;
	}

	@Override
	public Date getLatestChangeDate() {
		Date date = super.getLatestChangeDate();
		if (date == null) {
			return null;
		}

		Date entryDate = DatabaseHelper.getWeeklyReportEntryDao().getLatestChangeDate();
		if (entryDate != null && entryDate.after(date)) {
			date = entryDate;
		}

		return date;
	}

	public WeeklyReport build(EpiWeek epiWeek) {
		WeeklyReport report = super.build();

		report.setReportDateTime(new Date());
		User currentUser = ConfigProvider.getUser();
		report.setReportingUser(currentUser);
		report.setHealthFacility(currentUser.getHealthFacility());
		report.setCommunity(currentUser.getCommunity());
		report.setYear(epiWeek.getYear());
		report.setEpiWeek(epiWeek.getWeek());
		report.setAssignedOfficer(currentUser.getAssociatedOfficer());
		report.setDistrict(currentUser.getDistrict());

		int totalNumberOfCases = DatabaseHelper.getCaseDao().getNumberOfCasesForEpiWeek(epiWeek, currentUser);
		report.setTotalNumberOfCases(totalNumberOfCases);

		WeeklyReportEntryDao entryDao = DatabaseHelper.getWeeklyReportEntryDao();
		List<WeeklyReportEntry> entries = new ArrayList<>();
		for (Disease disease : DiseaseConfigurationCache.getInstance().getAllDiseases(true, true, true)) {
			entries.add(entryDao.build(epiWeek, disease, report));
		}
		report.setReportEntries(entries);

		return report;
	}

	public WeeklyReport queryByEpiWeekAndUser(EpiWeek epiWeek, User user) {
		if (!(ConfigProvider.hasUserRight(UserRight.WEEKLYREPORT_CREATE))) {
			throw new IllegalArgumentException("queryByEpiWeekAndUser is only supported for users who can create weekly reports");
		}

		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.and(
				where.eq(WeeklyReport.REPORTING_USER + "_id", user),
				where.eq(WeeklyReport.YEAR, epiWeek.getYear()),
				where.eq(WeeklyReport.EPI_WEEK, epiWeek.getWeek()));

			WeeklyReport result = (WeeklyReport) builder.queryForFirst();
			initLazyData(result);
			return result;

		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform queryByEpiWeekAndUser");
			throw new RuntimeException(e);
		}
	}
}
