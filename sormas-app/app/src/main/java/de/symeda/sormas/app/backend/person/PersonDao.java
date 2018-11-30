/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
                    ", " + Person.LAST_NAME + ", " + Person.UUID + " from " +
                    Person.TABLE_NAME + " where " + Person.TABLE_NAME + "." + AbstractDomainObject.SNAPSHOT + " = 0;",
                    new DataType[]{DataType.STRING, DataType.STRING, DataType.STRING});
            List<Object[]> results = rawResults.getResults();
            List<PersonNameDto> personNames = new ArrayList<>();
            for (Object[] result : results) {
                PersonNameDto personName = new PersonNameDto((String) result[0], (String) result[1], (String) result[2]);
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
