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
import de.symeda.sormas.app.util.DataUtils;

public class EventDao extends AbstractAdoDao<Event> {

    public EventDao(Dao<Event,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public String getTableName() {
        return Event.TABLE_NAME;
    }

    @Override
    public boolean save(Event event) throws DaoException {

        if (event.getEventLocation() != null) {
            DatabaseHelper.getLocationDao().save(event.getEventLocation());
        }

        return super.save(event);
    }

    @Override
    public boolean saveUnmodified(Event event) throws DaoException {

        if (event.getEventLocation() != null) {
            DatabaseHelper.getLocationDao().save(event.getEventLocation());
        }

        return super.saveUnmodified(event);
    }

    public Event getNewEvent() {

        Event event = DataUtils.createNew(Event.class);
        event.setReportDateTime(new Date());
        event.setReportingUser(ConfigProvider.getUser());

        Location location = DataUtils.createNew(Location.class);
        location.setRegion(ConfigProvider.getUser().getRegion());
        event.setEventLocation(location);

        event.setEventStatus(EventStatus.POSSIBLE);

        return event;
    }


}
