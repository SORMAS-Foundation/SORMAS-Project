/*
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.report;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.user.User;

/**
 * Created by Mate Strysewske on 07.09.2017.
 */

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

    public WeeklyReport build(EpiWeek epiWeek) {
        WeeklyReport report = super.build();

        report.setReportDateTime(new Date());
        User currentUser = ConfigProvider.getUser();
        report.setInformant(currentUser);
        report.setHealthFacility(currentUser.getHealthFacility());
        report.setYear(epiWeek.getYear());
        report.setEpiWeek(epiWeek.getWeek());

        // We need to use the getPreviousEpiWeek method because the report date of a weekly report will always
        // be in the week after the epi week the report is built for
        report.setTotalNumberOfCases(DatabaseHelper.getCaseDao().getNumberOfCasesForEpiWeek(epiWeek, currentUser));

        return report;
    }

    public WeeklyReport create(EpiWeek epiWeek) throws DaoException {
        WeeklyReport report = build(epiWeek);
        super.saveAndSnapshot(report);
        report = DatabaseHelper.getWeeklyReportDao().queryUuid(report.getUuid());

        WeeklyReportEntryDao entryDao = DatabaseHelper.getWeeklyReportEntryDao();
        for (Disease disease : Disease.values()) {
            entryDao.create(epiWeek, disease, report);
        }

        return report;
    }

    public WeeklyReport queryForEpiWeek(EpiWeek epiWeek, User informant) {
        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.and(
                    where.eq(WeeklyReport.YEAR, epiWeek.getYear()),
                    where.eq(WeeklyReport.EPI_WEEK, epiWeek.getWeek()),
                    where.eq(WeeklyReport.INFORMANT + "_id", informant)
            );

            return (WeeklyReport) builder.queryForFirst();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform queryForEpiWeek");
            throw new RuntimeException(e);
        }
    }

    /**
     * queries reports by facility (not user!)
     */
    public List<WeeklyReport> queryByDistrict(EpiWeek epiWeek, District district) {
        try {

            QueryBuilder builder = queryBuilder();
            QueryBuilder facilityBuilder = DatabaseHelper.getFacilityDao().queryBuilder();
            facilityBuilder.where().eq(Facility.DISTRICT, district);
            builder.join(facilityBuilder);

            Where where = builder.where();
            where.and(
                    where.eq(WeeklyReport.YEAR, epiWeek.getYear()),
                    where.eq(WeeklyReport.EPI_WEEK, epiWeek.getWeek())
            );

            return builder.query();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform queryByDistrict");
            throw new RuntimeException(e);
        }
    }
}
