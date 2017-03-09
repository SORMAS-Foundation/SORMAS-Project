package de.symeda.sormas.app.backend.hospitalization;

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

    public List<PreviousHospitalization> getByHospitalization(Hospitalization hospitalization) throws SQLException {
        QueryBuilder qb = queryBuilder();
        qb.where().eq(PreviousHospitalization.HOSPITALIZATION + "_id", hospitalization);
        qb.orderBy(PreviousHospitalization.CHANGE_DATE, false);
        return qb.query();

    }

    public long getCntByHospitalization(Hospitalization hospitalization) throws SQLException {
        QueryBuilder qb = queryBuilder();
        qb.where().eq(PreviousHospitalization.HOSPITALIZATION + "_id", hospitalization);
        return qb.countOf();

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
