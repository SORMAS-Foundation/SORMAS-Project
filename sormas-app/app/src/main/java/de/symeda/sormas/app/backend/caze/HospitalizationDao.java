package de.symeda.sormas.app.backend.caze;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.logger.Log;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;

/**
 * Created by Mate Strysewske on 22.02.2017.
 */

public class HospitalizationDao extends AbstractAdoDao<Hospitalization> {

    public HospitalizationDao(Dao<Hospitalization,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public String getTableName() {
        return Hospitalization.TABLE_NAME;
    }

}
