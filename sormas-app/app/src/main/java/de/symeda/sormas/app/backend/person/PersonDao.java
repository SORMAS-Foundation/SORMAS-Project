package de.symeda.sormas.app.backend.person;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

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

    @Override
    public boolean saveUnmodified(Person person) {

        if (person.getAddress() != null) {
            DatabaseHelper.getLocationDao().saveUnmodified(person.getAddress());
        }
        if (person.getBurialLocation() != null) {
            DatabaseHelper.getLocationDao().saveUnmodified(person.getBurialLocation());
        }
        if (person.getDeathLocation() != null) {
            DatabaseHelper.getLocationDao().saveUnmodified(person.getDeathLocation());
        }

        return super.saveUnmodified(person);
    }

    public List<Person> getAllByName(String firstName, String lastName) throws SQLException {
        return queryBuilder().where().eq(Person.FIRST_NAME, firstName).and().eq(Person.LAST_NAME, lastName).query();
    }

}
