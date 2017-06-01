package de.symeda.sormas.app.backend.sample;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.logger.Log;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

/**
 * Created by Mate Strysewske on 09.02.2017.
 */

public class SampleTestDao extends AbstractAdoDao<SampleTest> {

    public SampleTestDao(Dao<SampleTest, Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<SampleTest> getAdoClass() {
        return SampleTest.class;
    }

    public SampleTest queryMostRecentBySample(Sample sample) {

        if (sample.isSnapshot()) {
            throw new IllegalArgumentException("Does not support snapshot entities");
        }

        try {
            if (sample == null) return null;
            List<SampleTest> tests = queryBuilder()
                    .orderBy(SampleTest.TEST_DATE_TIME, false)
                    .where().eq(SampleTest.SAMPLE + "_id", sample)
                    .and().eq(AbstractDomainObject.SNAPSHOT, false)
                    .query();
            if (!tests.isEmpty()) {
                return tests.get(0);
            } else {
                return null;
            }
        } catch (SQLException e) {
            android.util.Log.e(getTableName(), "Could not perform queryMostRecentBySample on SampleTest");
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getTableName() {
        return SampleTest.TABLE_NAME;
    }

}
