/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.task.edit;

import static android.view.View.GONE;

import java.util.List;

import android.view.View;

import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.databinding.FragmentTaskEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

public class TaskEditFragment extends BaseEditFragment<FragmentTaskEditLayoutBinding, Task, Task> {

	private Task record;

	private List<Item> taskTypeList;
	private List<Item> priorityList;
	private List<Item> assigneeList;

	public static TaskEditFragment newInstance(Task activityRootData) {
		return newInstance(TaskEditFragment.class, activityRootData.getId() == null ? TaskNewActivity.buildBundle().get() : null, activityRootData);
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
		return record != null && ConfigProvider.getUser().equals(record.getCreatorUser());
	}

	@Override
	protected void prepareFragmentData() {
		record = getActivityRootData();

		taskTypeList = DataUtils.toItems(TaskType.getTaskTypes(record.getTaskContext()), true);
		priorityList = DataUtils.getEnumItems(TaskPriority.class, true);
		assigneeList = DataUtils.toItems(DatabaseHelper.getUserDao().queryForAll(), true);
	}

	@Override
	public void onLayoutBinding(final FragmentTaskEditLayoutBinding contentBinding) {
		setUpControlListeners(contentBinding);

		// Initialize ControlSpinnerFields
		contentBinding.taskTaskType.initializeSpinner(taskTypeList);
		contentBinding.taskPriority.initializeSpinner(priorityList);
		contentBinding.taskAssigneeUser.initializeSpinner(assigneeList);

		// Initialize ControlDateFields and ControlDateTimeFields
		contentBinding.taskSuggestedStart.initializeDateTimeField(getFragmentManager());
		contentBinding.taskDueDate.initializeDateTimeField(getFragmentManager());

		//creatorComment should be required when task type is OTHER
		contentBinding.taskTaskType.addValueChangedListener(new ValueChangeListener() {

			@Override
			public void onChange(ControlPropertyField field) {
				contentBinding.taskCreatorComment.setRequired(field.getValue() == TaskType.OTHER);
			}
		});

		contentBinding.setData(record);
	}

	@Override
	protected void onAfterLayoutBinding(FragmentTaskEditLayoutBinding contentBinding) {
		super.onAfterLayoutBinding(contentBinding);

		if (record.getId() == null) {
			contentBinding.taskAssigneeReply.setVisibility(GONE);
		}

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
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_task_edit_layout;
	}
}
