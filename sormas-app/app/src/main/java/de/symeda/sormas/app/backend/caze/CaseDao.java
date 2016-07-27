package de.symeda.sormas.app.backend.caze;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.person.Person;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class CaseDao extends AbstractAdoDao<Case> {

    public CaseDao(Dao<Case,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public String getTableName() {
        return Case.TABLE_NAME;
    }
}
