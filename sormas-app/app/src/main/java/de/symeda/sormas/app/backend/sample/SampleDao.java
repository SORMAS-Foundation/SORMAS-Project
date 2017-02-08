package de.symeda.sormas.app.backend.sample;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.logger.Log;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.util.DataUtils;

/**
 * Created by Mate Strysewske on 06.02.2017.
 */

public class SampleDao extends AbstractAdoDao<Sample> {

    private static final Log.Level LOG_LEVEL = Log.Level.DEBUG;
    private static final Logger logger = LoggerFactory.getLogger(RuntimeExceptionDao.class);

    public SampleDao(Dao<Sample, Long> innerDao) throws SQLException {
        super(innerDao);
    }

    public List<Sample> queryForCase(Case caze) {
        try {
            return queryBuilder().orderBy(Sample.SAMPLE_DATE_TIME, true).where().eq(Sample.ASSOCIATED_CASE+"_id", caze).query();
        } catch (SQLException e) {
            logger.log(LOG_LEVEL, e, "queryForCase threw exception");
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getTableName() {
        return Sample.TABLE_NAME;
    }

}
