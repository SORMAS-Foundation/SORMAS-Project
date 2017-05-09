package de.symeda.sormas.app.backend.hospitalization;

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
 * Created by Mate Strysewske on 22.02.2017.
 */

public class PreviousHospitalizationDao extends AbstractAdoDao<PreviousHospitalization> {

    public PreviousHospitalizationDao(Dao<PreviousHospitalization, Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public String getTableName() {
        return PreviousHospitalization.TABLE_NAME;
    }

    public List<PreviousHospitalization> getByHospitalization(Hospitalization hospitalization) {
        try {
            QueryBuilder qb = queryBuilder();
            qb.where().eq(PreviousHospitalization.HOSPITALIZATION + "_id", hospitalization);
            qb.orderBy(PreviousHospitalization.CHANGE_DATE, false);
            return qb.query();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getByHospitalization on PreviousHospitalization");
            throw new RuntimeException(e);
        }
    }

    public void deleteOrphansOfHospitalization(Hospitalization hospitalization) throws SQLException {
        DeleteBuilder deleteBuilder = deleteBuilder();
        Where where = deleteBuilder.where().eq(PreviousHospitalization.HOSPITALIZATION + "_id", hospitalization);

        // don't delete previous hospitalizations that are still used
        if (hospitalization.getPreviousHospitalizations() != null) {
            Set<Long> idsToKeep = new HashSet<Long>();
            for (PreviousHospitalization prevHosp : hospitalization.getPreviousHospitalizations()) {
                if(prevHosp.getId()!=null) {
                    idsToKeep.add(prevHosp.getId());
                }
            }
            where.and().notIn(PreviousHospitalization.ID, idsToKeep);
        }

        deleteBuilder.delete();
    }

}
