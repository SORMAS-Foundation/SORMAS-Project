package de.symeda.sormas.app.searchstrategy;

import java.util.List;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;

/**
 * Created by Orson on 03/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class EventSearchByStatusStrategy implements ISearchStrategy<Event> {

    private EventStatus status;

    public EventSearchByStatusStrategy(EventStatus status) {
        this.status = status;
    }

    @Override
    public List<Event> search() {
        List<Event> result;

        if (status != null) {
            result = DatabaseHelper.getEventDao().queryForEq(Event.EVENT_STATUS, status, Event.EVENT_DATE, false);
        } else {
            result = DatabaseHelper.getEventDao().queryForAll(Event.EVENT_DATE, false);
        }

        return result;
    }

}