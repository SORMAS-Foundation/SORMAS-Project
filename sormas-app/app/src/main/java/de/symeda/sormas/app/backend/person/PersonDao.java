package de.symeda.sormas.app.backend.person;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.NonUniqueResultException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class PersonDao extends AbstractAdoDao<Person> {

    public PersonDao(Dao<Person,Long> innerDao) throws SQLException {
        super(innerDao);
    }
}
