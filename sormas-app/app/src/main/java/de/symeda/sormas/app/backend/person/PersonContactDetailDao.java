/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import com.j256.ormlite.dao.Dao;

import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;

public class PersonContactDetailDao extends AbstractAdoDao<PersonContactDetail> {

    public PersonContactDetailDao(Dao<PersonContactDetail, Long> innerDao) {
        super(innerDao);
    }

    public List<PersonContactDetail> getByPerson(Person person) {
        if (person.isSnapshot()) {
            return querySnapshotsForEq(PersonContactDetail.PERSON + "_id", person, PersonContactDetail.CHANGE_DATE, false);
        }
        return queryForEq(PersonContactDetail.PERSON + "_id", person, PersonContactDetail.CHANGE_DATE, false);
    }

    @Override
    protected Class<PersonContactDetail> getAdoClass() {
        return PersonContactDetail.class;
    }

    @Override
    public String getTableName() {
        return PersonContactDetail.TABLE_NAME;
    }
}
