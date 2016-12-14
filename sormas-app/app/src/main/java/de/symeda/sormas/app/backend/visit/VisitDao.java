package de.symeda.sormas.app.backend.visit;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;

public class VisitDao extends AbstractAdoDao<Visit> {

    public VisitDao(Dao<Visit,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public String getTableName() {
        return Visit.TABLE_NAME;
    }

}
