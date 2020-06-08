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

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.util.Bundler;

public class TaskNewActivity extends BaseEditActivity<Task> {

	public static final String TAG = TaskNewActivity.class.getSimpleName();

	private AsyncTask saveTask;

	private String caseUuid;
	private String contactUuid;
	private String eventUuid;

	public static void startActivity(Context fromActivity) {
		BaseEditActivity.startActivity(fromActivity, TaskNewActivity.class, buildBundle());
	}

	public static void startActivityFromCase(Context fromActivity, String caseUuid) {
		BaseEditActivity.startActivity(fromActivity, TaskNewActivity.class, buildBundleWithCase(caseUuid));
	}

	public static void startActivityFromContact(Context fromActivity, String contactUuid) {
		BaseEditActivity.startActivity(fromActivity, TaskNewActivity.class, buildBundleWithContact(contactUuid));
	}

	public static void startActivityFromEvent(Context fromActivity, String eventUuid) {
		BaseEditActivity.startActivity(fromActivity, TaskNewActivity.class, buildBundleWithEvent(eventUuid));
	}

	public static Bundler buildBundle() {
		return BaseEditActivity.buildBundle(null);
	}

	public static Bundler buildBundleWithCase(String caseUuid) {
		return BaseEditActivity.buildBundle(null).setCaseUuid(caseUuid);
	}

	public static Bundler buildBundleWithContact(String contactUuid) {
		return BaseEditActivity.buildBundle(null).setContactUuid(contactUuid);
	}

	public static Bundler buildBundleWithEvent(String eventUuid) {
		return BaseEditActivity.buildBundle(null).setEventUuid(eventUuid);
	}

	@Override
	protected void onCreateInner(Bundle savedInstanceState) {
		super.onCreateInner(savedInstanceState);
		Bundler bundler = new Bundler(savedInstanceState);
		caseUuid = bundler.getCaseUuid();
		contactUuid = bundler.getContactUuid();
		eventUuid = bundler.getEventUuid();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		Bundler bundler = new Bundler(outState);
		bundler.setCaseUuid(caseUuid);
		bundler.setContactUuid(contactUuid);
		bundler.setEventUuid(eventUuid);
	}

	@Override
	public TaskStatus getPageStatus() {
		return null;
	}

	@Override
	protected Task queryRootEntity(String recordUuid) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Task buildRootEntity() {
		Task _task;

		if (!DataHelper.isNullOrEmpty(caseUuid)) {
			Case _case = DatabaseHelper.getCaseDao().queryUuidBasic(caseUuid);
			_task = DatabaseHelper.getTaskDao().build(_case);
		} else if (!DataHelper.isNullOrEmpty(contactUuid)) {
			Contact _contact = DatabaseHelper.getContactDao().queryUuid(contactUuid);
			_task = DatabaseHelper.getTaskDao().build(_contact);
		} else if (!DataHelper.isNullOrEmpty(eventUuid)) {
			Event _event = DatabaseHelper.getEventDao().queryUuid(eventUuid);
			_task = DatabaseHelper.getTaskDao().build(_event);
		} else {
			_task = DatabaseHelper.getTaskDao().build();
		}

		return _task;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		getSaveMenu().setTitle(R.string.action_save_task);
		return result;
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Task activityRootData) {
		BaseEditFragment fragment;

		if (caseUuid != null) {
			fragment = TaskEditFragment.newInstanceFromCase(activityRootData, caseUuid);
		} else if (contactUuid != null) {
			fragment = TaskEditFragment.newInstanceFromContact(activityRootData, contactUuid);
		} else if (eventUuid != null) {
			fragment = TaskEditFragment.newInstanceFromEvent(activityRootData, eventUuid);
		} else {
			fragment = TaskEditFragment.newInstance(activityRootData);
		}

		fragment.setLiveValidationDisabled(true);
		return fragment;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.caption_new_task;
	}

	@Override
	public void replaceFragment(BaseEditFragment f, boolean allowBackNavigation) {
		super.replaceFragment(f, allowBackNavigation);
		getActiveFragment().setLiveValidationDisabled(true);
	}

	@Override
	public void saveData() {

		if (saveTask != null) {
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
			return; // don't save multiple times
		}

		final Task taskToSave = getStoredRootEntity();
		TaskEditFragment fragment = (TaskEditFragment) getActiveFragment();

		fragment.setLiveValidationDisabled(false);

		try {
			FragmentValidator.validate(getContext(), fragment.getContentBinding());
		} catch (ValidationException e) {
			NotificationHelper.showNotification(this, ERROR, e.getMessage());
			return;
		}

		saveDataInner(taskToSave);
	}

	private void saveDataInner(final Task taskToSave) {

		if (saveTask != null) {
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
			return; // don't save multiple times
		}

		saveTask = new SavingAsyncTask(getRootView(), taskToSave) {

			@Override
			protected void onPreExecute() {
				showPreloader();
			}

			@Override
			public void doInBackground(TaskResultHolder resultHolder) throws DaoException, ValidationException {
				validateData(taskToSave);
				DatabaseHelper.getTaskDao().saveAndSnapshot(taskToSave);
			}

			@Override
			protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
				hidePreloader();
				super.onPostExecute(taskResult);
				if (taskResult.getResultStatus().isSuccess()) {
					finish();
					TaskEditActivity.startActivity(getContext(), taskToSave.getUuid());
				}
				saveTask = null;
			}
		}.executeOnThreadPool();
	}

	private void validateData(Task data) throws ValidationException {
		if (data.getTaskStatus() == TaskStatus.NOT_EXECUTABLE && DataHelper.isNullOrEmpty(data.getAssigneeReply())) {
			// TODO I18n: Replace with text from I18nProperties?
			throw new ValidationException(getContext().getResources().getString(R.string.message_task_reply_required));
		}

		if (data.getTaskStatus() == TaskStatus.DONE && data.getTaskType() == TaskType.CASE_INVESTIGATION) {
			Case caze = DatabaseHelper.getCaseDao().queryUuidBasic(data.getCaze().getUuid());
			CaseDataDto cazeDto = new CaseDataDto();
			CaseDtoHelper.fillDto(cazeDto, caze);
			CaseLogic.validateInvestigationDoneAllowed(cazeDto);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (saveTask != null && !saveTask.isCancelled())
			saveTask.cancel(true);
	}
}
