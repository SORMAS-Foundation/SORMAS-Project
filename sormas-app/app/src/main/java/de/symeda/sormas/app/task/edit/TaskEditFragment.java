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
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.contact.read.ContactReadActivity;
import de.symeda.sormas.app.databinding.FragmentTaskEditLayoutBinding;
import de.symeda.sormas.app.event.read.EventReadActivity;
import de.symeda.sormas.app.util.DataUtils;

import static android.view.View.GONE;

public class TaskEditFragment extends BaseEditFragment<FragmentTaskEditLayoutBinding, Task, Task> {

    private Task record;

    private List<Item> taskTypeList;
    private List<Item> priorityList;
    private List<Item> assigneeList;

//    public static TaskEditFragment newInstance(Task activityRootData) {
//        return newInstance(TaskEditFragment.class, null, activityRootData);
//    }

    public static TaskEditFragment newInstance(Task activityRootData) {
        return newInstance(TaskEditFragment.class, activityRootData.isNew() ? TaskNewActivity.buildBundle().get() : null, activityRootData);
    }

    public static TaskEditFragment newInstanceFromCase(Task activityRootData, String caseUuid) {
        return newInstance(TaskEditFragment.class, TaskNewActivity.buildBundleWithCase(caseUuid).get(), activityRootData);
    }

    public static TaskEditFragment newInstanceFromContact(Task activityRootData, String contactUuid) {
        return newInstance(TaskEditFragment.class, TaskNewActivity.buildBundleWithContact(contactUuid).get(), activityRootData);
    }

    public static TaskEditFragment newInstanceFromEvent(Task activityRootData, String eventUuid) {
        return newInstance(TaskEditFragment.class, TaskNewActivity.buildBundleWithEvent(eventUuid).get(), activityRootData);
    }

    private void setUpControlListeners(FragmentTaskEditLayoutBinding contentBinding) {
        contentBinding.setDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record.setTaskStatus(TaskStatus.DONE);
                getBaseEditActivity().saveData();
            }
        });

        contentBinding.setNotExecutable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record.setTaskStatus(TaskStatus.NOT_EXECUTABLE);
                getBaseEditActivity().saveData();
            }
        });

        if (record.getCaze() != null) {
            contentBinding.taskCaze.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Case caze = record.getCaze();
                    if (caze != null) {
                        CaseReadActivity.startActivity(getActivity(), caze.getUuid(), true);
                    }
                }
            });
        }

        if (record.getContact() != null) {
            contentBinding.taskContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Contact contact = record.getContact();
                    if (contact != null) {
                        ContactReadActivity.startActivity(getActivity(), contact.getUuid(), true);
                    }
                }
            });
        }

        if (record.getEvent() != null) {
            contentBinding.taskEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Event event = record.getEvent();
                    if (event != null) {
                        EventReadActivity.startActivity(getActivity(), event.getUuid(), true);
                    }
                }
            });
        }
    }

    // Overrides

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_task_information);
    }

    @Override
    public Task getPrimaryData() {
        return record;
    }

    @Override
    public boolean isShowSaveAction() {
        return true;
    }

    @Override
    protected void prepareFragmentData() {
        record = getActivityRootData();

        taskTypeList = DataUtils.toItems(TaskType.getTaskTypes(record.getTaskContext()), true);
        priorityList = DataUtils.getEnumItems(TaskPriority.class, true);
        assigneeList = DataUtils.toItems(DatabaseHelper.getUserDao().queryForAll(), true);
    }

    @Override
    public void onLayoutBinding(FragmentTaskEditLayoutBinding contentBinding) {
        setUpControlListeners(contentBinding);

        // Saving and editing the assignee reply is only allowed when the task is assigned to the user;
        // Additionally, the save option is hidden for pending tasks because those should be saved
        // by clicking on the "Done" and "Not executable" buttons
        if (!ConfigProvider.getUser().equals(record.getAssigneeUser())) {
            contentBinding.taskAssigneeReply.setEnabled(false);
            contentBinding.taskButtonPanel.setVisibility(GONE);
        } else {
            if (record.getTaskStatus() != TaskStatus.PENDING) {
                getBaseEditActivity().getSaveMenu().setVisible(true);
                contentBinding.taskButtonPanel.setVisibility(GONE);
            }
        }

        if (!ConfigProvider.getUser().equals(record.getCreatorUser())) {
            contentBinding.taskTaskType.setEnabled(false);
            contentBinding.taskSuggestedStart.setEnabled(false);
            contentBinding.taskDueDate.setEnabled(false);
            contentBinding.taskAssigneeUser.setEnabled(false);
            contentBinding.taskPriority.setEnabled(false);
            contentBinding.taskCreatorComment.setEnabled(false);
        }

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
    public int getEditLayout() {
        return R.layout.fragment_task_edit_layout;
    }
}
