package de.symeda.sormas.app.event.list;

import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.event.EventStatus;
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
        List<Event> list = new ArrayList<>();

        //TODO: Make changes here
        if (status == null) {
            //return DatabaseHelper.getContactDao().queryForAll(Contact.REPORT_DATE_TIME, false);
            return new ArrayList<>();
        }

        if (status == EventStatus.POSSIBLE) {
            list = MemoryDatabaseHelper.EVENT.getPossibleEvents(20);
        } else if (status == EventStatus.CONFIRMED) {
            list = MemoryDatabaseHelper.EVENT.getConfirmedEvents(20);
        } else if (status == EventStatus.NO_EVENT) {
            list = MemoryDatabaseHelper.EVENT.getNoEvents(20);
        }
        return list;
    }
}
