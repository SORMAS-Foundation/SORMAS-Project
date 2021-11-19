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

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.immunization.Immunization;
import de.symeda.sormas.app.backend.immunization.ImmunizationCriteria;
import de.symeda.sormas.app.backend.immunization.ImmunizationEntityHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.vaccination.Vaccination;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.util.Bundler;

public class VaccinationNewActivity extends BaseEditActivity<Vaccination> {

	public static final String TAG = VaccinationNewActivity.class.getSimpleName();

	private String immunizationUuid = null;
	private String caseUuid = null;
	private String contactUuid = null;
	private String eventParticipantUuid = null;
	private Vaccination vaccination;

	private AsyncTask saveTask;

	public static void startActivity(Context context, String immunizationUuid) {
		BaseEditActivity.startActivity(context, VaccinationNewActivity.class, buildBundle(immunizationUuid));
	}

	public static void startActivityFromCase(Context context, String caseUuid) {
		BaseEditActivity.startActivity(context, VaccinationNewActivity.class, buildBundleWithCase(caseUuid));
	}

	public static void startActivityFromContact(Context context, String contactUuid) {
		BaseEditActivity.startActivity(context, VaccinationNewActivity.class, buildBundleWithContact(contactUuid));
	}

	public static void startActivityFromEventParticipant(Context context, String eventParticipantUuid) {
		BaseEditActivity.startActivity(context, VaccinationNewActivity.class, buildBundleWithEventParticipant(eventParticipantUuid));
	}

	public static Bundler buildBundle(String immunizationUuid) {
		return buildBundle(null, 0).setImmunizationUuid(immunizationUuid);
	}

	public static Bundler buildBundleWithCase(String caseUuid) {
		return BaseEditActivity.buildBundle(null).setCaseUuid(caseUuid);
	}

	public static Bundler buildBundleWithContact(String contactUuid) {
		return BaseEditActivity.buildBundle(null).setContactUuid(contactUuid);
	}

	public static Bundler buildBundleWithEventParticipant(String eventParticipantUuid) {
		return BaseEditActivity.buildBundle(null).setEventParticipantUuid(eventParticipantUuid);
	}

	@Override
	public Enum getPageStatus() {
		return null;
	}

	@Override
	protected void onCreateInner(Bundle savedInstanceState) {
		super.onCreateInner(savedInstanceState);
		immunizationUuid = new Bundler(savedInstanceState).getImmunizationUuid();
		caseUuid = new Bundler(savedInstanceState).getCaseUuid();
		contactUuid = new Bundler(savedInstanceState).getContactUuid();
		eventParticipantUuid = new Bundler(savedInstanceState).getEventParticipantUuid();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		new Bundler(outState).setCaseUuid(immunizationUuid);
	}

