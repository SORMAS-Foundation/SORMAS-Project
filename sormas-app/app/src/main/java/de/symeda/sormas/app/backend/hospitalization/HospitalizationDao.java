package de.symeda.sormas.app.backend.hospitalization;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

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

    @Override
    public boolean save(Hospitalization hospitalization) throws DaoException {
        try {
            if (!super.save(hospitalization)) {
                return false;
            }

            DatabaseHelper.getPreviousHospitalizationDao().deleteOrphansOfHospitalization(hospitalization);
            if (hospitalization.getPreviousHospitalizations() != null && !hospitalization.getPreviousHospitalizations().isEmpty()) {
                for (PreviousHospitalization previousHospitalization : hospitalization.getPreviousHospitalizations()) {
                    previousHospitalization.setHospitalization(hospitalization);
                    DatabaseHelper.getPreviousHospitalizationDao().save(previousHospitalization);
                }
            }
            return true;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean saveUnmodified(Hospitalization hospitalization) throws DaoException {
        try {
            super.saveUnmodified(hospitalization);
            DatabaseHelper.getPreviousHospitalizationDao().deleteOrphansOfHospitalization(hospitalization);
            if (hospitalization.getPreviousHospitalizations() != null && !hospitalization.getPreviousHospitalizations().isEmpty()) {
                for (PreviousHospitalization previousHospitalization : hospitalization.getPreviousHospitalizations()) {
                    previousHospitalization.setHospitalization(hospitalization);
                    DatabaseHelper.getPreviousHospitalizationDao().saveUnmodified(previousHospitalization);
                }
            }
            return true;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

}
