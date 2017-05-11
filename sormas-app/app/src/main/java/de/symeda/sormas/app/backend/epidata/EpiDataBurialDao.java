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
import de.symeda.sormas.app.backend.common.DatabaseHelper;

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
    public EpiDataBurial create() {
        EpiDataBurial ado = super.create();
        ado.setBurialAddress(DatabaseHelper.getLocationDao().create());
        return ado;
    }


    public List<EpiDataBurial> getByEpiData(EpiData epiData) {
        try {
            QueryBuilder qb = queryBuilder();
            qb.where().eq(EpiDataBurial.EPI_DATA + "_id", epiData);
            qb.orderBy(EpiDataBurial.CHANGE_DATE, false);
            return qb.query();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getByEpiData on EpiDataBurial");
            throw new RuntimeException(e);
        }
    }

    public void deleteOrphansOfEpiData(EpiData epiData) throws SQLException {
        DeleteBuilder deleteBuilder = deleteBuilder();
        Where where = deleteBuilder.where().eq(EpiDataBurial.EPI_DATA + "_id", epiData);
        if (epiData.getBurials() != null) {
            Set<Long> idsToKeep = new HashSet<>();
            for (EpiDataBurial burial : epiData.getBurials()) {
                if (burial.getId() != null) {
                    idsToKeep.add(burial.getId());
                }
            }
            where.and().notIn(EpiDataBurial.ID, idsToKeep);
        }

        deleteBuilder.delete();
    }

    @Override
    public String getTableName() {
        return EpiDataBurial.TABLE_NAME;
    }
}
