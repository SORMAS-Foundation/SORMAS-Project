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

    public List<WeeklyReportEntry> getAllByWeeklyReport(WeeklyReport report) {
        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.eq(WeeklyReportEntry.WEEKLY_REPORT + "_id", report);

            return (List<WeeklyReportEntry>) builder.query();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getAllByWeeklyReport");
            throw new RuntimeException(e);
        }
    }

    public WeeklyReportEntry build(EpiWeek epiWeek, Disease disease, WeeklyReport report) {
        WeeklyReportEntry entry = super.build();

        entry.setWeeklyReport(report);
        entry.setDisease(disease);
        entry.setNumberOfCases(DatabaseHelper.getCaseDao().getNumberOfCasesForEpiWeekAndDisease(epiWeek, disease));

        return entry;
    }

    public WeeklyReportEntry create(EpiWeek epiWeek, Disease disease, WeeklyReport report) throws DaoException {
        WeeklyReportEntry entry = build(epiWeek, disease, report);
        return super.saveAndSnapshot(entry);
    }

}
