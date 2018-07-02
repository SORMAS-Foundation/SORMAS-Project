package de.symeda.sormas.app.shared;

import android.content.Context;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.core.BaseFormNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;

import de.symeda.sormas.app.backend.task.Task;

public class TaskFormNavigationCapsule extends BaseFormNavigationCapsule<Task, TaskFormNavigationCapsule> {

    public TaskFormNavigationCapsule(Context context, String recordUuid, TaskStatus pageStatus) {
        super(context, recordUuid, null, pageStatus);
    }
}