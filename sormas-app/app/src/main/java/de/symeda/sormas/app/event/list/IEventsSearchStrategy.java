package de.symeda.sormas.app.event.list;

import java.util.List;

import de.symeda.sormas.app.backend.event.Event;

/**
 * Created by Orson on 07/12/2017.
 */
public interface IEventsSearchStrategy {
    List<Event> search();

}
