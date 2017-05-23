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
    public EpiDataGathering create() {
        EpiDataGathering ado = super.create();
        ado.setGatheringAddress(DatabaseHelper.getLocationDao().create());
        return ado;
    }

    @Override
    public int delete(EpiDataGathering data) {

        // TODO we need to make sure the other delete methods don't work

        DatabaseHelper.getLocationDao().delete(data.getGatheringAddress());

        return super.delete(data);
    }

    public List<EpiDataGathering> getByEpiData(EpiData epiData) {
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

    public void deleteOrphansOfEpiData(EpiData epiData) throws SQLException {
        QueryBuilder queryBuilder = queryBuilder();
        Where where = queryBuilder.where().eq(EpiDataGathering.EPI_DATA + "_id", epiData);
        if (epiData.getGatherings() != null) {
            Set<Long> idsToKeep = new HashSet<>();
            for (EpiDataGathering gathering : epiData.getGatherings()) {
                if (gathering.getId() != null) {
                    idsToKeep.add(gathering.getId());
                }
            }
            where.and().notIn(EpiDataGathering.ID, idsToKeep);
        }

        List<EpiDataGathering> orphans = queryBuilder.query();
        for (EpiDataGathering orphan : orphans) {
            delete(orphan);
        }
    }

    @Override
    public String getTableName() {
        return EpiDataGathering.TABLE_NAME;
    }
}
