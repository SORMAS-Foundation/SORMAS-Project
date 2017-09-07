package de.symeda.sormas.app.backend.report;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;

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

    public WeeklyReportEntry buildForWeekAndDisease(int week, int year, Disease disease, WeeklyReport report) {
        WeeklyReportEntry entry = super.build();

        entry.setWeeklyReport(report);
        entry.setDisease(disease);
        //entry.setNumberOfCases(??);

        return entry;
    }

}
