package de.symeda.sormas.app.caze;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.epidata.EpiDataDao;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.hospitalization.HospitalizationDao;
import de.symeda.sormas.app.backend.location.LocationDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.symptoms.SymptomsDao;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.component.AbstractEditActivity;
import de.symeda.sormas.app.component.HelpDialog;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.contact.ContactNewActivity;
import de.symeda.sormas.app.sample.SampleEditActivity;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.task.TaskEditActivity;
import de.symeda.sormas.app.task.TaskForm;
import de.symeda.sormas.app.util.ValidationFailedException;

public class CaseEditActivity extends AbstractEditActivity {

    public static final String KEY_CASE_UUID = "caseUuid";
    public static final String CASE_SUBTITLE = "caseSubtitle";

    private CaseEditPagerAdapter adapter;
    private String caseUuid;
    private String taskUuid;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.case_edit_activity_layout);

        // This makes sure that the given amount of tabs is kept in memory, which means that
        // Android doesn't call onResume when the tab has no focus which would otherwise lead
        // to certain spinners not displaying their values
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(10);

        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setTitle(getResources().getText(R.string.headline_case) + " - " + ConfigProvider.getUser().getUserRole().toShortString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle params = getIntent().getExtras();
        if (params != null) {
            if (params.containsKey(KEY_CASE_UUID)) {
                caseUuid = params.getString(KEY_CASE_UUID);
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
        adapter = new CaseEditPagerAdapter(getSupportFragmentManager(), caseUuid);
        createTabViews(adapter);

        pager.setCurrentItem(currentTab);
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
                updateActionBarGroups(menu, false, true, false, true);
                break;

            case PATIENT:
                updateActionBarGroups(menu, false, true, false, true);
                break;

            case SYMPTOMS:
                updateActionBarGroups(menu, true, true, false, true);
                break;

            case CONTACTS:
                updateActionBarGroups(menu, false, true, true, false);
                break;

            case TASKS:
                updateActionBarGroups(menu, false, true, false, false);
                break;

            case SAMPLES:
                updateActionBarGroups(menu, false, true, true, false);
                break;

            case HOSPITALIZATION:
                updateActionBarGroups(menu, false, true, false, true);
                break;

            case EPIDATA:
                updateActionBarGroups(menu, false, true, false, true);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setCurrentTab(pager.getCurrentItem());
        CaseEditTabs tab = CaseEditTabs.values()[currentTab];
        Case caze = (Case) adapter.getData(CaseEditTabs.CASE_DATA.ordinal());
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

            // Report problem button
            case R.id.action_report:
                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName() + ":" + tab.toString(), caze.getUuid());
                AlertDialog dialog = userReportDialog.create();
                dialog.show();

                return true;

            // Save button
            case R.id.action_save:
                CaseDao caseDao = DatabaseHelper.getCaseDao();

                // PATIENT
                LocationDao locLocationDao = DatabaseHelper.getLocationDao();
                PersonDao personDao = DatabaseHelper.getPersonDao();
                Person person = (Person) adapter.getData(CaseEditTabs.PATIENT.ordinal());

                // SYMPTOMS
                SymptomsDao symptomsDao = DatabaseHelper.getSymptomsDao();
                Symptoms symptoms = (Symptoms) adapter.getData(CaseEditTabs.SYMPTOMS.ordinal());
                SymptomsEditForm symptomsEditForm = (SymptomsEditForm) adapter.getTabByPosition(CaseEditTabs.SYMPTOMS.ordinal());

                // HOSPITALIZATION
                HospitalizationDao hospitalizationDao = DatabaseHelper.getHospitalizationDao();
                Hospitalization hospitalization = (Hospitalization) adapter.getData(CaseEditTabs.HOSPITALIZATION.ordinal());

                // EPI DATA
                EpiDataDao epiDataDao = DatabaseHelper.getEpiDataDao();
                EpiData epiData = (EpiData) adapter.getData(CaseEditTabs.EPIDATA.ordinal());

                // CASE_DATA
                caze = (Case) adapter.getData(CaseEditTabs.CASE_DATA.ordinal());

                boolean validData = true;

                if (caze.getDisease() == null) {
                    validData = false;
                    Toast.makeText(this, "Please select a disease.", Toast.LENGTH_SHORT).show();
                }

                if (person.getFirstName() == null || person.getFirstName().isEmpty()) {
                    validData = false;
                    Toast.makeText(this, "Please enter a first name for the case person.", Toast.LENGTH_SHORT).show();
                }

                if (person.getLastName() == null || person.getLastName().isEmpty()) {
                    validData = false;
                    Toast.makeText(this, "Please enter a last name for the case person.", Toast.LENGTH_SHORT).show();
                }

                if (caze.getHealthFacility() == null) {
                    validData = false;
                    Toast.makeText(this, "Please select a health facility.", Toast.LENGTH_SHORT).show();
                }

                try {
                    symptomsEditForm.validateCaseData(symptoms);
                } catch(ValidationFailedException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    validData = false;
                }

                if (validData) {

                    if (person.getAddress() != null) {
                        locLocationDao.save(person.getAddress());
                    }
                    if (!personDao.save(person)) {
                        Toast.makeText(this, "person not saved", Toast.LENGTH_SHORT).show();
                    }
                    caze.setPerson(person); // we aren't sure why, but this is needed, otherwise the person will be overriden when first saved

                    if (symptoms != null) {
                        if (!symptomsDao.save(symptoms)) {
                            Toast.makeText(this, "symptoms not saved", Toast.LENGTH_SHORT).show();
                        }
                        caze.setSymptoms(symptoms);
                    }

                    if (hospitalization != null) {
                        if (!hospitalizationDao.save(hospitalization)) {
                            Toast.makeText(this, "hospitalization not saved", Toast.LENGTH_SHORT).show();
                        }
                        caze.setHospitalization(hospitalization);
                    }

                    if (epiData != null) {
                        if (!epiDataDao.save(epiData)) {
                            Toast.makeText(this, "epi data not saved", Toast.LENGTH_SHORT).show();
                        }
                        caze.setEpiData(epiData);
                    }

                    if (!caseDao.save(caze)) {
                        Toast.makeText(this, "case not saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "case saved", Toast.LENGTH_LONG).show();
                    }

                    SyncCasesTask.syncCasesWithProgressDialog(this, new Callback() {
                        @Override
                        public void call() {
                            onResume();
		                    try {
		                        pager.setCurrentItem(currentTab + 1);
		                    } catch (NullPointerException e) {
		                        pager.setCurrentItem(currentTab);
		                    }
                        }
                    });
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

        return super.

                onOptionsItemSelected(item);
    }

}
