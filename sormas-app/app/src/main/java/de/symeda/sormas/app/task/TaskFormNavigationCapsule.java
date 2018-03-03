package de.symeda.sormas.app.task;

import android.content.Context;

import de.symeda.sormas.app.core.BaseFormNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;

import de.symeda.sormas.app.backend.task.Task;

/**
 * Created by Orson on 09/01/2018.
 */

public class TaskFormNavigationCapsule extends BaseFormNavigationCapsule<Task> {

    private IStatusElaborator filterStatus;
    private SearchBy searchBy;

    public TaskFormNavigationCapsule(Context context, String recordUuid, Enum pageStatus) {
        super(context, recordUuid, null, pageStatus);
    }
}