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

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.symeda.sormas.api.utils.DataHelper;
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
import de.symeda.sormas.app.symptoms.SymptomsEditFragment;
import de.symeda.sormas.app.util.Bundler;

public class ClinicalVisitEditActivity extends BaseEditActivity<ClinicalVisit> {

	public static final String TAG = ClinicalVisitEditActivity.class.getSimpleName();

	private AsyncTask saveTask;
	private String caseUuid;

	public static void startActivity(Context context, String rootUuid, String caseUuid, ClinicalVisitSection section) {
		BaseEditActivity.startActivity(context, ClinicalVisitEditActivity.class, buildBundle(rootUuid, section).setCaseUuid(caseUuid));
	}

	public static Bundler buildBundleWithCase(String caseUuid) {
		return BaseEditActivity.buildBundle(null).setCaseUuid(caseUuid);
	}

	@Override
	public void onCreateInner(@Nullable Bundle savedInstanceState) {
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
		return DatabaseHelper.getClinicalVisitDao().queryUuid(recordUuid);
	}

	@Override
	protected ClinicalVisit buildRootEntity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enum getPageStatus() {
		return null;
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		return PageMenuItem.fromEnum(ClinicalVisitSection.values(), getContext());
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, ClinicalVisit activityRootData) {
		ClinicalVisitSection section = ClinicalVisitSection.fromOrdinal(menuItem.getPosition());
		BaseEditFragment fragment;
		switch (section) {
		case VISIT_INFO:
			fragment = ClinicalVisitEditFragment.newInstance(activityRootData);
			break;
		case CLINICAL_MEASUREMENTS:
			fragment = ClinicalMeasurementsEditFragment.newInstance(activityRootData.getSymptoms());
			break;
		case SYMPTOMS:
			fragment = SymptomsEditFragment.newInstance(activityRootData, caseUuid);
			break;
		default:
			throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
		}

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
					goToNextPage();
				} else {
					onResume(); // reload data
				}
				saveTask = null;
			}
		}.executeOnThreadPool();
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_clinical_visit;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (saveTask != null && !saveTask.isCancelled())
			saveTask.cancel(true);
	}
}
