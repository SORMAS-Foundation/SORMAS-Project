package de.symeda.sormas.app.backend.person;

import android.util.Log;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.util.DataUtils;

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
    public boolean save(Person person) throws DaoException {

        if (person.getAddress() != null) {
            DatabaseHelper.getLocationDao().save(person.getAddress());
        }
        if (person.getBurialLocation() != null) {
            DatabaseHelper.getLocationDao().save(person.getBurialLocation());
        }
        if (person.getDeathLocation() != null) {
            DatabaseHelper.getLocationDao().save(person.getDeathLocation());
        }

        return super.save(person);
    }

    @Override
    public boolean saveUnmodified(Person person) throws DaoException {

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

    public List<Person> getAllByName(String firstName, String lastName) {
        try {
            return queryBuilder().where().eq(Person.FIRST_NAME, firstName).and().eq(Person.LAST_NAME, lastName).query();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getAllByName on Person");
            throw new RuntimeException(e);
        }
    }

    public static Person createPerson() {
        Person person = DataUtils.createNew(Person.class);

        person.setAddress(DataUtils.createNew(Location.class));
        person.setBurialLocation(DataUtils.createNew(Location.class));
        person.setDeathLocation(DataUtils.createNew(Location.class));

        return person;
    }
}
