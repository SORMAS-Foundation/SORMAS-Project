package de.symeda.sormas.app.caze;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.PlagueType;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.app.AbstractEditTabActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
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
import de.symeda.sormas.app.backend.symptoms.SymptomsDtoHelper;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskDao;
import de.symeda.sormas.app.component.FacilityChangeDialogBuilder;
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
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.validation.PersonValidator;
import de.symeda.sormas.app.validation.SymptomsValidator;

public class CaseEditActivity extends AbstractEditTabActivity {

    public static final String KEY_CASE_UUID = "caseUuid";

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

        Case initialEntity = null;
        Bundle params = getIntent().getExtras();
        if (params != null) {
            if (params.containsKey(KEY_CASE_UUID)) {
                caseUuid = params.getString(KEY_CASE_UUID);
                initialEntity = DatabaseHelper.getCaseDao().queryUuid(caseUuid);
                // If the case has been removed from the database in the meantime, redirect the user to the cases overview
                // TODO add Snackbar and test
                if (initialEntity == null) {
                    Intent intent = new Intent(this, CasesActivity.class);
                    startActivity(intent);
                    finish();
                }

                if (toolbar != null) {
                    getSupportActionBar().setSubtitle(initialEntity.toString());
                }

                DatabaseHelper.getCaseDao().markAsRead(initialEntity);
            }
            if (params.containsKey(TaskForm.KEY_TASK_UUID)) {
                taskUuid = params.getString(TaskForm.KEY_TASK_UUID);
            }
            if (params.containsKey(KEY_PAGE)) {
                currentTab = params.getInt(KEY_PAGE);
            }
        }

        setAdapter(initialEntity);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Case currentEntity = DatabaseHelper.getCaseDao().queryUuid(caseUuid);
        // If the case has been removed from the database in the meantime, redirect the user to the cases overview
        if (currentEntity == null) {
            Intent intent = new Intent(this, CasesActivity.class);
            startActivity(intent);
            finish();
        }

        if (currentEntity.isUnreadOrChildUnread()) {
            // Resetting the adapter will reload the form and therefore also override any unsaved changes
            DatabaseHelper.getCaseDao().markAsRead(currentEntity);
            setAdapter(currentEntity);

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
        CaseEditTabs tab = adapter.getTabForPosition(currentTab);
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
        CaseEditTabs tab = adapter.getTabForPosition(currentTab);
        final Case caze = (Case) getData(adapter.getPositionOfTab(CaseEditTabs.CASE_DATA));
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
                final Case caseBeforeSaving = DatabaseHelper.getCaseDao().queryUuid(caze.getUuid());
                if (caze.getDisease() == Disease.PLAGUE) {
                    Symptoms symptoms = (Symptoms) getData(adapter.getPositionOfTab(CaseEditTabs.SYMPTOMS));
                    SymptomsDto symptomsDto = new SymptomsDto();
                    new SymptomsDtoHelper().fillInnerFromAdo(symptomsDto, symptoms);

                    final PlagueType newPlagueType = DiseaseHelper.getPlagueTypeForSymptoms(symptomsDto);
                    if (newPlagueType != null && newPlagueType != caze.getPlagueType()) {
                        AlertDialog plagueTypeChangeDialog = buildPlagueTypeChangeDialog(newPlagueType, new Callback() {
                            @Override
                            public void call() {
                                caze.setPlagueType(newPlagueType);
                                saveCase(caze, caseBeforeSaving);
                            }
                        });
                        plagueTypeChangeDialog.show();
                    } else {
                        saveCase(caze, caseBeforeSaving);
                    }
                } else {
                    saveCase(caze, caseBeforeSaving);
                }

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

    public void setAdapter(Case caze) {
        List<CaseEditTabs> visibleTabs;
        CaseDataDto caseDataDto = new CaseDataDto();
        new CaseDtoHelper().fillInnerFromAdo(caseDataDto, caze);
        if (!DiseaseHelper.hasContactFollowUp(caseDataDto)) {
            visibleTabs = Arrays.asList(CaseEditTabs.CASE_DATA, CaseEditTabs.PATIENT,
                    CaseEditTabs.HOSPITALIZATION, CaseEditTabs.SYMPTOMS, CaseEditTabs.EPIDATA,
                    CaseEditTabs.SAMPLES, CaseEditTabs.TASKS);
        } else {
            visibleTabs = Arrays.asList(CaseEditTabs.CASE_DATA, CaseEditTabs.PATIENT,
                    CaseEditTabs.HOSPITALIZATION, CaseEditTabs.SYMPTOMS, CaseEditTabs.EPIDATA,
                    CaseEditTabs.CONTACTS, CaseEditTabs.SAMPLES, CaseEditTabs.TASKS);
        }

        adapter = new CaseEditPagerAdapter(getSupportFragmentManager(), caze, visibleTabs);
        createTabViews(adapter);

        pager.setCurrentItem(currentTab);
    }

    public void moveCase(View v) {
        if (saveCaseToDatabase()) {
            final CaseDataFragmentLayoutBinding caseBinding = ((CaseEditDataForm)getTabByPosition(adapter.getPositionOfTab(CaseEditTabs.CASE_DATA))).getBinding();

            final Consumer positiveCallback = new Consumer() {
                @Override
                public void accept(Object success) {
                    Case updatedCase = DatabaseHelper.getCaseDao().queryUuid(caseBinding.getCaze().getUuid());
                    caseBinding.setCaze(updatedCase);

                    if ((boolean) success) {
                        Snackbar.make(findViewById(R.id.base_layout), getResources().getString(R.string.snackbar_case_moved), Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(findViewById(R.id.base_layout), getResources().getString(R.string.snackbar_case_moved_error), Snackbar.LENGTH_LONG).show();
                    }

                    setAdapter(updatedCase);
                }
            };

            final FacilityChangeDialogBuilder dialogBuilder = new FacilityChangeDialogBuilder(this, caseBinding.getCaze(), positiveCallback);
            AlertDialog facilityChangeDialog = dialogBuilder.create();
            facilityChangeDialog.show();
            dialogBuilder.setButtonListeners(facilityChangeDialog, this);
        }
    }

    private boolean saveCase(Case caze, Case caseBeforeSaving) {
        if (saveCaseToDatabase()) {
            Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_case)), Snackbar.LENGTH_LONG).show();

            // switch to next tab
            try {
                pager.setCurrentItem(currentTab + 1);
            } catch (NullPointerException e) {
                pager.setCurrentItem(currentTab);
            }

            // reset adapter for Plague cases to make sure that the Contact tab is displayed or hidden correctly
            Case savedCase = DatabaseHelper.getCaseDao().queryUuid(caze.getUuid());
            if (savedCase.getDisease() == Disease.PLAGUE && caseBeforeSaving.getPlagueType() != savedCase.getPlagueType() &&
                    (caseBeforeSaving.getPlagueType() == PlagueType.PNEUMONIC || savedCase.getPlagueType() == PlagueType.PNEUMONIC)) {
                setAdapter(savedCase);
            }
        }

