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

package de.symeda.sormas.app.event.edit;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.event.EventSection;
import de.symeda.sormas.app.event.eventparticipant.edit.EventParticipantNewActivity;
import de.symeda.sormas.app.task.edit.TaskNewActivity;

public class EventEditActivity extends BaseEditActivity<Event> {

	public static final String TAG = EventEditActivity.class.getSimpleName();

	private AsyncTask saveTask;

	public static void startActivity(Context context, String rootUuid, EventSection section) {
		BaseEditActivity.startActivity(context, EventEditActivity.class, buildBundle(rootUuid, section));
	}

	@Override
	protected Event queryRootEntity(String recordUuid) {
		return DatabaseHelper.getEventDao().queryUuidWithEmbedded(recordUuid);
	}

	@Override
	protected Event buildRootEntity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		return PageMenuItem.fromEnum(EventSection.values(), getContext());
	}

	@Override
	public EventStatus getPageStatus() {
		return getStoredRootEntity() == null ? null : getStoredRootEntity().getEventStatus();
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Event activityRootData) {
		EventSection section = EventSection.fromOrdinal(menuItem.getPosition());
		BaseEditFragment fragment;
		switch (section) {
		case EVENT_INFO:
			fragment = EventEditFragment.newInstance(activityRootData);
			break;
		case EVENT_PARTICIPANTS:
			fragment = EventEditPersonsInvolvedListFragment.newInstance(activityRootData);
			break;
		case TASKS:
			fragment = EventEditTaskListFragment.newInstance(activityRootData);
			break;
		default:
			throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
		}
		return fragment;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		getSaveMenu().setTitle(R.string.action_save_event);
		return result;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_event_edit;
	}

	@Override
	public void goToNewView() {
		EventSection section = EventSection.fromOrdinal(getActivePage().getPosition());

		switch (section) {
		case EVENT_PARTICIPANTS:
			EventParticipantNewActivity.startActivity(getContext(), getRootUuid());
			break;
		case TASKS:
			TaskNewActivity.startActivityFromEvent(getContext(), getRootUuid());
			break;
		default:
			throw new IllegalArgumentException(DataHelper.toStringNullable(section));
		}
	}

	@Override
	public void saveData() {

		if (saveTask != null) {
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
			return; // don't save multiple times
		}

		final Event eventToSave = (Event) getActiveFragment().getPrimaryData();
		EventEditFragment fragment = (EventEditFragment) getActiveFragment();

		try {
			FragmentValidator.validate(getContext(), fragment.getContentBinding());
		} catch (ValidationException e) {
			NotificationHelper.showNotification(this, ERROR, e.getMessage());
			return;
		}

		saveTask = new SavingAsyncTask(getRootView(), eventToSave) {

			@Override
			protected void onPreExecute() {
				showPreloader();
			}

			@Override
			public void doInBackground(TaskResultHolder resultHolder) throws DaoException {
				DatabaseHelper.getEventDao().saveAndSnapshot(eventToSave);
			}

			@Override
			protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
				hidePreloader();
				super.onPostExecute(taskResult);
				if (taskResult.getResultStatus().isSuccess()) {
					goToNextPage();
				} else {
					onResume(); // reload data
				}
				saveTask = null;
			}
		}.executeOnThreadPool();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (saveTask != null && !saveTask.isCancelled())
			saveTask.cancel(true);
	}
}
