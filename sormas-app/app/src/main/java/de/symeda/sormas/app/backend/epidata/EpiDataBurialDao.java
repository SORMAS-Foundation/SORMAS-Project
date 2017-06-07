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
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

public class EpiDataBurialDao extends AbstractAdoDao<EpiDataBurial> {

    public EpiDataBurialDao(Dao<EpiDataBurial,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<EpiDataBurial> getAdoClass() {
        return EpiDataBurial.class;
    }

    @Override
    public int delete(EpiDataBurial data) {

        // TODO we need to make sure the other delete methods don't work

        DatabaseHelper.getLocationDao().delete(data.getBurialAddress());

        return super.delete(data);
    }

    public List<EpiDataBurial> getByEpiData(EpiData epiData) {
        if (epiData.isSnapshot()) {
            return querySnapshotsForEq(EpiDataBurial.EPI_DATA + "_id", epiData, EpiDataBurial.CHANGE_DATE, false);
        }
        return queryForEq(EpiDataBurial.EPI_DATA + "_id", epiData, EpiDataBurial.CHANGE_DATE, false);
    }

    @Override
    public Date getLatestChangeDate() {
        Date date = super.getLatestChangeDate();
        if (date == null) {
            return null;
        }

        Date locationDate = getLatestChangeDateJoin(Location.TABLE_NAME, EpiDataBurial.BURIAL_ADDRESS);
        if (locationDate != null && locationDate.after(date)) {
            date = locationDate;
        }

        return date;
    }

    @Override
    public String getTableName() {
        return EpiDataBurial.TABLE_NAME;
    }
}
