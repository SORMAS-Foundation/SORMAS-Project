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

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.event.EventSection;
import de.symeda.sormas.app.event.eventparticipant.EventParticipantSaver;
import de.symeda.sormas.app.util.Bundler;

public class EventNewActivity extends BaseEditActivity<Event> {

	public static final String TAG = EventNewActivity.class.getSimpleName();

	private AsyncTask saveTask;

	private String caseUuid = null;

	public static void startActivity(Context fromActivity) {
		BaseEditActivity.startActivity(fromActivity, EventNewActivity.class, buildBundle(null));
	}

	public static void startActivityFromCase(Context fromActivity, String caseUuid) {
		BaseEditActivity.startActivity(fromActivity, EventNewActivity.class, buildBundleWithCase(caseUuid));
	}

	public static Bundler buildBundleWithCase(String caseUuid) {
		return BaseEditActivity.buildBundle(null).setCaseUuid(caseUuid);
	}

	@Override
	protected Event queryRootEntity(String recordUuid) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Event buildRootEntity() {
		Event event = DatabaseHelper.getEventDao().build();
		return event;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		getSaveMenu().setTitle(R.string.action_save_event);
		return result;
	}

	@Override
	public EventStatus getPageStatus() {
		return null;
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Event activityRootData) {
		BaseEditFragment fragment = EventEditFragment.newInstance(activityRootData);
		fragment.setLiveValidationDisabled(true);
		return fragment;
	}

	@Override
	protected void onCreateInner(Bundle savedInstanceState) {
		super.onCreateInner(savedInstanceState);
		Bundler bundler = new Bundler(savedInstanceState);
		caseUuid = bundler.getCaseUuid();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		Bundler bundler = new Bundler(outState);
		bundler.setCaseUuid(caseUuid);
	}

	@Override
	public void replaceFragment(BaseEditFragment f, boolean allowBackNavigation) {
		super.replaceFragment(f, allowBackNavigation);
		getActiveFragment().setLiveValidationDisabled(true);
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_event_new;
	}

	@Override
	public void saveData() {

		if (saveTask != null) {
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
			return; // don't save multiple times
		}

		final Event eventToSave = (Event) getActiveFragment().getPrimaryData();
		EventEditFragment fragment = (EventEditFragment) getActiveFragment();

		if (caseUuid != null) {
			Case linkedCase = DatabaseHelper.getCaseDao().getByReferenceDto(new CaseReferenceDto(caseUuid));
			if (eventToSave.getDisease() != null && !eventToSave.getDisease().equals(linkedCase.getDisease())) {
				NotificationHelper
					.showNotification(this, WARNING, getString(R.string.message_Event_and_Case_disease_mismatch) + " " + linkedCase.getDisease());
				return;
			}
		}

		fragment.setLiveValidationDisabled(false);

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
					finish();
					if (caseUuid != null) {
						EventParticipant eventParticipantToSave = DatabaseHelper.getEventParticipantDao().build();
						Case linkedCase = DatabaseHelper.getCaseDao().getByReferenceDto(new CaseReferenceDto(caseUuid));
						eventParticipantToSave.setPerson(linkedCase.getPerson());
						eventParticipantToSave.setEvent(eventToSave);
						eventParticipantToSave.setResultingCaseUuid(linkedCase.getUuid());
						EventParticipantSaver eventParticipantSaver = new EventParticipantSaver(EventNewActivity.this);
						eventParticipantSaver.saveEventParticipantLinkedToCase(eventParticipantToSave, false);
					} else {
						EventEditActivity.startActivity(getContext(), eventToSave.getUuid(), EventSection.EVENT_PARTICIPANTS);
					}
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
