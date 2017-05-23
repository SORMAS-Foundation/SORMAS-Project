package de.symeda.sormas.app.backend.epidata;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.stmt.PreparedQuery;

import java.sql.SQLException;
import java.util.Date;
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
    protected Class<EpiData> getAdoClass() {
        return EpiData.class;
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
    public Date getLatestChangeDate() {
        Date date = super.getLatestChangeDate();
        if (date == null) {
            return null;
        }

        Date burialDate = DatabaseHelper.getEpiDataBurialDao().getLatestChangeDate();
        if (burialDate != null && burialDate.after(date)) {
            date = burialDate;
        }
        Date gatheringDate = DatabaseHelper.getEpiDataGatheringDao().getLatestChangeDate();
        if (gatheringDate != null && gatheringDate.after(date)) {
            date = gatheringDate;
        }
        Date travelDate = DatabaseHelper.getEpiDataTravelDao().getLatestChangeDate();
        if (travelDate != null && travelDate.after(date)) {
            date = travelDate;
        }

        return date;
    }

    // TODO
//    @Override
//    public void save(EpiData epiData) throws DaoException {
//        try {
//            super.save(epiData);
//
//            DatabaseHelper.getEpiDataBurialDao().deleteOrphansOfEpiData(epiData);
//            if (epiData.getBurials() != null && !epiData.getBurials().isEmpty()) {
//                for (EpiDataBurial burial : epiData.getBurials()) {
//                    burial.setEpiData(epiData);
//                    DatabaseHelper.getEpiDataBurialDao().save(burial);
//                }
//            }
//            DatabaseHelper.getEpiDataGatheringDao().deleteOrphansOfEpiData(epiData);
//            if (epiData.getGatherings() != null && !epiData.getGatherings().isEmpty()) {
//                for (EpiDataGathering gathering : epiData.getGatherings()) {
//                    gathering.setEpiData(epiData);
//                    DatabaseHelper.getEpiDataGatheringDao().save(gathering);
//                }
//            }
//            DatabaseHelper.getEpiDataTravelDao().deleteOrphansOfEpiData(epiData);
//            if (epiData.getTravels() != null && !epiData.getTravels().isEmpty()) {
//                for (EpiDataTravel travel : epiData.getTravels()) {
//                    travel.setEpiData(epiData);
//                    DatabaseHelper.getEpiDataTravelDao().save(travel);
//                }
//            }
//
//        } catch (SQLException e) {
//            throw new DaoException(e);
//        }
//    }

}
