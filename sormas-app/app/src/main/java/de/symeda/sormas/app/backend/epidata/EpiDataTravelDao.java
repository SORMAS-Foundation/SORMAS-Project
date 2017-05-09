package de.symeda.sormas.app.backend.epidata;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

public class EpiDataTravelDao extends AbstractAdoDao<EpiDataTravel> {

    public EpiDataTravelDao(Dao<EpiDataTravel,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    public List<EpiDataTravel> getByEpiData(EpiData epiData) {
        try {
            QueryBuilder qb = queryBuilder();
            qb.where().eq(EpiDataTravel.EPI_DATA + "_id", epiData);
            qb.orderBy(EpiDataTravel.CHANGE_DATE, false);
            return qb.query();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getByEpiData on EpiDataTravel");
            throw new RuntimeException(e);
        }
    }

    public void deleteOrphansOfEpiData(EpiData epiData) throws SQLException {
        DeleteBuilder deleteBuilder = deleteBuilder();
        Where where = deleteBuilder.where().eq(EpiDataGathering.EPI_DATA + "_id", epiData);
        if (epiData.getGatherings() != null) {
            Set<Long> idsToKeep = new HashSet<>();
            for (EpiDataGathering gathering : epiData.getGatherings()) {
                if (gathering.getId() != null) {
                    idsToKeep.add(gathering.getId());
                }
            }
            where.and().notIn(EpiDataGathering.ID, idsToKeep);
        }
        deleteBuilder.delete();
    }

    @Override
    public String getTableName() {
        return EpiDataTravel.TABLE_NAME;
    }
}
