package de.symeda.sormas.app.backend.config;

import android.util.Log;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Some methods are copied from {@link com.j256.ormlite.dao.RuntimeExceptionDao}.
 *
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class ConfigDao {

    private Dao<Config, String> dao;

    public ConfigDao(Dao<Config,String> innerDao) throws SQLException {
        this.dao = innerDao;
    }

    /**
     * @see Dao#queryForId(Object)
     */
    public Config queryForId(String id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            Log.e(getClass().getName(), "queryForId threw exception on: " + id, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @see Dao#delete(Object)
     */
    public int delete(Config data) {
        try {
            return dao.delete(data);
        } catch (SQLException e) {
            Log.e(getClass().getName(), "delete threw exception on: " + data, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @see Dao#createOrUpdate(Object)
     */
    public Dao.CreateOrUpdateStatus createOrUpdate(Config data) {
        try {
            return dao.createOrUpdate(data);
        } catch (SQLException e) {
            Log.e(getClass().getName(), "createOrUpdate threw exception on: " + data, e);
            throw new RuntimeException(e);
        }
    }
}
