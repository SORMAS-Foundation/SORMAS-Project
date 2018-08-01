package de.symeda.sormas.app.core.enumeration;

import android.content.Context;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.R;

public class TaskStatusElaborator implements StatusElaborator {

    private TaskStatus status = null;

    public TaskStatusElaborator(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String getFriendlyName(Context context) {
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
    public Enum getValue() {
        return this.status;
    }

    @Override
    public int getIconResourceId() {
        switch (status) {

            case PENDING:
                return R.drawable.ic_lp_pending_task_192dp;
            case DONE:
                return R.drawable.ic_lp_done_task_192dp;
            case REMOVED:
            case NOT_EXECUTABLE:
                return R.drawable.ic_lp_not_exec_task_192dp;
            default:
                throw new IllegalArgumentException(status.toString());
        }
    }
}
