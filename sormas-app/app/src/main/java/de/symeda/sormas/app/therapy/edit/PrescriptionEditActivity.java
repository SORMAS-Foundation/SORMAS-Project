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

package de.symeda.sormas.app.therapy.edit;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.therapy.Prescription;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;

public class PrescriptionEditActivity extends BaseEditActivity<Prescription> {

	private AsyncTask saveTask;

	public static void startActivity(Context context, String rootUuid) {
		BaseEditActivity.startActivity(context, PrescriptionEditActivity.class, buildBundle(rootUuid));
	}

	public static void startActivity(Context context, String rootUuid, boolean finishInsteadOfUpNav) {
		BaseEditActivity.startActivity(context, PrescriptionEditActivity.class, buildBundle(rootUuid, finishInsteadOfUpNav));
	}

	@Override
	protected Prescription queryRootEntity(String recordUuid) {
		return DatabaseHelper.getPrescriptionDao().queryUuid(recordUuid);
	}

	@Override
	protected Prescription buildRootEntity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		getSaveMenu().setTitle(R.string.action_save_prescription);
		return result;
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Prescription activityRootData) {
		return PrescriptionEditFragment.newInstance(activityRootData);
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_prescription_edit;
	}

	@Override
	public void saveData() {
		if (saveTask != null) {
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
			return;
		}

		final Prescription prescription = (Prescription) getActiveFragment().getPrimaryData();
		PrescriptionEditFragment fragment = (PrescriptionEditFragment) getActiveFragment();

		try {
			FragmentValidator.validate(getContext(), fragment.getContentBinding());
		} catch (ValidationException e) {
			NotificationHelper.showNotification(this, ERROR, e.getMessage());
			return;
		}

		saveTask = new SavingAsyncTask(getRootView(), prescription) {

			@Override
			protected void onPreExecute() {
				showPreloader();
			}

			@Override
			public void doInBackground(TaskResultHolder resultHolder) throws DaoException {
				DatabaseHelper.getPrescriptionDao().saveAndSnapshot(prescription);
			}

			@Override
			protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
				hidePreloader();
				super.onPostExecute(taskResult);

				if (taskResult.getResultStatus().isSuccess()) {
					finish();
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

	@Override
	public Enum getPageStatus() {
		return null;
	}
}
