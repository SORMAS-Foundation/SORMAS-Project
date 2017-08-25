package de.symeda.sormas.app.caze;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.location.LocationDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleDao;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.AbstractEditTabActivity;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskDao;
import de.symeda.sormas.app.component.HelpDialog;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.contact.ContactNewActivity;
import de.symeda.sormas.app.contact.ContactsListFragment;
import de.symeda.sormas.app.databinding.CaseDataFragmentLayoutBinding;
import de.symeda.sormas.app.databinding.CaseSymptomsFragmentLayoutBinding;
import de.symeda.sormas.app.databinding.PersonEditFragmentLayoutBinding;
import de.symeda.sormas.app.person.PersonEditForm;
import de.symeda.sormas.app.sample.SampleEditActivity;
import de.symeda.sormas.app.sample.SamplesListFragment;
import de.symeda.sormas.app.task.TaskForm;
import de.symeda.sormas.app.task.TasksListFragment;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.validation.CaseValidator;
import de.symeda.sormas.app.validation.PersonValidator;
import de.symeda.sormas.app.validation.SymptomsValidator;

public class CaseEditActivity extends AbstractEditTabActivity {

    public static final String KEY_CASE_UUID = "caseUuid";
    public static final String CASE_SUBTITLE = "caseSubtitle";

    private CaseEditPagerAdapter adapter;
    private String caseUuid;
    private String taskUuid;
    private Toolbar toolbar;

