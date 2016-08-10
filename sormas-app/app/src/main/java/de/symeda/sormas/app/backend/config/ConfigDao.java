package de.symeda.sormas.app.backend.config;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.user.User;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class ConfigDao extends RuntimeExceptionDao<Config, String> {

    public ConfigDao(Dao<Config,String> innerDao) throws SQLException {
        super(innerDao);
    }
}
