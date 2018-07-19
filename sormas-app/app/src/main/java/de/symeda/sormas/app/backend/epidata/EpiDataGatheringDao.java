package de.symeda.sormas.app.backend.epidata;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

public class EpiDataGatheringDao extends AbstractAdoDao<EpiDataGathering> {

    public EpiDataGatheringDao(Dao<EpiDataGathering,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<EpiDataGathering> getAdoClass() {
        return EpiDataGathering.class;
    }

    @Override
    public void delete(EpiDataGathering data) throws SQLException {
        DatabaseHelper.getLocationDao().delete(data.getGatheringAddress());
        super.delete(data);
    }

    public List<EpiDataGathering> getByEpiData(EpiData epiData) {
        if (epiData.isSnapshot()) {
            return querySnapshotsForEq(EpiDataGathering.EPI_DATA + "_id", epiData, EpiDataGathering.CHANGE_DATE, false);
        }
        return queryForEq(EpiDataGathering.EPI_DATA + "_id", epiData, EpiDataGathering.CHANGE_DATE, false);
    }

    @Override
    public Date getLatestChangeDate() {
        Date date = super.getLatestChangeDate();
        if (date == null) {
            return null;
        }

        Date locationDate = getLatestChangeDateJoin(Location.TABLE_NAME, EpiDataGathering.GATHERING_ADDRESS);
        if (locationDate != null && locationDate.after(date)) {
            date = locationDate;
        }

        return date;
    }

    @Override
    public String getTableName() {
        return EpiDataGathering.TABLE_NAME;
    }
}
