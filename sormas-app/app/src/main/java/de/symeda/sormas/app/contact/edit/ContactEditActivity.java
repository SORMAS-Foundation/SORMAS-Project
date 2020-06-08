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

package de.symeda.sormas.app.contact.edit;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactEditAuthorization;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.contact.ContactSection;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.person.edit.PersonEditFragment;
import de.symeda.sormas.app.task.edit.TaskNewActivity;
import de.symeda.sormas.app.util.Bundler;
import de.symeda.sormas.app.visit.edit.VisitNewActivity;

public class ContactEditActivity extends BaseEditActivity<Contact> {

	public static final String TAG = ContactEditActivity.class.getSimpleName();

	private AsyncTask saveTask;

	public static void startActivity(Context context, String rootUuid, ContactSection section) {
		BaseActivity.startActivity(context, ContactEditActivity.class, buildBundle(rootUuid, section));
	}

	public static Bundler buildBundle(String rootUuid, ContactSection section) {
		return buildBundle(rootUuid, section.ordinal());
	}

	@Override
	protected Contact queryRootEntity(String recordUuid) {
		return DatabaseHelper.getContactDao().queryUuid(recordUuid);
	}

	@Override
	protected Contact buildRootEntity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		return PageMenuItem.fromEnum(ContactSection.values(), getContext());
	}

	@Override
	public ContactClassification getPageStatus() {
		return getStoredRootEntity() == null ? null : getStoredRootEntity().getContactClassification();
	}

	@Override
	protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Contact activityRootData) {
		ContactSection section = ContactSection.fromOrdinal(menuItem.getPosition());
		BaseEditFragment fragment;
		switch (section) {
		case CONTACT_INFO:
			fragment = ContactEditFragment.newInstance(activityRootData);
			break;
		case PERSON_INFO:
			fragment = PersonEditFragment.newInstance(activityRootData);
			break;
		case VISITS:
			fragment = ContactEditVisitsListFragment.newInstance(activityRootData);
			break;
		case TASKS:
			fragment = ContactEditTaskListFragment.newInstance(activityRootData);
			break;
		default:
			throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
		}
		return fragment;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getSaveMenu().setTitle(R.string.action_save_contact);

		return true;
	}

	@Override
	public void saveData() {

		if (saveTask != null) {
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
			return; // don't save multiple times
		}

		final Contact contactToSave = getStoredRootEntity();

		if (ContactEditAuthorization.isContactEditAllowed(contactToSave)) {
			try {
				FragmentValidator.validate(getContext(), getActiveFragment().getContentBinding());
			} catch (ValidationException e) {
				NotificationHelper.showNotification(this, ERROR, e.getMessage());
				return;
			}

			saveTask = new SavingAsyncTask(getRootView(), contactToSave) {

				@Override
				public void doInBackground(TaskResultHolder resultHolder) throws DaoException {
					DatabaseHelper.getPersonDao().saveAndSnapshot(contactToSave.getPerson());
					DatabaseHelper.getContactDao().saveAndSnapshot(contactToSave);
				}

				@Override
				protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
					super.onPostExecute(taskResult);

					if (taskResult.getResultStatus().isSuccess()) {
						if (getActivePage().getPosition() == ContactSection.PERSON_INFO.ordinal()) {
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
		} else {
			NotificationHelper.showNotification(this, WARNING, getString(R.string.message_edit_forbidden));
		}
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_contact_edit;
	}

	@Override
	public void goToNewView() {
		ContactSection activeSection = ContactSection.fromOrdinal(getActivePage().getPosition());
		switch (activeSection) {
		case VISITS:
			VisitNewActivity.startActivity(this, getRootUuid());
			break;
		case TASKS:
			TaskNewActivity.startActivityFromContact(getContext(), getRootUuid());
			break;
		default:
			throw new IllegalArgumentException(activeSection.toString());
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (saveTask != null && !saveTask.isCancelled())
			saveTask.cancel(true);
	}
}
