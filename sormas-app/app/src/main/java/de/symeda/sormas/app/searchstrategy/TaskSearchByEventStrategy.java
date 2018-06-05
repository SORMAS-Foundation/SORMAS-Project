package de.symeda.sormas.app.searchstrategy;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventDao;
import de.symeda.sormas.app.backend.task.Task;

/**
 * Created by Orson on 03/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TaskSearchByEventStrategy implements ISearchStrategy<Task> {
    private String recordId;

    public TaskSearchByEventStrategy(String recordId) {
        this.recordId = recordId;
    }

    @Override
    public List<Task> search() {
        List<Task> result = new ArrayList<>();

        if (recordId == null || recordId.isEmpty())
            return result;

        EventDao eventDao = DatabaseHelper.getEventDao();
        Event event = eventDao.queryUuid(recordId);

        if (event != null) {
            result = DatabaseHelper.getTaskDao().queryByEvent(event);
        }

        return result;
    }
}
