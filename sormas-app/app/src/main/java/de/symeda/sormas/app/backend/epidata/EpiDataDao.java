package de.symeda.sormas.app.backend.epidata;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.stmt.PreparedQuery;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.location.LocationDao;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

public class EpiDataDao extends AbstractAdoDao<EpiData> {

    private static final Logger logger = LoggerFactory.getLogger(EpiDataDao.class);

    public EpiDataDao(Dao<EpiData,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public String getTableName() {
        return EpiData.TABLE_NAME;
    }

    public EpiData initLazyData(EpiData epiData) {
        epiData.setBurials(DatabaseHelper.getEpiDataBurialDao().getByEpiData(epiData));
        epiData.setGatherings(DatabaseHelper.getEpiDataGatheringDao().getByEpiData(epiData));
        epiData.setTravels(DatabaseHelper.getEpiDataTravelDao().getByEpiData(epiData));

        return epiData;
    }

    @Override
    public boolean save(EpiData epiData) throws DaoException {
        try {
            if (!super.save(epiData)) {
                return false;
            }
            DatabaseHelper.getEpiDataBurialDao().deleteOrphansOfEpiData(epiData);
            if (epiData.getBurials() != null && !epiData.getBurials().isEmpty()) {
                for (EpiDataBurial burial : epiData.getBurials()) {
                    burial.setEpiData(epiData);
                    LocationDao locationDao = DatabaseHelper.getLocationDao();
                    if (burial.getBurialAddress() != null) {
                        locationDao.save(burial.getBurialAddress());
                    }
                    DatabaseHelper.getEpiDataBurialDao().save(burial);
                }
            }
            DatabaseHelper.getEpiDataGatheringDao().deleteOrphansOfEpiData(epiData);
            if (epiData.getGatherings() != null && !epiData.getGatherings().isEmpty()) {
                for (EpiDataGathering gathering : epiData.getGatherings()) {
                    gathering.setEpiData(epiData);
                    LocationDao locationDao = DatabaseHelper.getLocationDao();
                    if (gathering.getGatheringAddress() != null) {
                        locationDao.save(gathering.getGatheringAddress());
                    }
                    DatabaseHelper.getEpiDataGatheringDao().save(gathering);
                }
            }
            DatabaseHelper.getEpiDataTravelDao().deleteOrphansOfEpiData(epiData);
            if (epiData.getTravels() != null && !epiData.getTravels().isEmpty()) {
                for (EpiDataTravel travel : epiData.getTravels()) {
                    travel.setEpiData(epiData);
                    DatabaseHelper.getEpiDataTravelDao().save(travel);
                }
            }
            return true;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean saveUnmodified(EpiData epiData) throws DaoException {
        try {
            super.saveUnmodified(epiData);
            DatabaseHelper.getEpiDataBurialDao().deleteOrphansOfEpiData(epiData);
            if (epiData.getBurials() != null && !epiData.getBurials().isEmpty()) {
                for (EpiDataBurial burial : epiData.getBurials()) {
                    burial.setEpiData(epiData);
                    DatabaseHelper.getEpiDataBurialDao().saveUnmodified(burial);
                }
            }
            DatabaseHelper.getEpiDataGatheringDao().deleteOrphansOfEpiData(epiData);
            if (epiData.getGatherings() != null && !epiData.getGatherings().isEmpty()) {
                for (EpiDataGathering gathering : epiData.getGatherings()) {
                    gathering.setEpiData(epiData);
                    DatabaseHelper.getEpiDataGatheringDao().saveUnmodified(gathering);
                }
            }
            DatabaseHelper.getEpiDataTravelDao().deleteOrphansOfEpiData(epiData);
            if (epiData.getTravels() != null && !epiData.getTravels().isEmpty()) {
                for (EpiDataTravel travel : epiData.getTravels()) {
                    travel.setEpiData(epiData);
                    DatabaseHelper.getEpiDataTravelDao().saveUnmodified(travel);
                }
            }
            return true;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}
