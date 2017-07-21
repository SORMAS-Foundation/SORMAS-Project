package de.symeda.sormas.app.backend.region;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.user.User;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class CommunityDao extends AbstractAdoDao<Community> {

    public CommunityDao(Dao<Community,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<Community> getAdoClass() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTableName() {
        return Community.TABLE_NAME;
    }

    public List<Community> getByDistrict(District district) {
        return queryForEq(Community.DISTRICT+"_id", district, Community.NAME, true);
    }
}