    @Override
    public boolean isEditing() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.case_edit_activity_layout);

        // This makes sure that the given amount of tabs is kept in memory, which means that
        // Android doesn't call onResume when the tab has no focus which would otherwise lead
        // to certain spinners not displaying their values
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(CaseEditTabs.values().length);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setTitle(getResources().getText(R.string.headline_case) + " - " + ConfigProvider.getUser().getUserRole().toShortString());
        }

        Bundle params = getIntent().getExtras();
        if (params != null) {
            if (params.containsKey(KEY_CASE_UUID)) {
                caseUuid = params.getString(KEY_CASE_UUID);
                Case initialEntity = DatabaseHelper.getCaseDao().queryUuid(caseUuid);
                DatabaseHelper.getCaseDao().markAsRead(initialEntity);
            }
            if (params.containsKey(TaskForm.KEY_TASK_UUID)) {
                taskUuid = params.getString(TaskForm.KEY_TASK_UUID);
            }
            if (params.containsKey(KEY_PAGE)) {
                currentTab = params.getInt(KEY_PAGE);
            }
            if (params.containsKey(CASE_SUBTITLE) && toolbar != null) {
                getSupportActionBar().setSubtitle(params.getString(CASE_SUBTITLE));
            }
        }

        setAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Case currentEntity = DatabaseHelper.getCaseDao().queryUuid(caseUuid);
        if (currentEntity.isUnreadOrChildUnread()) {
            // Resetting the adapter will reload the form and therefore also override any unsaved changes
            DatabaseHelper.getCaseDao().markAsRead(currentEntity);
            setAdapter();

            final Snackbar snackbar = Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_entity_overridden), getResources().getString(R.string.entity_case)), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.snackbar_okay, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Bundle params = getIntent().getExtras();
        if (params != null) {
            if (params.containsKey(KEY_CASE_UUID)) {
                outState.putString(KEY_CASE_UUID, caseUuid);
            }
            if (params.containsKey(TaskForm.KEY_TASK_UUID)) {
                outState.putString(TaskForm.KEY_TASK_UUID, taskUuid);
            }
            if (params.containsKey(KEY_PAGE)) {
                outState.putInt(KEY_PAGE, currentTab);
            }
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_action_bar, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        CaseEditTabs tab = CaseEditTabs.values()[currentTab];
        switch (tab) {
            case CASE_DATA:
                updateActionBarGroups(menu, false, false, true, false, true);
                break;

            case PATIENT:
                updateActionBarGroups(menu, false, false, true, false, true);
                break;

            case SYMPTOMS:
                updateActionBarGroups(menu, true, false, true, false, true);
                break;

            case CONTACTS:
                updateActionBarGroups(menu, false, true, true, true, false);
                break;

            case TASKS:
                updateActionBarGroups(menu, false, true, true, false, false);
                break;

            case SAMPLES:
                updateActionBarGroups(menu, false, true, true, true, false);
                break;

            case HOSPITALIZATION:
                updateActionBarGroups(menu, false, false, true, false, true);
                break;

            case EPIDATA:
                updateActionBarGroups(menu, false, false, true, false, true);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setCurrentTab(pager.getCurrentItem());
        CaseEditTabs tab = CaseEditTabs.values()[currentTab];
        Case caze = (Case) getData(CaseEditTabs.CASE_DATA.ordinal());
        CaseDao caseDao = DatabaseHelper.getCaseDao();
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if (taskUuid != null) {
                    finish();
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }

                return true;

            // Help button
            case R.id.action_help:
                HelpDialog helpDialog = new HelpDialog(this);

                switch (tab) {
                    case SYMPTOMS:
                        String helpText = HelpDialog.getHelpForForm((LinearLayout) this.findViewById(R.id.case_symptoms_form));
                        helpDialog.setMessage(Html.fromHtml(helpText).toString());
                        break;
                }

                helpDialog.show();

                return true;

            case R.id.action_markAllAsRead:
                switch (tab) {
                    case CONTACTS:
                        ContactDao contactDao = DatabaseHelper.getContactDao();
                        PersonDao personDao = DatabaseHelper.getPersonDao();
                        List<Contact> contacts = contactDao.getByCase(caze);
                        for (Contact contactToMark : contacts) {
                            contactDao.markAsRead(contactToMark);
                        }

                        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                            if (fragment instanceof ContactsListFragment) {
                                fragment.onResume();
                            }
                        }
                        break;
                    case SAMPLES:
                        SampleDao sampleDao = DatabaseHelper.getSampleDao();
                        List<Sample> samples = sampleDao.queryByCase(caze);
                        for (Sample sampleToMark : samples) {
                            sampleDao.markAsRead(sampleToMark);
                        }

                        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                            if (fragment instanceof SamplesListFragment) {
                                fragment.onResume();
                            }
                        }
                        break;
                    case TASKS:
                        TaskDao taskDao = DatabaseHelper.getTaskDao();
                        List<Task> tasks = taskDao.queryByCase(caze);
                        for (Task taskToMark : tasks) {
                            taskDao.markAsRead(taskToMark);
                        }

                        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                            if (fragment instanceof TasksListFragment) {
                                fragment.onResume();
                            }
                        }
                        break;
                }
                return true;

            // Report problem button
            case R.id.action_report:
                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName() + ":" + tab.toString(), caze.getUuid());
                AlertDialog dialog = userReportDialog.create();
                dialog.show();

                return true;

            // Save button
            case R.id.action_save:
                // PATIENT
                LocationDao locLocationDao = DatabaseHelper.getLocationDao();
                PersonDao personDao = DatabaseHelper.getPersonDao();
                Person person = (Person) getData(CaseEditTabs.PATIENT.ordinal());

                // SYMPTOMS
                Symptoms symptoms = (Symptoms) getData(CaseEditTabs.SYMPTOMS.ordinal());

                // HOSPITALIZATION
                Hospitalization hospitalization = (Hospitalization) getData(CaseEditTabs.HOSPITALIZATION.ordinal());

                // EPI DATA
                EpiData epiData = (EpiData) getData(CaseEditTabs.EPIDATA.ordinal());

                // CASE_DATA
                caze = (Case) getData(CaseEditTabs.CASE_DATA.ordinal());

                // Validations have to be processed from last tab to first to make sure that the user will be re-directed
                // to the first tab with a validation error
                PersonEditFragmentLayoutBinding personBinding =  ((PersonEditForm)getTabByPosition(CaseEditTabs.PATIENT.ordinal())).getBinding();
                CaseSymptomsFragmentLayoutBinding symptomsBinding = ((SymptomsEditForm)getTabByPosition(CaseEditTabs.SYMPTOMS.ordinal())).getBinding();

                // Necessary because the entry could've been automatically set, in which case the setValue method of the
                // custom field has not been called
                symptoms.setOnsetSymptom((String) symptomsBinding.symptomsOnsetSymptom1.getValue());

                PersonValidator.clearErrors(personBinding);
                SymptomsValidator.clearErrorsForSymptoms(symptomsBinding);

                int validationErrorTab = -1;

                if (!SymptomsValidator.validateCaseSymptoms(symptoms, symptomsBinding)) {
                    validationErrorTab = CaseEditTabs.SYMPTOMS.ordinal();
                }
                if (!PersonValidator.validatePersonData(person, personBinding)) {
                    validationErrorTab = CaseEditTabs.PATIENT.ordinal();
                }

                if (validationErrorTab >= 0) {
                    pager.setCurrentItem(validationErrorTab);
                    return true;
                }

                try {
                    personDao.saveAndSnapshot(person);
                    caze.setPerson(person); // we aren't sure why, but this is needed, otherwise the person will be overriden when first saved
                    caze.setSymptoms(symptoms);
                    caze.setHospitalization(hospitalization);
                    caze.setEpiData(epiData);
                    caseDao.saveAndSnapshot(caze);

                    Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_case)), Snackbar.LENGTH_LONG).show();

                    // switch to next tab
                    try {
                        pager.setCurrentItem(currentTab + 1);
                    } catch (NullPointerException e) {
                        pager.setCurrentItem(currentTab);
                    }
                } catch (DaoException e) {
                    Log.e(getClass().getName(), "Error while trying to save case", e);
                    Log.e(getClass().getName(), "- root cause: ", ErrorReportingHelper.getRootCause(e));
                    Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_case)), Snackbar.LENGTH_LONG).show();
                    ErrorReportingHelper.sendCaughtException(tracker, e, caze, true);
                }

                return true;

            // Add button
            case R.id.action_add:
                switch (tab) {
                    case CONTACTS:
                        Bundle contactCreateBundle = new Bundle();
                        contactCreateBundle.putString(KEY_CASE_UUID, caseUuid);
                        Intent intentContactNew = new Intent(this, ContactNewActivity.class);
                        intentContactNew.putExtras(contactCreateBundle);
                        startActivity(intentContactNew);
                        break;
                    case SAMPLES:
                        Bundle sampleCreateBundle = new Bundle();
                        sampleCreateBundle.putString(KEY_CASE_UUID, caseUuid);
                        sampleCreateBundle.putBoolean(SampleEditActivity.NEW_SAMPLE, true);
                        Intent intentSampleNew = new Intent(this, SampleEditActivity.class);
                        intentSampleNew.putExtras(sampleCreateBundle);
                        startActivity(intentSampleNew);
                        break;
                }

                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void setAdapter() {
        adapter = new CaseEditPagerAdapter(getSupportFragmentManager(), caseUuid);
        createTabViews(adapter);

        pager.setCurrentItem(currentTab);
    }

}
