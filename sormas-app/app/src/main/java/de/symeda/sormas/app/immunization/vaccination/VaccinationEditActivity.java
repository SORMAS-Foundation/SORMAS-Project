/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.immunization.vaccination;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;

import java.util.List;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.immunization.Immunization;
import de.symeda.sormas.app.backend.vaccination.VaccinationEntity;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

public class VaccinationEditActivity extends BaseEditActivity<VaccinationEntity> {

	private AsyncTask saveTask;

	public static void startActivity(Context context, String rootUuid) {
		BaseEditActivity.startActivity(context, VaccinationEditActivity.class, buildBundle(rootUuid));
	}

	public static void startActivity(Context context, String rootUuid, boolean finishInsteadOfUpNav) {
		BaseEditActivity.startActivity(context, VaccinationEditActivity.class, buildBundle(rootUuid, finishInsteadOfUpNav));
	}

	@Override
	protected VaccinationEntity queryRootEntity(String recordUuid) {
		return DatabaseHelper.getVaccinationDao().queryUuid(recordUuid);
	}

	@Override
	protected VaccinationEntity buildRootEntity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		getSaveMenu().setTitle(R.string.action_save_vaccination);
		return result;
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		List<PageMenuItem> menuItems = PageMenuItem.fromEnum(VaccinationSection.values(), getContext());
		return menuItems;
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, VaccinationEntity activityRootData) {
		VaccinationSection section = VaccinationSection.fromOrdinal(menuItem.getPosition());
		BaseEditFragment fragment;
		switch (section) {

		case VACCINATION_INFO:
			fragment = VaccinationEditFragment.newInstance(activityRootData);
			break;
		case HEALTH_CONDITIONS:
			fragment = VaccinationEditHealthConditionsFragment.newInstance(activityRootData);
			break;
		default:
			throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
		}

		return fragment;

	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_vaccination_edit;
	}

	@Override
	public void saveData() {
		if (saveTask != null) {
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
			return;
		}

		final VaccinationEntity vaccinationEntity = getStoredRootEntity();

		try {
			FragmentValidator.validate(getContext(), getActiveFragment().getContentBinding());
		} catch (ValidationException e) {
			NotificationHelper.showNotification(this, ERROR, e.getMessage());
			return;
		}

		saveTask = new SavingAsyncTask(getRootView(), vaccinationEntity) {

			@Override
			protected void onPreExecute() {
				showPreloader();
			}

			@Override
			public void doInBackground(TaskResultHolder resultHolder) throws DaoException {
				final VaccinationEntity savedVaccination = DatabaseHelper.getVaccinationDao().saveAndSnapshot(vaccinationEntity);
				final Immunization immunization = DatabaseHelper.getImmunizationDao().queryUuid(vaccinationEntity.getImmunization().getUuid());
				final List<VaccinationEntity> vaccinations = immunization.getVaccinations();
				for (VaccinationEntity ve : vaccinations) {
					if (ve.getUuid().equals(savedVaccination.getUuid())) {
						vaccinations.remove(ve);
						break;
					}
				}
				vaccinations.add(savedVaccination);
				DatabaseHelper.getImmunizationDao().saveAndSnapshot(immunization);
			}

			@Override
			protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
				hidePreloader();
				super.onPostExecute(taskResult);

				if (taskResult.getResultStatus().isSuccess()) {
					if (getActivePage().getPosition() == VaccinationSection.HEALTH_CONDITIONS.ordinal()) {
						finish();
					} else {
						goToNextPage();
					}
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
