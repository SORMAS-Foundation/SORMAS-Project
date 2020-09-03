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
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.therapy.Prescription;
import de.symeda.sormas.app.backend.therapy.Treatment;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.util.Bundler;

public class TreatmentNewActivity extends BaseEditActivity<Treatment> {

	private AsyncTask saveTask;
	private String caseUuid = null;
	private String prescriptionUuid = null;

	public static void startActivity(Context context, String caseUuid) {
		BaseEditActivity.startActivity(context, TreatmentNewActivity.class, buildBundle(caseUuid));
	}

	public static void startActivityFromPrescription(Context context, String prescriptionUuid) {
		BaseEditActivity.startActivity(context, TreatmentNewActivity.class, buildBundleWithPrescription(prescriptionUuid));
	}

	public static Bundler buildBundle(String caseUuid) {
		return buildBundle(null, 0).setCaseUuid(caseUuid);
	}

	public static Bundler buildBundleWithPrescription(String prescriptionUuid) {
		return buildBundle(null, 0).setPrescriptionUuid(prescriptionUuid);
	}

	@Override
	protected void onCreateInner(Bundle savedInstanceState) {
		super.onCreateInner(savedInstanceState);
		caseUuid = new Bundler(savedInstanceState).getCaseUuid();
		prescriptionUuid = new Bundler(savedInstanceState).getPrescriptionUuid();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		new Bundler(outState).setCaseUuid(caseUuid);
		new Bundler(outState).setPrescriptionUuid(prescriptionUuid);
	}

	@Override
	protected Treatment queryRootEntity(String recordUuid) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Treatment buildRootEntity() {
		if (prescriptionUuid != null) {
			Prescription prescription = DatabaseHelper.getPrescriptionDao().queryUuid(prescriptionUuid);
			return DatabaseHelper.getTreatmentDao().build(prescription);
		} else {
			Case caze = DatabaseHelper.getCaseDao().queryUuidBasic(caseUuid);
			return DatabaseHelper.getTreatmentDao().build(caze);
		}
	}

	@Override
	public Enum getPageStatus() {
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		getSaveMenu().setTitle(R.string.action_save_treatment);
		return result;
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Treatment activityRootData) {
		BaseEditFragment fragment = TreatmentEditFragment.newInstance(activityRootData);
		fragment.setLiveValidationDisabled(true);
		return fragment;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_treatment_new;
	}

	@Override
	public void saveData() {
		if (saveTask != null) {
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
			return;
		}

		final Treatment treatment = getStoredRootEntity();
		TreatmentEditFragment fragment = (TreatmentEditFragment) getActiveFragment();

		fragment.setLiveValidationDisabled(false);

		try {
			FragmentValidator.validate(getContext(), fragment.getContentBinding());
		} catch (ValidationException e) {
			NotificationHelper.showNotification(this, ERROR, e.getMessage());
			return;
		}

		saveTask = new SavingAsyncTask(getRootView(), treatment) {

			@Override
			protected void onPreExecute() {
				showPreloader();
			}

			@Override
			public void doInBackground(TaskResultHolder resultHolder) throws DaoException {
				DatabaseHelper.getTreatmentDao().saveAndSnapshot(treatment);
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
