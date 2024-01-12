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

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.immunization.Immunization;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.immunization.ImmunizationPickOrCreateDialog;
import de.symeda.sormas.app.immunization.ImmunizationSection;
import de.symeda.sormas.app.immunization.list.ImmunizationListActivity;
import de.symeda.sormas.app.person.SelectOrCreatePersonDialog;
import de.symeda.sormas.app.util.Bundler;

public class ImmunizationNewActivity extends BaseEditActivity<Immunization> {

	private AsyncTask saveTask;

	private String caseUuid;
	private String contactUuid;
	private String eventParticipantUuid;

	public static void startActivity(Context context) {
		BaseEditActivity.startActivity(context, ImmunizationNewActivity.class, buildBundle());
	}

	public static void startActivityFromCase(Context fromActivity, String caseUuid) {
		BaseEditActivity.startActivity(fromActivity, ImmunizationNewActivity.class, buildBundleWithCase(caseUuid));
	}

	public static void startActivityFromContact(Context fromActivity, String contactUuid) {
		BaseEditActivity.startActivity(fromActivity, ImmunizationNewActivity.class, buildBundleWithContact(contactUuid));
	}

	public static void startActivityFromEventParticipant(Context fromActivity, String eventParticipantUuid) {
		BaseEditActivity.startActivity(fromActivity, ImmunizationNewActivity.class, buildBundleWithEventParticipant(eventParticipantUuid));
	}

