package de.symeda.sormas.app.task.landing;

import android.content.Context;

import de.symeda.sormas.app.core.BaseLandingToListNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;

import de.symeda.sormas.api.task.TaskStatus;

/**
 * Created by Orson on 09/01/2018.
 */

public class TaskLandingToListCapsule extends BaseLandingToListNavigationCapsule {

    public TaskLandingToListCapsule(Context context, TaskStatus filterStatus, SearchBy searchBy) {
        super(context, filterStatus, searchBy);
    }
}