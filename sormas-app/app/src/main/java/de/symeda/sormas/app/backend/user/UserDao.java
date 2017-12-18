package de.symeda.sormas.app.backend.user;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.region.District;
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
        return User.class;
    }

    @Override
    public String getTableName() {
        return User.TABLE_NAME;
    }

    public User getByUsername(String username) {
        List<User> users = queryForEq(User.USER_NAME, username);
        if (users.size() == 1) {
            User user = users.get(0);
            return user;
        } else if (users.size() == 0) {
            return null;
        } else {
            throw new RuntimeException("Found multiple users for name " + username);
        }
    }

    public List<User> getByRegionAndRole(Region region, UserRole role) {
        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.and(
                    where.eq(User.REGION + "_id", region.getId()),
                    where.like(User.USER_ROLES_JSON, "%\"" + role.name() + "\"%")
            );

            return (List<User>) builder.query();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getByRegionAndRole");
            throw new RuntimeException(e);
        }
    }

    public List<User> getByDistrictAndRole(District district, UserRole role, String orderBy) {
        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.and(
                    where.eq(User.DISTRICT + "_id", district.getId()),
                    where.like(User.USER_ROLES_JSON, "%\"" + role.name() + "\"%")
            );

            return (List<User>) builder.orderBy(orderBy, true).query();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getByDistrictAndRole");
            throw new RuntimeException(e);
        }
    }

}
