package de.symeda.sormas.app.backend.environment;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;

public class EnvironmentDao extends AbstractAdoDao<Environment> {

    public EnvironmentDao(Dao<Environment, Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<Environment> getAdoClass() {
        return Environment.class;
    }

    @Override
    public String getTableName() {
        return Environment.TABLE_NAME;
    }
}
