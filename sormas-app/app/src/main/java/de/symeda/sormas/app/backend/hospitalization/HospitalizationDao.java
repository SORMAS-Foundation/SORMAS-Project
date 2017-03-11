package de.symeda.sormas.app.backend.hospitalization;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

/**
 * Created by Mate Strysewske on 22.02.2017.
 */

public class HospitalizationDao extends AbstractAdoDao<Hospitalization> {

    private static final Logger logger = LoggerFactory.getLogger(HospitalizationDao.class);

    public HospitalizationDao(Dao<Hospitalization,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public String getTableName() {
        return Hospitalization.TABLE_NAME;
    }

    public Hospitalization initLazyData(Hospitalization hospitalization) {

        try {
            hospitalization.setPreviousHospitalizations(DatabaseHelper.getPreviousHospitalizationDao().getByHospitalization(hospitalization));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return hospitalization;
    }

    @Override
    public boolean save(Hospitalization hospitalization) {
        try {
            super.save(hospitalization);
            DatabaseHelper.getPreviousHospitalizationDao().deleteOrphansOfHospitalization(hospitalization);
            if (hospitalization.getPreviousHospitalizations() != null && !hospitalization.getPreviousHospitalizations().isEmpty()) {
                for (PreviousHospitalization previousHospitalization : hospitalization.getPreviousHospitalizations()) {
                    previousHospitalization.setHospitalization(hospitalization);
                    DatabaseHelper.getPreviousHospitalizationDao().save(previousHospitalization);
                }
            }
            return true;
        } catch (SQLException e) {
            logger.error(e, "save threw exception");
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean saveUnmodified(Hospitalization hospitalization) {
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
            e.printStackTrace();
            return false;
        }
    }

}
