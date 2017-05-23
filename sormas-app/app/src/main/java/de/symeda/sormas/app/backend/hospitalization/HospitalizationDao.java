package de.symeda.sormas.app.backend.hospitalization;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

/**
 * Created by Mate Strysewske on 22.02.2017.
 */

public class HospitalizationDao extends AbstractAdoDao<Hospitalization> {

    public HospitalizationDao(Dao<Hospitalization,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<Hospitalization> getAdoClass() {
        return Hospitalization.class;
    }

    @Override
    public String getTableName() {
        return Hospitalization.TABLE_NAME;
    }

    public Hospitalization initLazyData(Hospitalization hospitalization) {
        hospitalization.setPreviousHospitalizations(DatabaseHelper.getPreviousHospitalizationDao().getByHospitalization(hospitalization));
        return hospitalization;
    }

    // TODO
//    @Override
//    public void save(Hospitalization hospitalization) throws DaoException {
//        try {
//            super.save(hospitalization);
//
//            DatabaseHelper.getPreviousHospitalizationDao().deleteOrphansOfHospitalization(hospitalization);
//            if (hospitalization.getPreviousHospitalizations() != null && !hospitalization.getPreviousHospitalizations().isEmpty()) {
//                for (PreviousHospitalization previousHospitalization : hospitalization.getPreviousHospitalizations()) {
//                    previousHospitalization.setHospitalization(hospitalization);
//                    DatabaseHelper.getPreviousHospitalizationDao().save(previousHospitalization);
//                }
//            }
//
//        } catch (SQLException e) {
//            throw new DaoException(e);
//        }
//    }
}
