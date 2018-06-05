package de.symeda.sormas.app.core.enumeration;

import android.content.res.Resources;

import de.symeda.sormas.app.R;

import de.symeda.sormas.api.task.TaskStatus;

/**
 * Created by Orson on 25/12/2017.
 */
public class TaskStatusElaborator implements IStatusElaborator {

    private Resources resources = null;
    private TaskStatus status = null;

    public TaskStatusElaborator(TaskStatus status, Resources resources) {
        this.status = status;
        this.resources = resources;
    }

    @Override
    public String getFriendlyName() {
        if (status == TaskStatus.PENDING) {
            return resources.getString(R.string.status_task_pending);
        } else if (status == TaskStatus.DONE) {
            return resources.getString(R.string.status_task_done);
        } else if (status == TaskStatus.REMOVED) {
            return resources.getString(R.string.status_task_removed);
        } else if (status == TaskStatus.NOT_EXECUTABLE) {
            return resources.getString(R.string.status_task_not_executable);
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
