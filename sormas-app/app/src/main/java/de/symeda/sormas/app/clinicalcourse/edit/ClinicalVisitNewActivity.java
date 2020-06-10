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

package de.symeda.sormas.app.clinicalcourse.edit;

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
import de.symeda.sormas.app.backend.clinicalcourse.ClinicalVisit;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.clinicalcourse.ClinicalVisitSection;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.util.Bundler;

public class ClinicalVisitNewActivity extends BaseEditActivity<ClinicalVisit> {

	private AsyncTask saveTask;
	private String caseUuid;

	public static void startActivity(Context context, String caseUuid) {
		BaseEditActivity.startActivity(context, ClinicalVisitNewActivity.class, buildBundle(caseUuid));
	}

	public static Bundler buildBundle(String caseUuid) {
		return buildBundle(null, 0).setCaseUuid(caseUuid);
	}

	@Override
	protected void onCreateInner(Bundle savedInstanceState) {
		super.onCreateInner(savedInstanceState);
		caseUuid = new Bundler(savedInstanceState).getCaseUuid();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		new Bundler(outState).setCaseUuid(caseUuid);
	}

	@Override
	protected ClinicalVisit queryRootEntity(String recordUuid) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected ClinicalVisit buildRootEntity() {
		return DatabaseHelper.getClinicalVisitDao().build(caseUuid);
	}

	@Override
	public Enum getPageStatus() {
		return null;
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, ClinicalVisit activityRootData) {
		BaseEditFragment fragment = ClinicalVisitEditFragment.newInstance(activityRootData);
		fragment.setLiveValidationDisabled(true);
		return fragment;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		getSaveMenu().setTitle(R.string.action_save_clinical_visit);
		return result;
	}

	@Override
	public void saveData() {

		if (saveTask != null) {
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
			return; // don't save multiple times
		}

		final ClinicalVisit clinicalVisit = getStoredRootEntity();
		ClinicalVisitEditFragment fragment = (ClinicalVisitEditFragment) getActiveFragment();

		fragment.setLiveValidationDisabled(false);

		try {
			FragmentValidator.validate(getContext(), getActiveFragment().getContentBinding());
		} catch (ValidationException e) {
			NotificationHelper.showNotification(this, ERROR, e.getMessage());
			return;
		}

		saveTask = new SavingAsyncTask(getRootView(), clinicalVisit) {

			@Override
			protected void onPreExecute() {
				showPreloader();
			}

			@Override
			public void doInBackground(TaskResultHolder resultHolder) throws Exception {
				DatabaseHelper.getClinicalVisitDao().saveAndSnapshot(clinicalVisit);
			}

			@Override
			protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
				hidePreloader();
				super.onPostExecute(taskResult);
				if (taskResult.getResultStatus().isSuccess()) {
					finish();
					ClinicalVisitEditActivity
						.startActivity(getContext(), clinicalVisit.getUuid(), caseUuid, ClinicalVisitSection.CLINICAL_MEASUREMENTS);
				}

				saveTask = null;
			}
		}.executeOnThreadPool();
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_clinical_visit_new;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (saveTask != null && !saveTask.isCancelled())
			saveTask.cancel(true);
	}
}
