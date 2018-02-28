package de.symeda.sormas.app.task.landing;

import android.content.Context;

import de.symeda.sormas.app.core.BaseLandingToListNavigationCapsule;
import de.symeda.sormas.app.core.SearchStrategy;

import de.symeda.sormas.api.task.TaskStatus;

/**
 * Created by Orson on 09/01/2018.
 */

public class TaskLandingToListCapsule extends BaseLandingToListNavigationCapsule {

    public TaskLandingToListCapsule(Context context, TaskStatus filterStatus, SearchStrategy searchStrategy) {
        super(context, filterStatus, searchStrategy);
    }
}