package de.symeda.sormas.app.backend.person;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.field.DataType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.person.PersonNameDto;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.location.Location;

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

    public List<PersonNameDto> getPersonNameDtos() {
        try {
            GenericRawResults<Object[]> rawResults = queryRaw("select " + Person.FIRST_NAME +
                    ", " + Person.LAST_NAME + ", " + Person.ID + " from " +
                    Person.TABLE_NAME + " where " + Person.TABLE_NAME + "." + AbstractDomainObject.SNAPSHOT + " = 0;",
                    new DataType[]{DataType.STRING, DataType.STRING, DataType.LONG});
            List<Object[]> results = rawResults.getResults();
            List<PersonNameDto> personNames = new ArrayList<>();
            for (Object[] result : results) {
                PersonNameDto personName = new PersonNameDto((String) result[0], (String) result[1], (Long) result[2]);
                personNames.add(personName);
            }

            return personNames;
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getPersonNameDtos on Person");
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

        return date;
    }
}
