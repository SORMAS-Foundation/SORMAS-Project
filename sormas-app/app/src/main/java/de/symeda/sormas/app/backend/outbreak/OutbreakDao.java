package de.symeda.sormas.app.backend.outbreak;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class OutbreakDao extends AbstractAdoDao<Outbreak> {

    public OutbreakDao(Dao<Outbreak,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<Outbreak> getAdoClass() {
        return Outbreak.class;
    }

    @Override
    public String getTableName() {
        return Outbreak.TABLE_NAME;
    }

    public boolean hasOutbreak(District district, Disease disease) {
        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.and(
                    where.eq(Outbreak.DISTRICT, district),
                    where.eq(Outbreak.DISEASE, disease)
            );
            int result = (int) builder.countOf();
            return result > 0;
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getNumberOfCasesForEpiWeekAndDisease");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Outbreak saveAndSnapshot(Outbreak source) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Outbreak mergeOrCreate(Outbreak source) throws DaoException {
        throw new UnsupportedOperationException();
    }
}
