package de.symeda.sormas.app.backend.person;

import android.util.Log;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.epidata.EpiDataGathering;
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
    protected Class<Person> getAdoClass() {
        return Person.class;
    }

    @Override
    public String getTableName() {
        return Person.TABLE_NAME;
    }

    public List<Person> getAllByName(String firstName, String lastName) {
        try {
            return queryBuilder().where().eq(Person.FIRST_NAME, firstName).and().eq(Person.LAST_NAME, lastName).query();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getAllByName on Person");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Date getLatestChangeDate() {
        Date date = super.getLatestChangeDate();
        if (date == null) {
            return null;
        }

        Date locationDate = getLatestChangeDateJoin(Location.TABLE_NAME, Person.ADDRESS);
        if (locationDate != null && locationDate.after(date)) {
            date = locationDate;
        }
        locationDate = getLatestChangeDateJoin(Location.TABLE_NAME, Person.BURIAL_LOCATION);
        if (locationDate != null && locationDate.after(date)) {
            date = locationDate;
        }
        locationDate = getLatestChangeDateJoin(Location.TABLE_NAME, Person.DEATH_LOCATION);
        if (locationDate != null && locationDate.after(date)) {
            date = locationDate;
        }
		
        return date;
    }

    @Override
    public Person create() {
        Person person = super.create();

        person.setAddress(DatabaseHelper.getLocationDao().create());

        return person;
    }
}
