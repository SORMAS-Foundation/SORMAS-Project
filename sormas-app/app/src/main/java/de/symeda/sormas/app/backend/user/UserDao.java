package de.symeda.sormas.app.backend.user;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.Region;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class UserDao extends AbstractAdoDao<User> {

    public UserDao(Dao<User,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<User> getAdoClass() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTableName() {
        return User.TABLE_NAME;
    }

    public User getByUsername(String username) {
        List<User> users = queryForEq(User.USER_NAME, username);
        if (users.size() == 0) {
            return null;
        } else if (users.size() == 1) {
            return users.get(0);
        } else {
            throw new RuntimeException("Found multiple users for name " + username);
        }
    }

    public List<User> getByRegionAndRole(Region region, UserRole role) {
        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.and(
                    where.eq(User.REGION, region),
                    where.eq(User.USER_ROLE, role)
            );

            return (List<User>) builder.query();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getByRegionAndRole");
            throw new RuntimeException(e);
        }
    }
}
