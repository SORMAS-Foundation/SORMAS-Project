package de.symeda.sormas.app.backend.region;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.user.User;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class CommunityDao extends AbstractAdoDao<Community> {

    public CommunityDao(Dao<Community,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public String getTableName() {
        return Community.TABLE_NAME;
    }

}
