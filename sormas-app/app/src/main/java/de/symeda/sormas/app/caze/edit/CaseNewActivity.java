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

package de.symeda.sormas.app.caze.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.caze.CasePickOrCreateDialog;
import de.symeda.sormas.app.caze.CaseSection;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.util.Bundler;
import de.symeda.sormas.app.util.LocationService;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

public class CaseNewActivity extends BaseEditActivity<Case> {

    public static final String TAG = CaseNewActivity.class.getSimpleName();

    private AsyncTask saveTask;

    private boolean emptyReportDate;
    private String contactUuid;
    private String eventParticipantUuid;
    private String newSubHeading;

    private List<Disease> lineListingDiseases;

    public static void startActivity(Context fromActivity) {
        BaseEditActivity.startActivity(fromActivity, CaseNewActivity.class, buildBundle());
    }

    public static void startActivityWithEmptyReportDate(Context fromActivity) {
        BaseEditActivity.startActivity(fromActivity, CaseNewActivity.class, buildBundleWithEmptyReportDate());
    }

    public static void startActivityFromContact(Context fromActivity, String contactUuid) {
        BaseEditActivity.startActivity(fromActivity, CaseNewActivity.class, buildBundleWithContact(contactUuid));
    }

    public static void startActivityFromEventPerson(Context fromActivity, String eventParticipantUuid) {
        BaseEditActivity.startActivity(fromActivity, CaseNewActivity.class, buildBundleWithEventParticipant(eventParticipantUuid));
    }

    public static Bundler buildBundle() {
        return BaseEditActivity.buildBundle(null);
    }

    public static Bundler buildBundleWithEmptyReportDate() {
        return BaseEditActivity.buildBundle(null).setEmptyReportDate(true);
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
        contactUuid = bundler.getContactUuid();
        eventParticipantUuid = bundler.getEventParticipantUuid();
        emptyReportDate = bundler.getEmptyReportDate();

        lineListingDiseases = DatabaseHelper.getFeatureConfigurationDao().getDiseasesWithLineListing();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundler bundler = new Bundler(outState);
        bundler.setContactUuid(contactUuid);
        bundler.setEventParticipantUuid(eventParticipantUuid);
        bundler.setEmptyReportDate(emptyReportDate);
    }

    @Override
    public CaseClassification getPageStatus() {
        return null;
    }

    @Override
    protected Case queryRootEntity(String recordUuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Case buildRootEntity() {
        Person _person;
        Case _case;
        if (!DataHelper.isNullOrEmpty(contactUuid)) {
            Contact sourceContact = DatabaseHelper.getContactDao().queryUuid(contactUuid);
            _person = sourceContact.getPerson();
            _case = DatabaseHelper.getCaseDao().build(_person,
                    DatabaseHelper.getCaseDao().queryUuidBasic(sourceContact.getCaseUuid()));
        } else if (!DataHelper.isNullOrEmpty(eventParticipantUuid)) {
            EventParticipant eventParticipant = DatabaseHelper.getEventParticipantDao().queryUuid(eventParticipantUuid);
            _case = DatabaseHelper.getCaseDao().build(eventParticipant);
        } else {
            _person = DatabaseHelper.getPersonDao().build();
            _case = DatabaseHelper.getCaseDao().build(_person);
        }

        if (emptyReportDate) {
            _case.setReportDate(null);
        }

        return _case;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_case);
        return result;
    }