        return true;
    }

    private boolean saveCaseToDatabase() {
        // PATIENT
        LocationDao locLocationDao = DatabaseHelper.getLocationDao();
        PersonDao personDao = DatabaseHelper.getPersonDao();
        Person person = (Person) getData(adapter.getPositionOfTab(CaseEditTabs.PATIENT));

        // SYMPTOMS
        Symptoms symptoms = (Symptoms) getData(adapter.getPositionOfTab(CaseEditTabs.SYMPTOMS));

        // HOSPITALIZATION
        Hospitalization hospitalization = (Hospitalization) getData(adapter.getPositionOfTab(CaseEditTabs.HOSPITALIZATION));

        // EPI DATA
        EpiData epiData = (EpiData) getData(adapter.getPositionOfTab(CaseEditTabs.EPIDATA));

        // CASE_DATA
        Case caze = (Case) getData(adapter.getPositionOfTab(CaseEditTabs.CASE_DATA));

        // Validations have to be processed from last tab to first to make sure that the user will be re-directed
        // to the first tab with a validation error
        PersonEditFragmentLayoutBinding personBinding =  ((PersonEditForm)getTabByPosition(adapter.getPositionOfTab(CaseEditTabs.PATIENT))).getBinding();
        CaseSymptomsFragmentLayoutBinding symptomsBinding = ((SymptomsEditForm)getTabByPosition(adapter.getPositionOfTab(CaseEditTabs.SYMPTOMS))).getBinding();

        // Necessary because the entry could've been automatically set, in which case the setValue method of the
        // custom field has not been called
        symptoms.setOnsetSymptom((String) symptomsBinding.symptomsOnsetSymptom.getValue());

        PersonValidator.clearErrors(personBinding);
        SymptomsValidator.clearErrorsForSymptoms(symptomsBinding);

        int validationErrorTab = -1;

        if (!SymptomsValidator.validateCaseSymptoms(symptoms, symptomsBinding)) {
            validationErrorTab = adapter.getPositionOfTab(CaseEditTabs.SYMPTOMS);
        }
        if (!PersonValidator.validatePersonData(person, personBinding)) {
            validationErrorTab = adapter.getPositionOfTab(CaseEditTabs.PATIENT);
        }

        if (validationErrorTab >= 0) {
            pager.setCurrentItem(validationErrorTab);
            return false;
        }

        try {
            personDao.saveAndSnapshot(person);
            caze.setPerson(person); // we have to set this - otherwise data from the person will be overridden with not fully initialized data
            caze.setSymptoms(symptoms);
            caze.setHospitalization(hospitalization);
            caze.setEpiData(epiData);
            DatabaseHelper.getCaseDao().saveAndSnapshot(caze);

            return true;
        } catch (DaoException e) {
            Log.e(getClass().getName(), "Error while trying to save case", e);
            Log.e(getClass().getName(), "- root cause: ", ErrorReportingHelper.getRootCause(e));
            Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_case)), Snackbar.LENGTH_LONG).show();
            ErrorReportingHelper.sendCaughtException(tracker, e, caze, true);

            return false;
        }
    }

    private AlertDialog buildPlagueTypeChangeDialog(PlagueType plagueType, final Callback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format(getResources().getString(R.string.alert_plague_type_change), plagueType.toString(), plagueType.toString()));
        builder.setTitle(R.string.alert_title_plague_type_change);
        builder.setIcon(R.drawable.ic_info_outline_black_24dp);
        AlertDialog dialog = builder.create();
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.action_confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.call();
                    }
                }
        );

        return dialog;
    }

}
