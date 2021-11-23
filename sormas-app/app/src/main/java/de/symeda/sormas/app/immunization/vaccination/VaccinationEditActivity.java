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

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.immunization.Immunization;
import de.symeda.sormas.app.backend.vaccination.Vaccination;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;

public class VaccinationEditActivity extends BaseEditActivity<Vaccination> {

	private AsyncTask saveTask;

	public static void startActivity(Context context, String rootUuid) {
		BaseEditActivity.startActivity(context, VaccinationEditActivity.class, buildBundle(rootUuid));
	}

	public static void startActivity(Context context, String rootUuid, boolean finishInsteadOfUpNav) {
		BaseEditActivity.startActivity(context, VaccinationEditActivity.class, buildBundle(rootUuid, finishInsteadOfUpNav));
	}

	@Override
	protected Vaccination queryRootEntity(String recordUuid) {
		return DatabaseHelper.getVaccinationDao().queryUuid(recordUuid);
	}

	@Override
	protected Vaccination buildRootEntity() {
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
		if (!ConfigProvider.hasUserRight(UserRight.IMMUNIZATION_VIEW)
			|| DatabaseHelper.getFeatureConfigurationDao().isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {
			menuItems.set(VaccinationSection.HEALTH_CONDITIONS.ordinal(), null);
		}
		return menuItems;
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Vaccination activityRootData) {
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

		final Vaccination Vaccination = getStoredRootEntity();

		try {
			FragmentValidator.validate(getContext(), getActiveFragment().getContentBinding());
		} catch (ValidationException e) {
			NotificationHelper.showNotification(this, ERROR, e.getMessage());
			return;
		}

		saveTask = new SavingAsyncTask(getRootView(), Vaccination) {

			@Override
			protected void onPreExecute() {
				showPreloader();
			}

			@Override
			public void doInBackground(TaskResultHolder resultHolder) throws DaoException {
				final Vaccination savedVaccination = DatabaseHelper.getVaccinationDao().saveAndSnapshot(Vaccination);
				final Immunization immunization = DatabaseHelper.getImmunizationDao().queryUuid(Vaccination.getImmunization().getUuid());
				final List<Vaccination> vaccinations = immunization.getVaccinations();
				for (Vaccination ve : vaccinations) {
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
