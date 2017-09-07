package de.symeda.sormas.app.backend.report;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
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

    public WeeklyReport buildForWeek(int week, int year) {
        WeeklyReport report = super.build();

        report.setReportDateTime(new Date());
        User currentUser = ConfigProvider.getUser();
        report.setInformant(currentUser);
        report.setHealthFacility(currentUser.getHealthFacility());
        //report.setTotalNumberOfCases(??)

        return report;
    }

    public WeeklyReport createForWeek(int week, int year) throws DaoException {
        WeeklyReport report = buildForWeek(week, year);
        report = super.saveAndSnapshot(report);

        WeeklyReportEntryDao entryDao = DatabaseHelper.getWeeklyReportEntryDao();
        for (Disease disease : Disease.values()) {
            WeeklyReportEntry entry = entryDao.buildForWeekAndDisease(week, year, disease, report);
            entryDao.saveAndSnapshot(entry);
        }

        return report;
    }

}
