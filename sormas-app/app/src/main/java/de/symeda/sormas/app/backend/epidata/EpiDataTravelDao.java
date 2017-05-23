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

    @Override
    protected Class<EpiDataTravel> getAdoClass() {
        return EpiDataTravel.class;
    }

    public List<EpiDataTravel> getByEpiData(EpiData epiData) {
        return queryForEq(EpiDataTravel.EPI_DATA + "_id", epiData, EpiDataTravel.CHANGE_DATE, false);
    }

    public void deleteOrphansOfEpiData(EpiData epiData) throws SQLException {
        QueryBuilder queryBuilder = queryBuilder();
        Where where = queryBuilder.where().eq(EpiDataTravel.EPI_DATA + "_id", epiData);
        if (epiData.getTravels() != null) {
            Set<Long> idsToKeep = new HashSet<>();
            for (EpiDataTravel travel : epiData.getTravels()) {
                if (travel.getId() != null) {
                    idsToKeep.add(travel.getId());
                }
            }
            where.and().notIn(EpiDataTravel.ID, idsToKeep);
        }

        List<EpiDataTravel> orphans = queryBuilder.query();
        for (EpiDataTravel orphan : orphans) {
            delete(orphan);
        }
    }

    @Override
    public String getTableName() {
        return EpiDataTravel.TABLE_NAME;
    }
}
