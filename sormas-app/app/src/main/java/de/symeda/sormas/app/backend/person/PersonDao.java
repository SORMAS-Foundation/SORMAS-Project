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
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.person.PersonNameDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.util.LocationService;

import static de.symeda.sormas.api.i18n.Strings.and;

public class PersonDao extends AbstractAdoDao<Person> {

    public PersonDao(Dao<Person,Long> innerDao) {
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

    public List<PersonNameDto> getRelevantPersonNames(PersonSimilarityCriteria similarityCriteria) {
        try {
            QueryBuilder<Person, Long> builder = queryBuilder();
            Where<Person, Long> where = builder.where();
            where.eq(AbstractDomainObject.SNAPSHOT, false);

            if (similarityCriteria.getSex() != null) {
                where.and();
                where.or(
                        where.isNull(Person.SEX),
                        where.eq(Person.SEX, similarityCriteria.getSex())
                );
            }
            if (similarityCriteria.getBirthdateYYYY() != null) {
                where.and();
                where.or(
                        where.isNull(Person.BIRTHDATE_YYYY),
                        where.eq(Person.BIRTHDATE_YYYY, similarityCriteria.getBirthdateYYYY())
                );
            }
            if (similarityCriteria.getBirthdateMM() != null) {
                where.and();
                where.or(
                        where.isNull(Person.BIRTHDATE_MM),
                        where.eq(Person.BIRTHDATE_MM, similarityCriteria.getBirthdateMM())
                );
            }
            if (similarityCriteria.getBirthdateDD() != null) {
                where.and();
                where.or(
                        where.isNull(Person.BIRTHDATE_DD),
                        where.eq(Person.BIRTHDATE_DD, similarityCriteria.getBirthdateDD())
                );
            }

            builder.selectColumns(Person.FIRST_NAME, Person.LAST_NAME, Person.UUID);

            return builder.orderBy(Person.LAST_NAME, true)
                    .orderBy(Person.LAST_NAME, true)
                    .orderBy(Person.UUID, true)
                    .query()
                    .stream()
                    .map(person -> new PersonNameDto(person.getFirstName(), person.getLastName(), person.getUuid()))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getRelevantPersonNames on Person");
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

    @Override
    public Person saveAndSnapshot(final Person person) throws DaoException {

        final Person existingPerson = queryUuid(person.getUuid());
        onPersonChanged(existingPerson, person);
        return super.saveAndSnapshot(person);
    }

    private void onPersonChanged(Person existingPerson, Person changedPerson) {

        // approximate age reference date
        if (existingPerson == null
                || !DataHelper.equal(changedPerson.getApproximateAge(), existingPerson.getApproximateAge())
                || !DataHelper.equal(changedPerson.getApproximateAgeType(), existingPerson.getApproximateAgeType())) {
            if (changedPerson.getApproximateAge() == null) {
                changedPerson.setApproximateAgeReferenceDate(null);
            } else {
                changedPerson.setApproximateAgeReferenceDate(changedPerson.getDeathDate() != null ? changedPerson.getDeathDate() : new Date());
            }
        }
    }
}