	public static Bundler buildBundle() {
		return BaseEditActivity.buildBundle(null);
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
	protected void onCreateInner(Bundle savedInstanceState) {
		super.onCreateInner(savedInstanceState);
		Bundler bundler = new Bundler(savedInstanceState);
		caseUuid = bundler.getCaseUuid();
		contactUuid = bundler.getContactUuid();
		eventParticipantUuid = bundler.getEventParticipantUuid();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		Bundler bundler = new Bundler(outState);
		bundler.setCaseUuid(caseUuid);
		bundler.setContactUuid(contactUuid);
		bundler.setEventUuid(eventParticipantUuid);
	}

	@Override
	protected Immunization queryRootEntity(String recordUuid) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Immunization buildRootEntity() {

		final Immunization immunization;
		if (!DataHelper.isNullOrEmpty(caseUuid)) {
			Case _case = DatabaseHelper.getCaseDao().queryUuidBasic(caseUuid);
			immunization = DatabaseHelper.getImmunizationDao().build(_case.getPerson());
		} else if (!DataHelper.isNullOrEmpty(contactUuid)) {
			Contact _contact = DatabaseHelper.getContactDao().queryUuid(contactUuid);
			immunization = DatabaseHelper.getImmunizationDao().build(_contact.getPerson());
		} else if (!DataHelper.isNullOrEmpty(eventParticipantUuid)) {
			EventParticipant _eventP = DatabaseHelper.getEventParticipantDao().queryUuid(eventParticipantUuid);
			immunization = DatabaseHelper.getImmunizationDao().build(_eventP.getPerson());
			immunization.setDisease(_eventP.getEvent().getDisease());
		} else {
			final Person person = DatabaseHelper.getPersonDao().build();
			immunization = DatabaseHelper.getImmunizationDao().build(person);
		}

		return immunization;
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Immunization activityRootData) {
		BaseEditFragment fragment;

		if (!DataHelper.isNullOrEmpty(caseUuid)) {
			fragment = ImmunizationNewFragment.newInstanceFromCase(activityRootData, caseUuid);
		} else if (!DataHelper.isNullOrEmpty(contactUuid)) {
			fragment = ImmunizationNewFragment.newInstanceFromContact(activityRootData, contactUuid);
		} else if (!DataHelper.isNullOrEmpty(eventParticipantUuid)) {
			fragment = ImmunizationNewFragment.newInstanceFromEventParticipant(activityRootData, eventParticipantUuid);
		} else {
			fragment = ImmunizationNewFragment.newInstance(activityRootData);
		}

		fragment.setLiveValidationDisabled(true);
		return fragment;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_immunization_new;
	}

	@Override
	public void replaceFragment(BaseEditFragment f, boolean allowBackNavigation) {
		super.replaceFragment(f, allowBackNavigation);
		getActiveFragment().setLiveValidationDisabled(true);
	}

	@Override
	public void saveData() {
		if (saveTask != null) {
			// don't save multiple times
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
			return;
		}

		final Immunization immunization = getStoredRootEntity();
		ImmunizationNewFragment fragment = (ImmunizationNewFragment) getActiveFragment();
		fragment.setLiveValidationDisabled(false);

		try {
			FragmentValidator.validate(getContext(), fragment.getContentBinding());
		} catch (ValidationException e) {
			NotificationHelper.showNotification(this, ERROR, e.getMessage());
			return;
		}

		// Person selection can be skipped if the immunization was created from a case, contact or event participant
		if (caseUuid != null || contactUuid != null || eventParticipantUuid != null) {
			pickOrCreateImmunizationAndSave(immunization, fragment);
		} else {
			SelectOrCreatePersonDialog.selectOrCreatePerson(immunization.getPerson(), immunization, person -> {
				if (person != null) {
					immunization.setPerson(person);
					pickOrCreateImmunizationAndSave(immunization, fragment);
				}
			});
		}
	}

	private void pickOrCreateImmunizationAndSave(Immunization immunization, ImmunizationNewFragment fragment) {
		ImmunizationPickOrCreateDialog.pickOrCreateImmunization(immunization, pickedImmunization -> {
			if (pickedImmunization != null) {
				if (saveTask != null) {
					// don't save multiple times
					NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
					return;
				}
				if (pickedImmunization.getUuid().equals(immunization.getUuid())) {
					saveTask = new SavingAsyncTask(getRootView(), immunization) {

						@Override
						protected void onPreExecute() {
							showPreloader();
						}

						@Override
						protected void doInBackground(TaskResultHolder resultHolder) throws Exception {
							DatabaseHelper.getPersonDao().saveAndSnapshot(immunization.getPerson());
							DatabaseHelper.getImmunizationDao().saveAndSnapshot(immunization);
						}

						@Override
						protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
							hidePreloader();
							if (taskResult.getResultStatus().isSuccess()) {
								finish();
								ImmunizationEditActivity.startActivity(getContext(), immunization.getUuid(), ImmunizationSection.IMMUNIZATION_INFO);
							}

							// do after clearing, because we want to show a success notification that would otherwise be hidden immediately
							super.onPostExecute(taskResult);

							saveTask = null;
						}
					}.executeOnThreadPool();
				} else {
					saveTask = new SavingAsyncTask(getRootView(), immunization) {

						@Override
						protected void onPreExecute() {
							showPreloader();
						}

						@Override
						protected void doInBackground(TaskResultHolder resultHolder) throws Exception {
							pickedImmunization.update(immunization);
							DatabaseHelper.getImmunizationDao().saveAndSnapshot(pickedImmunization);
						}

						@Override
						protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
							hidePreloader();
							if (taskResult.getResultStatus().isSuccess()) {
								finish();
								ImmunizationEditActivity
									.startActivity(getContext(), pickedImmunization.getUuid(), ImmunizationSection.IMMUNIZATION_INFO);
							}

							// do after clearing, because we want to show a success notification that would otherwise be hidden immediately
							super.onPostExecute(taskResult);

							saveTask = null;
						}
					}.executeOnThreadPool();
				}
			} else {
				finish();
				ImmunizationListActivity.startActivity(getContext());
			}
		});
	}

	@Override
	public Enum getPageStatus() {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (saveTask != null && !saveTask.isCancelled()) {
			saveTask.cancel(true);
		}
	}
}
