package de.symeda.sormas.app.backend.event;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Date;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.util.LocationService;

public class EventDao extends AbstractAdoDao<Event> {

    public EventDao(Dao<Event,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<Event> getAdoClass() {
        return Event.class;
    }

    @Override
    public String getTableName() {
        return Event.TABLE_NAME;
    }

    @Override
    public Date getLatestChangeDate() {
        Date date = super.getLatestChangeDate();
        if (date == null) {
            return null;
        }

        Date locationDate = getLatestChangeDateJoin(Location.TABLE_NAME, Event.EVENT_LOCATION);
        if (locationDate != null && locationDate.after(date)) {
            date = locationDate;
        }

        return date;
    }

    @Override
    public Event build() {

        Event event = super.build();

        event.setReportDateTime(new Date());
        event.setReportingUser(ConfigProvider.getUser());
        event.getEventLocation().setRegion(ConfigProvider.getUser().getRegion());
        event.setEventStatus(EventStatus.POSSIBLE);

        return event;
    }

    @Override
    public Event saveAndSnapshot(final Event event) throws DaoException {
        // If a new event is created, use the last available location to update its report latitude and longitude
        if (event.getId() == null) {
            android.location.Location location = LocationService.instance().getLocation();
            if (location != null) {
                event.setReportLat((float) location.getLatitude());
                event.setReportLon((float) location.getLongitude());
            }
        }

        return super.saveAndSnapshot(event);
    }

}
