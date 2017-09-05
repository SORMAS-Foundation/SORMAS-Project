package de.symeda.sormas.app.backend.contact;

import android.location.Location;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.util.LocationService;

/**
 * Created by Stefan Szczesny on 29.11.2016.
 */
public class ContactDao extends AbstractAdoDao<Contact> {

    public ContactDao(Dao<Contact,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<Contact> getAdoClass() {
        return Contact.class;
    }

    @Override
    public String getTableName() {
        return Contact.TABLE_NAME;
    }

    public List<Contact> getByCase(Case caze) {

        if (caze.isSnapshot()) {
            throw new IllegalArgumentException("Does not support snapshot entities");
        }

        try {
            QueryBuilder qb = queryBuilder();
            qb.where().eq(Contact.CAZE + "_id", caze)
                    .and().eq(AbstractDomainObject.SNAPSHOT, false);
            qb.orderBy(Contact.LAST_CONTACT_DATE, false);
            return qb.query();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getByCase on Contact");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Contact build() {

        Contact contact = super.build();

        contact.setReportDateTime(new Date());
        contact.setReportingUser(ConfigProvider.getUser());

        return contact;
    }

    @Override
    public void markAsRead(Contact contact) {
        super.markAsRead(contact);
        DatabaseHelper.getPersonDao().markAsRead(contact.getPerson());
    }

    @Override
    public Contact saveAndSnapshot(final Contact contact) throws DaoException {
        // If a new contact is created, use the last available location to update its report latitude and longitude
        if (contact.getId() == null) {
            LocationService locationService = LocationService.getLocationService(DatabaseHelper.getContext());
            Location location = locationService.getLocation();
            if (location != null) {
                // Use the geo-coordinates of the current location object if it's not older than 15 minutes
                if (new Date().getTime() <= location.getTime() + (1000 * 60 * 15)) {
                    contact.setReportLat((float) location.getLatitude());
                    contact.setReportLon((float) location.getLongitude());
                }
            }
        }

        return super.saveAndSnapshot(contact);
    }
}
