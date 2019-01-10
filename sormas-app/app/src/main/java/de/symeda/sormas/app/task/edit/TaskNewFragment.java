/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.task.edit;

import android.view.View;

import java.util.List;

import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.databinding.FragmentTaskEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

public class TaskNewFragment extends BaseEditFragment<FragmentTaskEditLayoutBinding, Task, Task> {

    public static final String TAG = TaskNewFragment.class.getSimpleName();

    private Task record;

    private List<Item> taskTypeList;
    private List<Item> priorityList;
    private List<Item> assigneeList;

    public static TaskNewFragment newInstance(Task activityRootData) {
        return newInstance(TaskNewFragment.class, TaskNewActivity.buildBundle().get(), activityRootData);
    }

    public static TaskNewFragment newInstanceFromCase(Task activityRootData, String caseUuid) {
        return newInstance(TaskNewFragment.class, TaskNewActivity.buildBundleWithCase(caseUuid).get(), activityRootData);
    }

    public static TaskNewFragment newInstanceFromContact(Task activityRootData, String contactUuid) {
        return newInstance(TaskNewFragment.class, TaskNewActivity.buildBundleWithContact(contactUuid).get(), activityRootData);
    }

    public static TaskNewFragment newInstanceFromEvent(Task activityRootData, String eventUuid) {
        return newInstance(TaskNewFragment.class, TaskNewActivity.buildBundleWithEvent(eventUuid).get(), activityRootData);
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_new_task);
    }

    @Override
    public Task getPrimaryData() { return record; }

    @Override
    protected void prepareFragmentData() {
        record = getActivityRootData();

        taskTypeList = DataUtils.getEnumItems(TaskType.class, true);
        priorityList = DataUtils.getEnumItems(TaskPriority.class, true);
        assigneeList = DataUtils.toItems(DatabaseHelper.getUserDao().queryForAll(), true);
    }

    @Override
    public void onLayoutBinding(FragmentTaskEditLayoutBinding contentBinding) {
        contentBinding.setData(record);

        // Initialize ControlSpinnerFields
        contentBinding.taskTaskType.initializeSpinner(taskTypeList);
        contentBinding.taskPriority.initializeSpinner(priorityList);
        contentBinding.taskAssigneeUser.initializeSpinner(assigneeList);

        // Initialize ControlDateFields and ControlDateTimeFields
        contentBinding.taskSuggestedStart.initializeDateTimeField(getFragmentManager());
        contentBinding.taskDueDate.initializeDateTimeField(getFragmentManager());
    }

    @Override
    public void onAfterLayoutBinding(final FragmentTaskEditLayoutBinding contentBinding) {

    }

    @Override
    public int getEditLayout() { return R.layout.fragment_task_edit_layout; }

}
