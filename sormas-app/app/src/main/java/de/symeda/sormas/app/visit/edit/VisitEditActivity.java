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

package de.symeda.sormas.app.visit.edit;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.symptoms.SymptomsEditFragment;
import de.symeda.sormas.app.util.Bundler;
import de.symeda.sormas.app.visit.VisitSection;

public class VisitEditActivity extends BaseEditActivity<Visit> {

	public static final String TAG = VisitEditActivity.class.getSimpleName();

	private AsyncTask saveTask;

	private String contactUuid = null;

	public static void startActivity(Context context, String rootUuid, String contactUuid, VisitSection section) {
		BaseEditActivity.startActivity(context, VisitEditActivity.class, buildBundle(rootUuid, contactUuid, section));
	}

	public static Bundler buildBundle(String rootUuid, String contactUuid, VisitSection section) {
		return buildBundle(rootUuid, section).setContactUuid(contactUuid);
	}

	@Override
	protected void onCreateInner(Bundle savedInstanceState) {
		super.onCreateInner(savedInstanceState);
		contactUuid = new Bundler(savedInstanceState).getContactUuid();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		new Bundler(outState).setContactUuid(contactUuid);
	}

	@Override
	protected Visit queryRootEntity(String recordUuid) {
		return DatabaseHelper.getVisitDao().queryUuid(recordUuid);
	}

	@Override
	protected Visit buildRootEntity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public VisitStatus getPageStatus() {
		return getStoredRootEntity() == null ? null : getStoredRootEntity().getVisitStatus();
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		return PageMenuItem.fromEnum(VisitSection.values(), getContext());
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Visit activityRootData) {

		VisitSection section = VisitSection.fromOrdinal(menuItem.getPosition());
		BaseEditFragment fragment;
		switch (section) {
		case VISIT_INFO:
			fragment = VisitEditFragment.newInstance(activityRootData, contactUuid);
			break;
		case SYMPTOMS:
			fragment = SymptomsEditFragment.newInstance(activityRootData);
			break;
		default:
			throw new IllegalArgumentException(DataHelper.toStringNullable(section));
		}

		return fragment;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		getSaveMenu().setTitle(R.string.action_save_visit);
		return result;
	}

	@Override
	public void saveData() {

		if (saveTask != null) {
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
			return; // don't save multiple times
		}

		final Visit visit = getStoredRootEntity();

		try {
			FragmentValidator.validate(getContext(), getActiveFragment().getContentBinding());
		} catch (ValidationException e) {
			NotificationHelper.showNotification(this, ERROR, e.getMessage());
			return;
		}

		saveTask = new SavingAsyncTask(getRootView(), visit) {

			@Override
			protected void onPreExecute() {
				showPreloader();
			}

			@Override
			public void doInBackground(TaskResultHolder resultHolder) throws Exception {
				DatabaseHelper.getVisitDao().saveAndSnapshot(visit);
			}

			@Override
			protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
				hidePreloader();
				super.onPostExecute(taskResult);
				if (taskResult.getResultStatus().isSuccess()) {
					if (!goToNextPage()) {
						finish();
					}
				} else {
					onResume(); // reload data
				}
				saveTask = null;
			}
		}.executeOnThreadPool();
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_contact_visit;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (saveTask != null && !saveTask.isCancelled())
			saveTask.cancel(true);
	}
}
