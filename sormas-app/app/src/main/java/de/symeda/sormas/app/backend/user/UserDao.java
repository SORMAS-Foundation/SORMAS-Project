package de.symeda.sormas.app.backend.user;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.facility.Facility;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class UserDao extends AbstractAdoDao<User> {

    public UserDao(Dao<User,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public String getTableName() {
        return User.TABLE_NAME;
    }

}
