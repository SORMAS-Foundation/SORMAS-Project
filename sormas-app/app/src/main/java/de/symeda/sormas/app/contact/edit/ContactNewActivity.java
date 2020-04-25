/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.contact.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import java.util.Date;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.contact.ContactSection;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.person.SelectOrCreatePersonDialog;
import de.symeda.sormas.app.util.Bundler;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.LocationService;
import de.symeda.sormas.app.util.NavigationHelper;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

public class ContactNewActivity extends BaseEditActivity<Contact> {

    public static final String TAG = ContactNewActivity.class.getSimpleName();

    private AsyncTask saveTask;
    private String caseUuid = null;

    public static <TActivity extends BaseActivity> void startActivity(Context context, String caseUuid) {
        BaseEditActivity.startActivity(context, ContactNewActivity.class, buildBundle(caseUuid));
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
    protected Contact queryRootEntity(String recordUuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Contact buildRootEntity() {
        Person _person = DatabaseHelper.getPersonDao().build();
        Contact _contact = DatabaseHelper.getContactDao().build();

        // not null, because contact can only be created when the user has access to the case
        if (caseUuid != null) {
            Case contactCase = DatabaseHelper.getCaseDao().queryUuidBasic(caseUuid);
            _contact.setCaseUuid(caseUuid);
            _contact.setDisease(contactCase.getDisease());
            _contact.setDiseaseDetails(contactCase.getDiseaseDetails());
        }

        _contact.setPerson(_person);
        _contact.setReportDateTime(new Date());
        _contact.setContactClassification(ContactClassification.UNCONFIRMED);
        _contact.setContactStatus(ContactStatus.ACTIVE);
        _contact.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
        _contact.setReportingUser(ConfigProvider.getUser());

        return _contact;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_contact);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && caseUuid == null) {
            NavigationHelper.goToContacts(getContext());
            finish();
        } else {
            super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public ContactClassification getPageStatus() {
        return null;
    }

    @Override
    protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Contact activityRootData) {
        BaseEditFragment fragment = ContactNewFragment.newInstance(activityRootData);
        fragment.setLiveValidationDisabled(true);
        return fragment;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_contact_new;
    }

    @Override
    public void replaceFragment(BaseEditFragment f, boolean allowBackNavigation) {
        super.replaceFragment(f, allowBackNavigation);
        getActiveFragment().setLiveValidationDisabled(true);
    }

    @Override
    public void saveData() {

        if (saveTask != null) {
            NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
            return; // don't save multiple times
        }

        final Contact contactToSave = getStoredRootEntity();

        ContactNewFragment fragment = (ContactNewFragment) getActiveFragment();
        fragment.setLiveValidationDisabled(false);

        try {
            FragmentValidator.validate(getContext(), fragment.getContentBinding());
        } catch (ValidationException e) {
            NotificationHelper.showNotification(this, ERROR, e.getMessage());
            return;
        }

        SelectOrCreatePersonDialog.selectOrCreatePerson(contactToSave.getPerson(), new Consumer<Person>() {
            @Override
            public void accept(Person person) {
                contactToSave.setPerson(person);

                saveTask = new SavingAsyncTask(getRootView(), contactToSave) {
                    @Override
                    protected void onPreExecute() {
                        showPreloader();
                    }

                    @Override
                    protected void doInBackground(TaskResultHolder resultHolder) throws Exception {
                        if (contactToSave.getRelationToCase() == ContactRelation.SAME_HOUSEHOLD && contactToSave.getCaseUuid() != null
                                && contactToSave.getPerson().getAddress().isEmptyLocation()) {
                            Case contactCase = DatabaseHelper.getCaseDao().queryUuidBasic(contactToSave.getCaseUuid());
                            if (contactCase != null) {
                                contactToSave.getPerson().getAddress().setRegion(contactCase.getRegion());
                                contactToSave.getPerson().getAddress().setDistrict(contactCase.getDistrict());
                                contactToSave.getPerson().getAddress().setCommunity(contactCase.getCommunity());
                            }
                        }

                        //set current address when person address is null
                        if (contactToSave.getPerson().getAddress().getLatitude() == null || contactToSave.getPerson().getAddress().getLongitude() == null) {
                            android.location.Location phoneLocation = LocationService.instance().getLocation(ContactNewActivity.this);
                            if (phoneLocation != null) {
                                contactToSave.getPerson().getAddress().setLatitude(phoneLocation.getLatitude());
                                contactToSave.getPerson().getAddress().setLongitude(phoneLocation.getLongitude());
                                contactToSave.getPerson().getAddress().setLatLonAccuracy(phoneLocation.getAccuracy());
                            }
                        }

                        DatabaseHelper.getPersonDao().saveAndSnapshot(contactToSave.getPerson());
                        DatabaseHelper.getContactDao().saveAndSnapshot(contactToSave);
                    }

                    @Override
                    protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                        hidePreloader();
                        super.onPostExecute(taskResult);
                        if (taskResult.getResultStatus().isSuccess()) {
                            finish();
                            ContactEditActivity.startActivity(getContext(), contactToSave.getUuid(), ContactSection.PERSON_INFO);
                        }
                        saveTask = null;
                    }
                }.executeOnThreadPool();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }
}
