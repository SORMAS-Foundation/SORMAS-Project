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

package de.symeda.sormas.app.pathogentest.edit;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

import java.util.Date;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.util.Bundler;

public class PathogenTestNewActivity extends BaseEditActivity<PathogenTest> {

	public static final String TAG = PathogenTestNewActivity.class.getSimpleName();

	private String sampleUuid = null;

	private AsyncTask saveTask;

	public static void startActivity(Context context, String sampleUuid) {
		BaseEditActivity.startActivity(context, PathogenTestNewActivity.class, buildBundle(sampleUuid));
	}

	public static Bundler buildBundle(String caseUuid) {
		return buildBundle(null, 0).setCaseUuid(caseUuid);
	}

	@Override
	public Enum getPageStatus() {
		return null;
	}

	@Override
	protected void onCreateInner(Bundle savedInstanceState) {
		super.onCreateInner(savedInstanceState);
		sampleUuid = new Bundler(savedInstanceState).getCaseUuid();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		new Bundler(outState).setCaseUuid(sampleUuid);
	}

	@Override
	protected PathogenTest queryRootEntity(String recordUuid) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected PathogenTest buildRootEntity() {
		// basic instead of reference, because we want to have at least the related person
		Sample associatedSample = DatabaseHelper.getSampleDao().queryUuid(sampleUuid);
		return DatabaseHelper.getSampleTestDao().build(associatedSample);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		getSaveMenu().setTitle(R.string.action_save_case);
		return result;
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, PathogenTest activityRootData) {
		BaseEditFragment fragment = PathogenTestEditFragment.newInstance(activityRootData);
		fragment.setLiveValidationDisabled(true);
		return fragment;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_pathogen_test_new;
	}

	@Override
	public void replaceFragment(BaseEditFragment f, boolean allowBackNavigation) {
		super.replaceFragment(f, allowBackNavigation);
	}

	@Override
	public void saveData() {

		if (saveTask != null) {
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
			return; // don't save multiple times
		}

		final PathogenTest PathogenTestToSave = getStoredRootEntity();
		PathogenTestEditFragment fragment = (PathogenTestEditFragment) getActiveFragment();

		if (PathogenTestToSave.getLabUser() == null) {
			NotificationHelper.showNotification(this, ERROR, getString(R.string.error_no_pathogentest_labuser));
			return;
		}
		if (PathogenTestToSave.getTestDateTime() == null) {
			PathogenTestToSave.setTestDateTime(new Date());
		}

		fragment.setLiveValidationDisabled(false);

		try {
			FragmentValidator.validate(getContext(), fragment.getContentBinding());
		} catch (ValidationException e) {
			NotificationHelper.showNotification(this, ERROR, e.getMessage());
			return;
		}

		saveTask = new SavingAsyncTask(getRootView(), PathogenTestToSave) {

			@Override
			protected void onPreExecute() {
				showPreloader();
			}

			@Override
			public void doInBackground(TaskResultHolder resultHolder) throws DaoException, ValidationException {
				DatabaseHelper.getSampleTestDao().saveAndSnapshot(PathogenTestToSave);
			}

			@Override
			protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
				hidePreloader();
				super.onPostExecute(taskResult);
				if (taskResult.getResultStatus().isSuccess()) {
					finish();
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