    @Override
    protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Case activityRootData) {
        BaseEditFragment fragment;
        if (contactUuid != null && eventParticipantUuid == null) {
            fragment = CaseNewFragment.newInstanceFromContact(activityRootData, contactUuid);
        } else if (eventParticipantUuid != null && contactUuid == null) {
            fragment = CaseNewFragment.newInstanceFromEventParticipant(activityRootData, eventParticipantUuid);
        } else {
            fragment = CaseNewFragment.newInstance(activityRootData);
        }
        fragment.setLiveValidationDisabled(true);
        return fragment;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_case_new;
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

        final Case caze = getStoredRootEntity();
        CaseNewFragment fragment = (CaseNewFragment) getActiveFragment();

        fragment.setLiveValidationDisabled(false);

        try {
            FragmentValidator.validate(getContext(), fragment.getContentBinding());
        } catch (ValidationException e) {
            NotificationHelper.showNotification(this, ERROR, e.getMessage());
            return;
        }

        CasePickOrCreateDialog.pickOrCreateCase(caze, pickedCase -> {
            if (pickedCase.getUuid().equals(caze.getUuid())) {
                saveDataInner(caze);
            } else {
                if (lineListingDiseases.contains(caze.getDisease()) && Boolean.TRUE.equals(fragment.getContentBinding().rapidCaseEntryCheckBox.getValue())) {
                    setStoredRootEntity(buildRootEntity());
                    fragment.setActivityRootData(getStoredRootEntity());
                    fragment.updateForRapidCaseEntry(caze);
                } else{
                    finish();
                    CaseEditActivity.startActivity(getContext(), pickedCase.getUuid(), CaseSection.CASE_INFO);
                }
            }
        });
    }

    private void saveDataInner(final Case caseToSave) {

        if (saveTask != null) {
            NotificationHelper.showNotification(this, WARNING, getString(R.string.message_already_saving));
            return; // don't save multiple times
        }

        saveTask = new SavingAsyncTask(getRootView(), caseToSave) {
            @Override
            protected void onPreExecute() {
                showPreloader();
            }

            @Override
            protected void doInBackground(TaskResultHolder resultHolder) throws Exception {
                //set current address when person address is null
                if (caseToSave.getPerson().getAddress().getLatitude() == null || caseToSave.getPerson().getAddress().getLongitude() == null) {
                    android.location.Location phoneLocation = LocationService.instance().getLocation(CaseNewActivity.this);
                    if (phoneLocation != null) {
                        caseToSave.getPerson().getAddress().setLatitude(phoneLocation.getLatitude());
                        caseToSave.getPerson().getAddress().setLongitude(phoneLocation.getLongitude());
                        caseToSave.getPerson().getAddress().setLatLonAccuracy(phoneLocation.getAccuracy());
                    }
                }
                DatabaseHelper.getPersonDao().saveAndSnapshot(caseToSave.getPerson());

                // epid number
                if (StringUtils.isBlank(caseToSave.getEpidNumber())) {
                    Calendar calendar = Calendar.getInstance();
                    String year = String.valueOf(calendar.get(Calendar.YEAR)).substring(2);
                    caseToSave.setEpidNumber(caseToSave.getRegion().getEpidCode() != null ? caseToSave.getRegion().getEpidCode() : ""
                            + "-" + caseToSave.getDistrict().getEpidCode() != null ? caseToSave.getDistrict().getEpidCode() : ""
                            + "-" + year + "-");
                }

                DatabaseHelper.getCaseDao().saveAndSnapshot(caseToSave);

                if (!DataHelper.isNullOrEmpty(contactUuid)) {
                        Contact sourceContact = DatabaseHelper.getContactDao().queryUuid(contactUuid);
                        sourceContact.setResultingCaseUuid(caseToSave.getUuid());
                        sourceContact.setResultingCaseUser(ConfigProvider.getUser());
                        DatabaseHelper.getContactDao().saveAndSnapshot(sourceContact);
                    }

                    if (!DataHelper.isNullOrEmpty(eventParticipantUuid)) {
                        EventParticipant eventParticipant = DatabaseHelper.getEventParticipantDao().queryUuid(eventParticipantUuid);
                    eventParticipant.setResultingCaseUuid(caseToSave.getUuid());
                    DatabaseHelper.getEventParticipantDao().saveAndSnapshot(eventParticipant);
                }
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                hidePreloader();
                if (taskResult.getResultStatus().isSuccess()) {

                    CaseNewFragment fragment = (CaseNewFragment) getActiveFragment();
                    if (lineListingDiseases.contains(caseToSave.getDisease()) && Boolean.TRUE.equals(fragment.getContentBinding().rapidCaseEntryCheckBox.getValue())) {
                        setStoredRootEntity(buildRootEntity());
                        fragment.setActivityRootData(getStoredRootEntity());
                        fragment.updateForRapidCaseEntry(caseToSave);
                        setNewSubHeading(caseToSave.getPerson());
                    } else {
                        finish();
                        CaseEditActivity.startActivity(getContext(), caseToSave.getUuid(), CaseSection.CASE_INFO);
                    }
                }

                // do after clearing, because we want to show a success notification that would otherwise be hidden immediately
                super.onPostExecute(taskResult);

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

    public List<Disease> getLineListingDiseases() {
        return lineListingDiseases;
    }

    @Override
    public void updateSubHeadingTitle(String title) {
        if (newSubHeading != null) {
            setSubHeadingTitle(newSubHeading);
            newSubHeading = null;
        } else {
            setSubHeadingTitle(title);
        }
    }

    void setNewSubHeading(Person person) {
        StringBuilder lastCaseText = new StringBuilder();
        lastCaseText.append(getResources().getString(R.string.caption_last_case)).append(": ").append(person.getFirstName()).append(" ").append(person.getLastName());
        String dobText = PersonHelper.getAgeAndBirthdateString(person.getApproximateAge(), person.getApproximateAgeType(), person.getBirthdateDD(), person.getBirthdateMM(), person.getBirthdateYYYY());
        if (!DataHelper.isNullOrEmpty(dobText)){
            lastCaseText.append(" | ").append(dobText);
        }
        if (person.getSex() != null){
            lastCaseText.append(" | ").append(person.getSex());
        }

        newSubHeading = lastCaseText.toString();
    }

}