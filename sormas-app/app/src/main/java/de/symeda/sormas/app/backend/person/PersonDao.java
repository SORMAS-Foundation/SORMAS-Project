package de.symeda.sormas.app.backend.person;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.NonUniqueResultException;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class PersonDao extends AbstractAdoDao<Person> {

    public PersonDao(Dao<Person,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public String getTableName() {
        return Person.TABLE_NAME;
    }

    public List<Person> getAllPersonsWithoutCase() throws SQLException {
        return queryBuilder().where().isNull("caze_id").query();
    }

}
