package de.symeda.sormas.app.searchstrategy;

import java.util.List;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.task.Task;

/**
 * Created by Orson on 03/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TaskSearchByStatusStrategy implements ISearchStrategy<Task> {

    private TaskStatus status;

    public TaskSearchByStatusStrategy(TaskStatus status) {
        this.status = status;
    }

    @Override
    public List<Task> search() {
        List<Task> result;

        if (status == TaskStatus.PENDING) {
            result = DatabaseHelper.getTaskDao().queryMyPending();
        } else if (status == TaskStatus.NOT_EXECUTABLE) {
            result = DatabaseHelper.getTaskDao().queryMyNotExecutable();
        } else {
            result = DatabaseHelper.getTaskDao().queryMyDoneOrRemoved();
        }

        return result;
    }
}
