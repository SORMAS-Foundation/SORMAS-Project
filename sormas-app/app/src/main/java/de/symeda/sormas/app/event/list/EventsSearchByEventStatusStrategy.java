package de.symeda.sormas.app.event.list;

import java.util.List;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;

/**
 * Created by Orson on 07/12/2017.
 */
public class EventsSearchByEventStatusStrategy implements IEventsSearchStrategy {

    private EventStatus status = null;

    public EventsSearchByEventStatusStrategy(EventStatus status) {
        this.status= status;
    }

    @Override
    public List<Event> search() {
        List<Event> list;

        //TODO: Make changes here
        if (status == null) {
            list = DatabaseHelper.getEventDao().queryForAll(Event.EVENT_DATE, false);
        } else {
            list = DatabaseHelper.getEventDao().queryForEq(Event.EVENT_STATUS, status, Event.EVENT_DATE, false);
        }

        return list;
    }
}