	@Override
	protected Vaccination queryRootEntity(String recordUuid) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Vaccination buildRootEntity() {
		// basic instead of reference, because we want to have at least the related person
		Immunization immunization = null;
		if (immunizationUuid != null) {
			immunization = DatabaseHelper.getImmunizationDao().queryUuid(immunizationUuid);
		}
		return DatabaseHelper.getVaccinationDao().build(immunization);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		getSaveMenu().setTitle(R.string.action_save_vaccination);
		return result;
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Vaccination activityRootData) {

		BaseEditFragment fragment = VaccinationEditFragment.newInstance(activityRootData);
		fragment.setLiveValidationDisabled(true);
		return fragment;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_vaccination_new;
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

		vaccination = getStoredRootEntity();
		VaccinationEditFragment fragment = (VaccinationEditFragment) getActiveFragment();

		if (vaccination.getReportingUser() == null) {
			vaccination.setReportingUser(ConfigProvider.getUser());
		}

		fragment.setLiveValidationDisabled(false);

		try {
			FragmentValidator.validate(getContext(), fragment.getContentBinding());
		} catch (ValidationException e) {
			NotificationHelper.showNotification(this, ERROR, e.getMessage());
			return;
		}

		saveTask = new SavingAsyncTask(getRootView(), vaccination) {

			@Override
			protected void onPreExecute() {
				showPreloader();
			}

			@Override
			public void doInBackground(TaskResultHolder resultHolder) throws DaoException {

				if (vaccination.getImmunization() == null) {
					//in case of Reduced immunization feature
					Immunization newImmunization = buildImmunizationFromRootEntity();
					boolean immunizationFound = addImmunizationToVaccination(vaccination, newImmunization.getPerson(), newImmunization.getDisease());
					if (!immunizationFound) {
						createImmunization(newImmunization);
					}
				}

				final Vaccination savedVaccination = DatabaseHelper.getVaccinationDao().saveAndSnapshot(vaccination);

				final Immunization immunization = DatabaseHelper.getImmunizationDao().queryUuid(vaccination.getImmunization().getUuid());
				final List<Vaccination> vaccinations = immunization.getVaccinations();
				vaccinations.add(savedVaccination);
				DatabaseHelper.getImmunizationDao().saveAndSnapshot(immunization);
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

	private boolean addImmunizationToVaccination(Vaccination vaccination, Person person, Disease disease) {

		ImmunizationCriteria immunizationCriteria = new ImmunizationCriteria();
		immunizationCriteria.setPerson(person);
		immunizationCriteria.setDisease(disease);

		List<Immunization> immunizations = DatabaseHelper.getImmunizationDao().queryAllByCriteria(immunizationCriteria);

		if (immunizations.isEmpty()) {
			return false;
		}

		if (immunizations.size() == 1) {
			vaccination.setImmunization(immunizations.get(0));
			return true;
		}

		// Case 1: If the vaccination date is empty, add the vaccination to the latest immunization
		if (vaccination.getVaccinationDate() == null) {
			immunizations.sort(Comparator.comparing(i -> ImmunizationEntityHelper.getDateForComparison(i, true)));
			vaccination.setImmunization(immunizations.get(immunizations.size() - 1));
			return true;
		}

		// Case 2: Search for an immunization with start date < vaccination date < end date
		Optional<Immunization> immunization = immunizations.stream()
			.filter(
				i -> i.getStartDate() != null
					&& i.getEndDate() != null
					&& DateHelper.isBetween(vaccination.getVaccinationDate(), i.getStartDate(), i.getEndDate()))
			.findFirst();
		if (immunization.isPresent()) {
			vaccination.setImmunization(immunization.get());
			return true;
		}

		// Case 3: Search for the immunization with the nearest end or start date to the vaccination date
		immunization = immunizations.stream().filter(i -> i.getEndDate() != null || i.getStartDate() != null).min((i1, i2) -> {
			Integer i1Interval =
				Math.abs(DateHelper.getDaysBetween(i1.getEndDate() != null ? i1.getEndDate() : i1.getStartDate(), vaccination.getVaccinationDate()));
			Integer i2Interval =
				Math.abs(DateHelper.getDaysBetween(i2.getEndDate() != null ? i2.getEndDate() : i2.getStartDate(), vaccination.getVaccinationDate()));
			return i1Interval.compareTo(i2Interval);
		});
		if (immunization.isPresent()) {
			vaccination.setImmunization(immunization.get());
			return true;
		}

		// Case 4: Use the immunization with the nearest report date to the vaccination date
		immunization = immunizations.stream().min((i1, i2) -> {
			Integer i1Interval = Math.abs(DateHelper.getDaysBetween(i1.getReportDate(), vaccination.getVaccinationDate()));
			Integer i2Interval = Math.abs(DateHelper.getDaysBetween(i2.getReportDate(), vaccination.getVaccinationDate()));
			return i1Interval.compareTo(i2Interval);
		});
		if (immunization.isPresent()) {
			vaccination.setImmunization(immunization.get());
			return true;
		}

		return false;
	}

	private Immunization buildImmunizationFromRootEntity() {

		Person immunizationPerson = null;
		Disease immunizationDisease = null;
		Region immunizationRegion = null;
		District immunizationDistrict = null;

		if (caseUuid != null) {
			Case caze = DatabaseHelper.getCaseDao().queryUuid(caseUuid);
			immunizationPerson = caze.getPerson();
			immunizationDisease = caze.getDisease();
			immunizationRegion = caze.getResponsibleRegion();
			immunizationDistrict = caze.getResponsibleDistrict();
		}

		if (contactUuid != null) {
			Contact contact = DatabaseHelper.getContactDao().queryUuid(contactUuid);
			immunizationPerson = contact.getPerson();
			immunizationDisease = contact.getDisease();
			if (contact.getRegion() == null || contact.getDistrict() == null) {
				Case sourceCase = DatabaseHelper.getCaseDao().queryUuid(contact.getCaseUuid());
				immunizationRegion = sourceCase.getResponsibleRegion();
				immunizationDistrict = sourceCase.getResponsibleDistrict();
			} else {
				immunizationRegion = contact.getRegion();
				immunizationDistrict = contact.getDistrict();
			}
		}

		if (eventParticipantUuid != null) {
			EventParticipant eventParticipant = DatabaseHelper.getEventParticipantDao().queryUuid(eventParticipantUuid);
			immunizationPerson = eventParticipant.getPerson();
			immunizationDisease = eventParticipant.getEvent().getDisease();
			if (eventParticipant.getResponsibleRegion() == null || eventParticipant.getResponsibleDistrict() == null) {
				immunizationRegion = eventParticipant.getEvent().getEventLocation().getRegion();
				immunizationDistrict = eventParticipant.getEvent().getEventLocation().getDistrict();
			} else {
				immunizationRegion = eventParticipant.getResponsibleRegion();
				immunizationDistrict = eventParticipant.getResponsibleDistrict();
			}
		}

		final Immunization newImmunization = DatabaseHelper.getImmunizationDao().build(immunizationPerson);
		newImmunization.setDisease(immunizationDisease);
		newImmunization.setResponsibleRegion(immunizationRegion);
		newImmunization.setResponsibleDistrict(immunizationDistrict);
		newImmunization.setImmunizationManagementStatus(ImmunizationManagementStatus.COMPLETED);
		newImmunization.setImmunizationStatus(ImmunizationStatus.ACQUIRED);
		newImmunization.setMeansOfImmunization(MeansOfImmunization.VACCINATION);
		newImmunization.setReportDate(new Date());

		return newImmunization;
	}

	private void createImmunization(Immunization immunization) {
		try {
			DatabaseHelper.getImmunizationDao().saveAndSnapshot(immunization);
		} catch (DaoException e) {
			e.printStackTrace();
		}
		vaccination.setImmunization(immunization);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (saveTask != null && !saveTask.isCancelled())
			saveTask.cancel(true);
	}
}
