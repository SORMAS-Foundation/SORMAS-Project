package de.symeda.sormas.app.core.enumeration;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.R;

public class TaskStatusElaborator implements IStatusElaborator {

    private TaskStatus status = null;

    public TaskStatusElaborator(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String getFriendlyName() {
        if (status != null) {
            return status.toString();
        }
        return "";
    }

    @Override
    public int getColorIndicatorResource() {
        if (status == TaskStatus.PENDING) {
            return R.color.indicatorTaskPending;
        } else if (status == TaskStatus.DONE) {
            return R.color.indicatorTaskDone;
        } else if (status == TaskStatus.REMOVED) {
            return R.color.indicatorTaskRemoved;
        } else if (status == TaskStatus.NOT_EXECUTABLE) {
            return R.color.indicatorTaskNotExecutable;
        }

        return R.color.noColor;
    }

    @Override
    public String getStatekey() {
        return ARG_TASK_STATUS;
    }

    @Override
    public Enum getValue() {
        return this.status;
    }
}
