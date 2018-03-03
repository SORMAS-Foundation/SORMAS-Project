package de.symeda.sormas.app.task.list;

import android.content.Context;

import de.symeda.sormas.app.core.BaseListNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;

import de.symeda.sormas.api.task.TaskStatus;

/**
 * Created by Orson on 09/01/2018.
 */

public class TaskListCapsule extends BaseListNavigationCapsule {

    public TaskListCapsule(Context context, TaskStatus filterStatus, SearchBy searchBy) {
        super(context, filterStatus, searchBy);
    }
}