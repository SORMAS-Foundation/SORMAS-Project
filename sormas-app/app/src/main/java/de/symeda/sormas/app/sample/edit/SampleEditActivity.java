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

package de.symeda.sormas.app.sample.edit;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;

import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.pathogentest.edit.PathogenTestNewActivity;
import de.symeda.sormas.app.sample.SampleSection;
import de.symeda.sormas.app.sample.ShipmentStatus;
import de.symeda.sormas.app.sample.read.SampleEditPathogenTestListFragment;

public class SampleEditActivity extends BaseEditActivity<Sample> {

	private AsyncTask saveTask;

	public static void startActivity(Context context, String rootUuid) {
		BaseEditActivity.startActivity(context, SampleEditActivity.class, buildBundle(rootUuid));
	}

	public static void startActivity(Context context, String recordUuid, SampleSection section) {
		BaseEditActivity.startActivity(context, SampleEditActivity.class, buildBundle(recordUuid, section));
	}

	@Override
	protected Sample queryRootEntity(String recordUuid) {
		return DatabaseHelper.getSampleDao().queryUuid(recordUuid);
	}

	@Override
	protected Sample buildRootEntity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		List<PageMenuItem> menuItems = PageMenuItem.fromEnum(SampleSection.values(), getContext());
		Sample sample = getStoredRootEntity();
//        if(sample != null && sample.getSamplePurpose().equals(SamplePurpose.INTERNAL)){
//            menuItems.remove(SampleSection.PATHOGEN_TESTS.ordinal());
//        }
		return menuItems;
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Sample activityRootData) {
		int menuKey = 0;
		if (menuItem != null)
			menuKey = menuItem.getPosition();
		SampleSection section = SampleSection.fromOrdinal(menuKey);
		BaseEditFragment fragment;
		switch (section) {
		case SAMPLE_INFO:
			fragment = SampleEditFragment.newInstance(activityRootData);
			break;
		case PATHOGEN_TESTS:
			fragment = SampleEditPathogenTestListFragment.newInstance(activityRootData);
			break;
		default:
			throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
		}
		return fragment;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		getSaveMenu().setTitle(R.string.action_save_sample);
		return result;
	}

	@Override
	public ShipmentStatus getPageStatus() {
		Sample sample = getStoredRootEntity();
		if (sample != null && SamplePurpose.INTERNAL != sample.getSamplePurpose()) {
			ShipmentStatus shipmentStatus = sample.getReferredToUuid() != null
				? ShipmentStatus.REFERRED_OTHER_LAB
				: sample.isReceived() ? ShipmentStatus.RECEIVED : sample.isShipped() ? ShipmentStatus.SHIPPED : ShipmentStatus.NOT_SHIPPED;
			return shipmentStatus;
		} else {
			return null;
		}
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_sample_edit;
	}

	@Override
	public void saveData() {

		if (saveTask != null) {
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
			return; // don't save multiple times
		}

		final Sample sampleToSave = getStoredRootEntity();
		SampleEditFragment fragment = (SampleEditFragment) getActiveFragment();

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
				DatabaseHelper.getSampleDao().saveAndSnapshot(sampleToSave);
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
		SampleSection activeSection = SampleSection.fromOrdinal(getActivePage().getPosition());

		if (activeSection == SampleSection.PATHOGEN_TESTS) {
			PathogenTestNewActivity.startActivity(getContext(), getRootUuid());
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (saveTask != null && !saveTask.isCancelled())
			saveTask.cancel(true);
	}
}
