/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.environmentsample.edit;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.environment.environmentsample.EnvironmentSample;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.environmentsample.EnvironmentSampleSection;
import de.symeda.sormas.app.sample.ShipmentStatus;

public class EnvironmentSampleEditActivity extends BaseEditActivity<EnvironmentSample> {

	private AsyncTask saveTask;

	public static void startActivity(Context context, String rootUuid) {
		BaseEditActivity.startActivity(context, EnvironmentSampleEditActivity.class, buildBundle(rootUuid));
	}

	public static void startActivity(Context context, String recordUuid, EnvironmentSampleSection section) {
		BaseEditActivity.startActivity(context, EnvironmentSampleEditActivity.class, buildBundle(recordUuid, section));
	}

	@Override
	protected EnvironmentSample queryRootEntity(String recordUuid) {
		return DatabaseHelper.getEnvironmentSampleDao().queryUuid(recordUuid);
	}

	@Override
	protected EnvironmentSample buildRootEntity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		return PageMenuItem.fromEnum(EnvironmentSampleSection.values(), getContext());
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, EnvironmentSample activityRootData) {
		int menuKey = 0;
		if (menuItem != null)
			menuKey = menuItem.getPosition();
		EnvironmentSampleSection section = EnvironmentSampleSection.fromOrdinal(menuKey);
		BaseEditFragment fragment;
		switch (section) {
		case ENVIRONMENT_SAMPLE_INFO:
			fragment = EnvironmentSampleEditFragment.newInstance(activityRootData);
			break;
		default:
			throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
		}
		return fragment;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		getSaveMenu().setTitle(R.string.action_save_environment_sample);
		return result;
	}

	@Override
	public ShipmentStatus getPageStatus() {
		EnvironmentSample sample = getStoredRootEntity();
		return sample != null
			? sample.isReceived() ? ShipmentStatus.RECEIVED : sample.isDispatched() ? ShipmentStatus.SHIPPED : ShipmentStatus.NOT_SHIPPED
			: null;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_environment_sample_edit;
	}

	@Override
	public void saveData() {

		if (saveTask != null) {
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
			return; // don't save multiple times
		}

		final EnvironmentSample sampleToSave = getStoredRootEntity();
		EnvironmentSampleEditFragment fragment = (EnvironmentSampleEditFragment) getActiveFragment();

		try {
			FragmentValidator.validate(getContext(), fragment.getContentBinding());
		} catch (ValidationException e) {
			NotificationHelper.showNotification(this, ERROR, e.getMessage());
			return;
		}

		saveTask = new SavingAsyncTask(getRootView(), sampleToSave) {

			@Override
			protected void onPreExecute() {
				showPreloader();
			}

			@Override
			public void doInBackground(TaskResultHolder resultHolder) throws Exception, ValidationException {
				DatabaseHelper.getEnvironmentSampleDao().saveAndSnapshot(sampleToSave);
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
	public void goToNewView() {
		EnvironmentSampleSection activeSection = EnvironmentSampleSection.fromOrdinal(getActivePage().getPosition());

//		if (activeSection == SampleSection.PATHOGEN_TESTS) {
//			PathogenTestNewActivity.startActivity(getContext(), getRootUuid());
//		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (saveTask != null && !saveTask.isCancelled())
			saveTask.cancel(true);
	}

}
