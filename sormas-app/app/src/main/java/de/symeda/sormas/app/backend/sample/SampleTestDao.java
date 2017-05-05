package de.symeda.sormas.app.backend.sample;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.logger.Log;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;

/**
 * Created by Mate Strysewske on 09.02.2017.
 */

public class SampleTestDao extends AbstractAdoDao<SampleTest> {

    private static final Log.Level LOG_LEVEL = Log.Level.DEBUG;
    private static final Logger logger = LoggerFactory.getLogger(RuntimeExceptionDao.class);

    public SampleTestDao(Dao<SampleTest, Long> innerDao) throws SQLException {
        super(innerDao);
    }

    public SampleTest getMostRecentForSample(Sample sample) throws SQLException {
        if(sample == null) return null;
        List<SampleTest> tests = queryBuilder().orderBy(SampleTest.TEST_DATE_TIME, false).where().eq(SampleTest.SAMPLE + "_id", sample).query();
        if (!tests.isEmpty()) {
            return tests.get(0);
        } else {
            return null;
        }
    }

    @Override
    public String getTableName() {
        return SampleTest.TABLE_NAME;
    }

}
