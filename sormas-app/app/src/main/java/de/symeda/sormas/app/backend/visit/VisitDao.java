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

package de.symeda.sormas.app.backend.visit;

import android.location.Location;
import androidx.annotation.NonNull;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.util.LocationService;

public class VisitDao extends AbstractAdoDao<Visit> {

    public VisitDao(Dao<Visit,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<Visit> getAdoClass() {
        return Visit.class;
    }

    @Override
    public String getTableName() {
        return Visit.TABLE_NAME;
    }

    public List<Visit> getByContact(Contact contact) {
        if (contact.isSnapshot()) {
            throw new IllegalArgumentException("Does not support snapshot entities");
        }

        try {
            QueryBuilder qb = queryBuilder();
            Where where = qb.where();
            filterByStatusAndContact(where, null, contact);

            qb.orderBy(Visit.VISIT_DATE_TIME, true);

            return qb.query();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getByContact on Visit");
            throw new RuntimeException(e);
        }
    }

    public int getVisitCount(Contact contact, VisitStatus visitStatus) {
        if (contact.isSnapshot()) {
            throw new IllegalArgumentException("Does not support snapshot entities");
        }

        try {
            QueryBuilder qb = queryBuilder();
            Where where = qb.where();

            filterByStatusAndContact(where, visitStatus, contact);

            return (int) qb.countOf();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getVisitCount on Visit");
            throw new RuntimeException(e);
        }
    }

    private void filterByStatusAndContact(Where where, VisitStatus visitStatus, Contact contact) throws SQLException {
        where.and(
                where.eq(AbstractDomainObject.SNAPSHOT, false),
                where.eq(Visit.PERSON + "_id", contact.getPerson()),
                where.eq(Visit.DISEASE, contact.getDisease())
        );

        if (visitStatus != null) {
            where.and();
            where.eq(Visit.VISIT_STATUS, visitStatus);
        }

        Date contactStartDate = ContactLogic.getStartDate(contact.getLastContactDate(), contact.getReportDateTime());
        Date contactEndDate = ContactLogic.getEndDate(contact.getLastContactDate(), contact.getReportDateTime(), contact.getFollowUpUntil());
        Date lowerLimit = DateHelper.subtractDays(contactStartDate, VisitDto.ALLOWED_CONTACT_DATE_OFFSET);
        if (lowerLimit != null) {
            where.and();
            where.ge(Visit.VISIT_DATE_TIME, lowerLimit);
        }

        Date upperLimit = DateHelper.addDays(contactEndDate, VisitDto.ALLOWED_CONTACT_DATE_OFFSET);
        if (upperLimit != null) {
            where.and();
            where.le(Visit.VISIT_DATE_TIME, upperLimit);
        }
    }

    @Override
    public Date getLatestChangeDate() {
        Date date = super.getLatestChangeDate();
        if (date == null) {
            return null;
        }

        Date symptomsDate = getLatestChangeDateJoin(Symptoms.TABLE_NAME, Visit.SYMPTOMS);
        if (symptomsDate != null && symptomsDate.after(date)) {
            date = symptomsDate;
        }

        return date;
    }

    @Override
    public Visit build() {
        throw new UnsupportedOperationException();
    }

    @NonNull
    public Visit build(String contactUuid) {
        Contact contact = DatabaseHelper.getContactDao().queryUuid(contactUuid);
        Visit visit = super.build();
        visit.setSymptoms(DatabaseHelper.getSymptomsDao().build());
        visit.setPerson(contact.getPerson());
        visit.setDisease(contact.getDisease());
        visit.setVisitDateTime(new Date());
        visit.setVisitUser(ConfigProvider.getUser());
        return visit;
    }

    @Override
    public Visit saveAndSnapshot(final Visit visit) throws DaoException {
        // If a new visit is created, use the last available location to update its report latitude and longitude
        if (visit.getId() == null) {
            Location location = LocationService.instance().getLocation();
            if (location != null) {
                visit.setReportLat(location.getLatitude());
                visit.setReportLon(location.getLongitude());
                visit.setReportLatLonAccuracy(location.getAccuracy());
            }
        }

        return super.saveAndSnapshot(visit);
    }

}
