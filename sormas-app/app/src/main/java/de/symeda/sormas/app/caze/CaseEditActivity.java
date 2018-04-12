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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.PlagueType;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.AbstractEditTabActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleDao;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.symptoms.SymptomsDtoHelper;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskDao;
import de.symeda.sormas.app.backend.user.User;
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

    /**
     * this will be used to provide the case to the sub forms when they need general information.
     * E.g. the symptoms form might need to know whether the person is an infant.
     * TODO should be updated whenever the tab is changed or case is saved
     */
    private Case editedCase;

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
        viewPager.setOffscreenPageLimit(0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_case));
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
        User user = ConfigProvider.getUser();
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
                updateActionBarGroups(menu, false, true, true, user.hasUserRight(UserRight.CONTACT_CREATE), false);
                break;

            case TASKS:
                updateActionBarGroups(menu, false, true, true, false, false);
                break;

            case SAMPLES:
                updateActionBarGroups(menu, false, true, true, user.hasUserRight(UserRight.SAMPLE_CREATE), false);
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
                        List<Contact> contacts = contactDao.getByCase(editedCase);
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
                        List<Sample> samples = sampleDao.queryByCase(editedCase);
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
                        List<Task> tasks = taskDao.queryByCase(editedCase);
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
                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName() + ":" + tab.toString(), editedCase.getUuid());
                AlertDialog dialog = userReportDialog.create();
                dialog.show();

                return true;

            // Save button
            case R.id.action_save:
                final Case caseBeforeSaving = DatabaseHelper.getCaseDao().queryUuid(editedCase.getUuid());
                boolean showPlagueTypeChangeAlert = false;
                if (editedCase.getDisease() == Disease.PLAGUE) {
                    showPlagueTypeChangeAlert = updatePlagueType(editedCase);
                }

                if (saveCaseToDatabase()) {
                    if (editedCase.getDisease() == Disease.PLAGUE && showPlagueTypeChangeAlert) {
                        AlertDialog plagueTypeChangeDialog = buildPlagueTypeChangeDialog(editedCase.getPlagueType(), new Callback() {
                            @Override
                            public void call() {
                                finalizeSaveProcess(editedCase, caseBeforeSaving);
                            }
                        });
                        plagueTypeChangeDialog.show();
                    } else {
                        finalizeSaveProcess(editedCase, caseBeforeSaving);
                    }
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

    public void setAdapter(Case caze) {

        editedCase = caze;

        CaseDataDto caseDataDto = new CaseDataDto();
        new CaseDtoHelper().fillInnerFromAdo(caseDataDto, caze);
        List<CaseEditTabs> visibleTabs = buildVisibleTabsList(caseDataDto);

        adapter = new CaseEditPagerAdapter(getSupportFragmentManager(), new CaseProvider() {
            @Override
            public Case getCase() {
                return editedCase;
            }
        }, visibleTabs);
        createTabViews(adapter);

        pager.setCurrentItem(currentTab);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position > pager.getOffscreenPageLimit()) {
                    pager.setOffscreenPageLimit(Math.min(position + 2, CaseEditTabs.values().length));
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position > pager.getOffscreenPageLimit()) {
                    pager.setOffscreenPageLimit(Math.min(position + 2, CaseEditTabs.values().length));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void transferCase(View v) {
        if (saveCaseToDatabase()) {
            final CaseDataFragmentLayoutBinding caseBinding = ((CaseEditDataForm)getTabByPosition(adapter.getPositionOfTab(CaseEditTabs.CASE_DATA))).getBinding();

            final Consumer positiveCallback = new Consumer() {
                @Override
                public void accept(Object success) {
                    Case updatedCase = DatabaseHelper.getCaseDao().queryUuid(caseBinding.getCaze().getUuid());
                    caseBinding.setCaze(updatedCase);

                    if ((boolean) success) {
                        Snackbar.make(findViewById(R.id.base_layout), getResources().getString(R.string.snackbar_case_transfered), Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(findViewById(R.id.base_layout), getResources().getString(R.string.snackbar_case_transfered_error), Snackbar.LENGTH_LONG).show();
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

    private boolean updatePlagueType(Case caze) {
        Symptoms symptoms = (Symptoms) getData(adapter.getPositionOfTab(CaseEditTabs.SYMPTOMS));
        if (symptoms != null) {
            SymptomsDto symptomsDto = new SymptomsDto();
            new SymptomsDtoHelper().fillInnerFromAdo(symptomsDto, symptoms);

            final PlagueType newPlagueType = DiseaseHelper.getPlagueTypeForSymptoms(symptomsDto);
            if (newPlagueType != null && newPlagueType != caze.getPlagueType()) {
                caze.setPlagueType(newPlagueType);
                return true;
            }
        }

        return false;
    }

    private boolean saveCaseToDatabase() {

        // CAZE
        Case caze = (Case) getData(adapter.getPositionOfTab(CaseEditTabs.CASE_DATA));

        // PATIENT
        PersonDao personDao = DatabaseHelper.getPersonDao();
        Person person = (Person) getData(adapter.getPositionOfTab(CaseEditTabs.PATIENT));

        // SYMPTOMS
        Symptoms symptoms = (Symptoms) getData(adapter.getPositionOfTab(CaseEditTabs.SYMPTOMS));

        // HOSPITALIZATION
        Hospitalization hospitalization = (Hospitalization) getData(adapter.getPositionOfTab(CaseEditTabs.HOSPITALIZATION));

        // EPI DATA
        EpiData epiData = (EpiData) getData(adapter.getPositionOfTab(CaseEditTabs.EPIDATA));

        // Validations have to be processed from last tab to first to make sure that the user will be re-directed
        // to the first tab with a validation error
        int validationErrorTab = -1;

        if (symptoms != null) {
            CaseSymptomsFragmentLayoutBinding symptomsBinding = ((SymptomsEditForm)getTabByPosition(adapter.getPositionOfTab(CaseEditTabs.SYMPTOMS))).getBinding();

            // Necessary because the entry could've been automatically set, in which case the setValue method of the
            // custom field has not been called
            symptoms.setOnsetSymptom((String) symptomsBinding.symptomsOnsetSymptom.getValue());

            SymptomsValidator.clearErrorsForSymptoms(symptomsBinding);
            if (!SymptomsValidator.validateCaseSymptoms(symptoms, symptomsBinding)) {
                validationErrorTab = adapter.getPositionOfTab(CaseEditTabs.SYMPTOMS);
            }
        }

        if (person != null) {
            PersonEditFragmentLayoutBinding personBinding =  ((PersonEditForm)getTabByPosition(adapter.getPositionOfTab(CaseEditTabs.PATIENT))).getBinding();

            PersonValidator.clearErrors(personBinding);
            if (!PersonValidator.validatePersonData(person, personBinding)) {
                validationErrorTab = adapter.getPositionOfTab(CaseEditTabs.PATIENT);
            }
        }

        if (validationErrorTab >= 0) {
            pager.setCurrentItem(validationErrorTab);
            return false;
        }

        try {
            if (person != null) {
                caze.setPerson(person); // we have to set this - otherwise data from the person will be overridden with not fully initialized data
            }
            if (symptoms != null) {
                caze.setSymptoms(symptoms);
            }
            if (hospitalization != null) {
                caze.setHospitalization(hospitalization);
            }
            if (epiData != null) {
                caze.setEpiData(epiData);
            }

            if (caze.getId() != null) {
                Case existingCase = DatabaseHelper.getCaseDao().queryForId(caze.getId());
                updateOutcomeAndPersonCondition(existingCase, caze);
            }

            if (person != null) {
                DatabaseHelper.getPersonDao().saveAndSnapshot(person);
            }
            DatabaseHelper.getCaseDao().saveAndSnapshot(caze);

            editedCase = caze;

            return true;
        } catch (DaoException e) {
            Log.e(getClass().getName(), "Error while trying to save case", e);
            Log.e(getClass().getName(), "- root cause: ", ErrorReportingHelper.getRootCause(e));
            Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_case)), Snackbar.LENGTH_LONG).show();
            ErrorReportingHelper.sendCaughtException(tracker, e, caze, true);

            return false;
        }
    }

    private void updateOutcomeAndPersonCondition(Case existingCase, Case newCase) {

        if (existingCase != null) {
            // see CaseFacadeEjb.onCaseChanged
            if (newCase.getOutcome() != existingCase.getOutcome()) {
                if (newCase.getOutcome() == CaseOutcome.DECEASED) {
                    if (newCase.getPerson().getPresentCondition() != PresentCondition.DEAD
                            && newCase.getPerson().getPresentCondition() != PresentCondition.BURIED) {
                        newCase.getPerson().setPresentCondition(PresentCondition.DEAD);
                    }
                }
            }

            // see PersonFacadeEjb.onPersonChanged
            Person existingPerson = existingCase.getPerson();
            Person newPerson = newCase.getPerson();
            if (newPerson.getPresentCondition() != null
                    && existingPerson.getPresentCondition() != newPerson.getPresentCondition()) {
                if (newPerson.getPresentCondition().isDeceased()) {
                    if (newCase.getOutcome() == CaseOutcome.NO_OUTCOME) {
                        newCase.setOutcome(CaseOutcome.DECEASED);
                        newCase.setOutcomeDate(new Date());
                    }
                } else {
                    if (newCase.getOutcome() == CaseOutcome.DECEASED) {
                        newCase.setOutcome(CaseOutcome.NO_OUTCOME);
                        newCase.setOutcomeDate(new Date());
                    }
                }
            }
        }
    }


    private void finalizeSaveProcess(Case caze, Case caseBeforeSaving) {
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

    private List<CaseEditTabs> buildVisibleTabsList(CaseDataDto caseDataDto) {
        User user = ConfigProvider.getUser();
        List<CaseEditTabs> visibleTabs = new ArrayList<>();
        visibleTabs.addAll(Arrays.asList(CaseEditTabs.CASE_DATA, CaseEditTabs.PATIENT,
                CaseEditTabs.HOSPITALIZATION, CaseEditTabs.SYMPTOMS, CaseEditTabs.EPIDATA));

        if (user.hasUserRight(UserRight.CONTACT_VIEW) && DiseaseHelper.hasContactFollowUp(caseDataDto.getDisease(), caseDataDto.getPlagueType())) {
            visibleTabs.add(CaseEditTabs.CONTACTS);
        }

        if (user.hasUserRight(UserRight.SAMPLE_VIEW)) {
            visibleTabs.add(CaseEditTabs.SAMPLES);
        }

        if (user.hasUserRight(UserRight.TASK_VIEW)) {
            visibleTabs.add(CaseEditTabs.TASKS);
        }

        return visibleTabs;
    }

}
