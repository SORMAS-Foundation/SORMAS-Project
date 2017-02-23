package de.symeda.sormas.app.backend.caze;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;

/**
 * Created by Mate Strysewske on 22.02.2017.
 */

public class PreviousHospitalizationDao extends AbstractAdoDao<PreviousHospitalization> {

    public PreviousHospitalizationDao(Dao<PreviousHospitalization,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public String getTableName() {
        return PreviousHospitalization.TABLE_NAME;
    }

}
