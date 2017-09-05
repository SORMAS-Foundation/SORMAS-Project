package de.symeda.sormas.app.backend.visit;

import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.util.LocationService;
//import kotlin.NotImplementedError;

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
            where.and(
                    where.eq(AbstractDomainObject.SNAPSHOT, false),
                    where.eq(Visit.PERSON + "_id", contact.getPerson()),
                    where.eq(Visit.DISEASE, contact.getCaze().getDisease())
            );
            // see sormas-backend/VisitService.getAllByContact()
            Date lowerLimit = contact.getLastContactDate() != null ? DateHelper.subtractDays(contact.getLastContactDate(), 10) : contact.getReportDateTime();
            if (lowerLimit != null) {
                where.and();
                where.gt(Visit.VISIT_DATE_TIME, lowerLimit);
            }

            Date upperLimit = DateHelper.addDays(contact.getFollowUpUntil(), 10);
            if (upperLimit != null) {
                where.and();
                where.lt(Visit.VISIT_DATE_TIME, upperLimit);
            }

            qb.orderBy(Visit.VISIT_DATE_TIME, true);

            return qb.query();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getByContact on Visit");
            throw new RuntimeException(e);
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

    /**
     * new visit is assigned to the contact's person and has the same disease as the contact's case
     * @param contactUuid
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @NonNull
    public Visit build(String contactUuid) {
        Contact contact = DatabaseHelper.getContactDao().queryUuid(contactUuid);
        Visit visit = super.build();
        visit.setPerson(contact.getPerson());
        visit.setDisease(contact.getCaze().getDisease());
        visit.setVisitDateTime(new Date());
        return visit;
    }

    @Override
    public Visit saveAndSnapshot(final Visit visit) throws DaoException {
        // If a new visit is created, use the last available location to update its report latitude and longitude
        if (visit.getId() == null) {
            LocationService locationService = LocationService.getLocationService(DatabaseHelper.getContext());
            Location location = locationService.getLocation();
            if (location != null) {
                // Use the geo-coordinates of the current location object if it's not older than 15 minutes
                if (new Date().getTime() <= location.getTime() + (1000 * 60 * 15)) {
                    visit.setReportLat((float) location.getLatitude());
                    visit.setReportLon((float) location.getLongitude());
                }
            }
        }

        return super.saveAndSnapshot(visit);
    }

}
