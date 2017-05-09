package de.symeda.sormas.app.backend.user;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.person.Person;

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

    @Override
    public boolean save(User user) throws DaoException {

        if (user.getAddress() != null) {
            DatabaseHelper.getLocationDao().save(user.getAddress());
        }

        return super.save(user);
    }

    @Override
    public boolean saveUnmodified(User user) throws DaoException {

        if (user.getAddress() != null) {
            DatabaseHelper.getLocationDao().saveUnmodified(user.getAddress());
        }

        return super.saveUnmodified(user);
    }
}
