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

package de.symeda.sormas.app.immunization.edit;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.immunization.Immunization;
import de.symeda.sormas.app.backend.immunization.ImmunizationCriteria;
import de.symeda.sormas.app.backend.immunization.ImmunizationEditAuthorization;
import de.symeda.sormas.app.backend.immunization.ImmunizationSimilarityCriteria;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.immunization.ImmunizationSection;
import de.symeda.sormas.app.immunization.vaccination.VaccinationNewActivity;
import de.symeda.sormas.app.person.edit.PersonEditFragment;
import de.symeda.sormas.app.util.Bundler;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

public class ImmunizationEditActivity extends BaseEditActivity<Immunization> {

	private AsyncTask saveTask;
	private List<PageMenuItem> pageMenuItems;

	public static void startActivity(Context context, String rootUuid) {
		BaseEditActivity.startActivity(context, ImmunizationEditActivity.class, buildBundle(rootUuid));
	}

	public static void startActivity(Context context, String recordUuid, ImmunizationSection section) {
		BaseActivity.startActivity(context, ImmunizationEditActivity.class, buildBundle(recordUuid, section));
	}

	public static Bundler buildBundle(String recordUuid, ImmunizationSection section) {
		return BaseEditActivity.buildBundle(recordUuid, section.ordinal());
	}

	@Override
	protected Immunization queryRootEntity(String recordUuid) {
		return DatabaseHelper.getImmunizationDao().queryUuidWithEmbedded(recordUuid);
	}

	@Override
	protected Immunization buildRootEntity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		final Immunization storedRootEntity = this.getStoredRootEntity();
		if (storedRootEntity != null) {
			return pageMenuItems != null ? pageMenuItems : updatePageMenuItems(storedRootEntity.getMeansOfImmunization());
		} else {
			return PageMenuItem.fromEnum(ImmunizationSection.values(), getContext());
		}
	}

	private List<PageMenuItem> updatePageMenuItems(MeansOfImmunization meansOfImmunization) {
		if (meansOfImmunization == MeansOfImmunization.VACCINATION || meansOfImmunization == MeansOfImmunization.VACCINATION_RECOVERY) {
			pageMenuItems = PageMenuItem.fromEnum(ImmunizationSection.values(), getContext());
		} else {
			pageMenuItems = PageMenuItem.fromEnum(getContext(), ImmunizationSection.IMMUNIZATION_INFO, ImmunizationSection.PERSON_INFO);
		}
		return pageMenuItems;
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Immunization activityRootData) {
		ImmunizationSection section = ImmunizationSection.fromOrdinal(menuItem.getPosition());
		BaseEditFragment fragment;
		switch (section) {

		case PERSON_INFO:
			fragment = PersonEditFragment.newInstance(activityRootData);
			break;
		case IMMUNIZATION_INFO:
			fragment = ImmunizationEditFragment.newInstance(activityRootData, meansOfImmunization -> {
				this.updatePageMenuItems(meansOfImmunization);
				updatePageMenu();
			});
			break;
		case VACCINATIONS:
			fragment = ImmunizationEditVaccinationListFragment.newInstance(activityRootData);
			break;
		default:
			throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
		}

		return fragment;
	}

	@Override
	public void saveData() {

		if (saveTask != null) {
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
			return; // don't save multiple times
		}

		final Immunization changedImmunization = getStoredRootEntity();

		if (ImmunizationEditAuthorization.isImmunizationEditAllowed(changedImmunization)) {

			final ImmunizationCriteria immunizationCriteria = new ImmunizationCriteria();
			immunizationCriteria.setResponsibleRegion(changedImmunization.getResponsibleRegion());
			final Disease disease = changedImmunization.getDisease();
			immunizationCriteria.setDisease(disease);
			immunizationCriteria.setMeansOfImmunization(changedImmunization.getMeansOfImmunization());
			final ImmunizationSimilarityCriteria criteria = new ImmunizationSimilarityCriteria();
			criteria.setImmunizationCriteria(immunizationCriteria);
			criteria.setImmunizationUuid(changedImmunization.getUuid());
			criteria.setPersonUuid(changedImmunization.getPerson().getUuid());
			criteria.setStartDate(changedImmunization.getStartDate());
			criteria.setEndDate(changedImmunization.getEndDate());

			List<Immunization> similarImmunizations = DatabaseHelper.getImmunizationDao().getSimilarImmunizations(criteria);

			if (!similarImmunizations.isEmpty() && getActivePage().getPosition() == ImmunizationSection.IMMUNIZATION_INFO.ordinal()) {
				final ImmunizationOverlapsDto immunizationOverlapsDto = new ImmunizationOverlapsDto();
				immunizationOverlapsDto.setStartDate(changedImmunization.getStartDate());
				immunizationOverlapsDto.setEndDate(changedImmunization.getEndDate());
				final Immunization existingSimilarImmunization = similarImmunizations.get(0);
				immunizationOverlapsDto.setStartDateExisting(existingSimilarImmunization.getStartDate());
				immunizationOverlapsDto.setEndDateExisting(existingSimilarImmunization.getEndDate());
				immunizationOverlapsDto.setDisease(disease);

				final ImmunizationEditOverrideDialog immunizationEditOverrideDialog = new ImmunizationEditOverrideDialog(
					getContext(),
					R.layout.dialog_immunization_overlaps_layout,
					immunizationOverlapsDto,
					() -> saveImmunization(changedImmunization));
				immunizationEditOverrideDialog.show();
			} else {
				saveImmunization(changedImmunization);
			}
		} else {
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_edit_forbidden));
		}
	}

	private void saveImmunization(Immunization changedImmunization) {
		try {
			FragmentValidator.validate(getContext(), getActiveFragment().getContentBinding());
		} catch (ValidationException e) {
			NotificationHelper.showNotification(this, ERROR, e.getMessage());
			return;
		}

		saveTask = new SavingAsyncTask(getRootView(), changedImmunization) {

			@Override
			protected void onPreExecute() {
				showPreloader();
			}

			@Override
			protected void doInBackground(TaskResultHolder resultHolder) throws DaoException {
				synchronized (ImmunizationEditActivity.this) {
					DatabaseHelper.getPersonDao().saveAndSnapshot(changedImmunization.getPerson());
					DatabaseHelper.getImmunizationDao().saveAndSnapshot(changedImmunization);
				}
			}

			@Override
			protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
				hidePreloader();
				super.onPostExecute(taskResult);
				if (taskResult.getResultStatus().isSuccess()) {
					if (getActivePage().getPosition() == ImmunizationSection.PERSON_INFO.ordinal()) {
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
	public Enum getPageStatus() {
		return null;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_immunization_edit;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (saveTask != null && !saveTask.isCancelled()) {
			saveTask.cancel(true);
		}
	}

	@Override
	public void goToNewView() {
		ImmunizationSection activeSection = ImmunizationSection.fromOrdinal(getActivePage().getPosition());

		if (activeSection == ImmunizationSection.VACCINATIONS) {
			discardStoredRootEntity();
			VaccinationNewActivity.startActivity(getContext(), getRootUuid());
		}
	}
}
