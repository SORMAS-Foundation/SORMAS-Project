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

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.event.eventparticipant.EventParticipantSection;
import de.symeda.sormas.app.immunization.edit.ImmunizationNewActivity;
import de.symeda.sormas.app.immunization.vaccination.VaccinationNewActivity;
import de.symeda.sormas.app.util.Bundler;

public class EventParticipantEditActivity extends BaseEditActivity<EventParticipant> {

	private AsyncTask saveTask;
	private String eventUuid;

	public static Bundler buildBundle(String rootUuid, String eventUuid, EventParticipantSection section) {
		return buildBundle(rootUuid, section.ordinal()).setEventUuid(eventUuid);
	}

	public static void startActivity(Context context, String recordUuid, String eventUuid, EventParticipantSection section) {
		BaseActivity.startActivity(context, EventParticipantEditActivity.class, buildBundle(recordUuid, eventUuid, section));
	}

	@Override
	public EventStatus getPageStatus() {
		return null;
	}

	@Override
	protected void onCreateInner(Bundle savedInstanceState) {
		super.onCreateInner(savedInstanceState);
		eventUuid = new Bundler(savedInstanceState).getEventUuid();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		new Bundler(outState).setEventUuid(eventUuid);
	}

	@Override
	protected EventParticipant queryRootEntity(String recordUuid) {
		return DatabaseHelper.getEventParticipantDao().queryUuid(recordUuid);
	}

	@Override
	protected EventParticipant buildRootEntity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		getSaveMenu().setTitle(R.string.action_save_event_participant);
		return result;
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		List<PageMenuItem> menuItems = PageMenuItem.fromEnum(EventParticipantSection.values(), getContext());
		Event event = DatabaseHelper.getEventDao().queryUuid(eventUuid);
		if (!ConfigProvider.hasUserRight(UserRight.IMMUNIZATION_VIEW)
			|| DatabaseHelper.getFeatureConfigurationDao().isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {
			menuItems.set(EventParticipantSection.IMMUNIZATIONS.ordinal(), null);
		}
		if (!ConfigProvider.hasUserRight(UserRight.IMMUNIZATION_VIEW)
			|| !DatabaseHelper.getFeatureConfigurationDao().isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)
			|| event.getDisease() == null) {
			menuItems.set(EventParticipantSection.VACCINATIONS.ordinal(), null);
		}
		return menuItems;
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, EventParticipant activityRootData) {

		EventParticipantSection section = EventParticipantSection.fromOrdinal(menuItem.getPosition());
		BaseEditFragment fragment;
		switch (section) {

		case EVENT_PARTICIPANT_INFO:
			fragment = EventParticipantEditFragment.newInstance(activityRootData);
			break;
		case IMMUNIZATIONS:
			fragment = EventParticipantEditImmunizationListFragment.newInstance(activityRootData);
			break;
		case VACCINATIONS:
			fragment = EventParticipantEditVaccinationListFragment.newInstance(activityRootData);
			break;
		default:
			throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
		}
		return fragment;
	}

	@Override
	public void goToNewView() {
		EventParticipantSection activeSection = EventParticipantSection.fromOrdinal(getActivePage().getPosition());

		if (activeSection == EventParticipantSection.IMMUNIZATIONS) {
			ImmunizationNewActivity.startActivityFromEventParticipant(getContext(), getRootUuid());
		} else if (activeSection == EventParticipantSection.VACCINATIONS) {
			VaccinationNewActivity.startActivityFromEventParticipant(getContext(), getRootUuid());
		}
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_event_edit;
	}

	@Override
	public void saveData() {

		if (saveTask != null) {
			// don't save multiple times
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
			return;
		}

		final EventParticipant eventParticipant = (EventParticipant) getActiveFragment().getPrimaryData();
		EventParticipantEditFragment fragment = (EventParticipantEditFragment) getActiveFragment();

		try {
			FragmentValidator.validate(getContext(), fragment.getContentBinding());
		} catch (ValidationException e) {
			NotificationHelper.showNotification(this, ERROR, e.getMessage());
			return;
		}

		saveTask = new SavingAsyncTask(getRootView(), eventParticipant) {

			@Override
			protected void onPreExecute() {
				showPreloader();
			}

			@Override
			public void doInBackground(TaskResultHolder resultHolder) throws DaoException {
				DatabaseHelper.getPersonDao().saveAndSnapshot(eventParticipant.getPerson());
				DatabaseHelper.getEventParticipantDao().saveAndSnapshot(eventParticipant);
			}

			@Override
			protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
				hidePreloader();
				super.onPostExecute(taskResult);

				if (taskResult.getResultStatus().isSuccess()) {
					finish();
				} else {
					// reload data
					onResume();
				}
				saveTask = null;
			}
		}.executeOnThreadPool();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (saveTask != null && !saveTask.isCancelled()) {
			saveTask.cancel(true);
		}
	}
}
