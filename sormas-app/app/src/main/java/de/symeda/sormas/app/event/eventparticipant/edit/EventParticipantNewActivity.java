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

package de.symeda.sormas.app.event.eventparticipant.edit;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.event.eventparticipant.EventParticipantSection;
import de.symeda.sormas.app.person.SelectOrCreatePersonDialog;
import de.symeda.sormas.app.util.Bundler;
import de.symeda.sormas.app.util.Consumer;

public class EventParticipantNewActivity extends BaseEditActivity<EventParticipant> {

	public static final String TAG = EventParticipantNewActivity.class.getSimpleName();

	private AsyncTask saveTask;

	private String eventUuid = null;

	private boolean rapidEntry;

	public static void startActivity(Context context, String eventUuid, boolean rapidEntry) {
		BaseEditActivity.startActivity(context, EventParticipantNewActivity.class, buildBundle(eventUuid, rapidEntry));
	}

	public static Bundler buildBundle(String eventUuid, boolean rapidEntry) {
		return buildBundle(null, 0).setEventUuid(eventUuid).setRapidEntry(rapidEntry);
	}

	@Override
	protected void onCreateInner(Bundle savedInstanceState) {
		super.onCreateInner(savedInstanceState);
		Bundler bundler = new Bundler(savedInstanceState);
		eventUuid = bundler.getEventUuid();
		rapidEntry = bundler.isRapidEntry();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		new Bundler(outState).setEventUuid(eventUuid);
	}

	@Override
	protected EventParticipant queryRootEntity(String recordUuid) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected EventParticipant buildRootEntity() {
		Person person = DatabaseHelper.getPersonDao().build();
		EventParticipant eventParticipant = DatabaseHelper.getEventParticipantDao().build();
		eventParticipant.setPerson(person);
		Event event = DatabaseHelper.getEventDao().queryUuid(eventUuid);
		eventParticipant.setEvent(event);
		return eventParticipant;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		getSaveMenu().setTitle(R.string.action_save_event);
		return result;
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, EventParticipant activityRootData) {
		BaseEditFragment fragment = EventParticipantNewFragment.newInstance(activityRootData, rapidEntry);
		fragment.setLiveValidationDisabled(true);
		return fragment;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_event_participant_new;
	}

	@Override
	public Enum getPageStatus() {
		return null;
	}

	@Override
	public void saveData() {

		if (saveTask != null) {
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
			return; // don't save multiple times
		}

		final EventParticipant eventParticipantToSave = (EventParticipant) getActiveFragment().getPrimaryData();
		EventParticipantNewFragment fragment = (EventParticipantNewFragment) getActiveFragment();

		fragment.setLiveValidationDisabled(false);

		try {
			FragmentValidator.validate(getContext(), fragment.getContentBinding());
		} catch (ValidationException e) {
			NotificationHelper.showNotification(this, ERROR, e.getMessage());
			return;
		}

		final Event event = DatabaseHelper.getEventDao().queryUuid(eventUuid);

		SelectOrCreatePersonDialog.selectOrCreatePerson(eventParticipantToSave.getPerson(), event, new Consumer<Person>() {

			@Override
			public void accept(Person person) {
				eventParticipantToSave.setPerson(person);

				saveTask = new SavingAsyncTask(getRootView(), eventParticipantToSave) {

					@Override
					protected void onPreExecute() {
						showPreloader();
					}

					@Override
					protected void doInBackground(TaskResultHolder resultHolder) throws Exception {
						DatabaseHelper.getPersonDao().saveAndSnapshot(eventParticipantToSave.getPerson());
						eventParticipantToSave.setEvent(event);
						DatabaseHelper.getEventParticipantDao().saveAndSnapshot(eventParticipantToSave);
					}

					@Override
					protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
						hidePreloader();
						super.onPostExecute(taskResult);
						if (taskResult.getResultStatus().isSuccess()) {
							if (((EventParticipantNewFragment) getActiveFragment()).isRapidEntry()) {
								rapidEntry = true;
								startActivity(getContext(), EventParticipantNewActivity.class, buildBundle(eventUuid, true));
							} else {
								EventParticipantEditActivity
									.startActivity(getContext(), getRootUuid(), eventUuid, EventParticipantSection.EVENT_PARTICIPANT_INFO);
							}
						}
						saveTask = null;
					}
				}.executeOnThreadPool();
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (saveTask != null && !saveTask.isCancelled())
			saveTask.cancel(true);
	}
}
