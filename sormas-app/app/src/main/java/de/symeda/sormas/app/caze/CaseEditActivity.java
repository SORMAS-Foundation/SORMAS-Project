package de.symeda.sormas.app.caze;

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

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.LocationDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.symptoms.SymptomsDao;
import de.symeda.sormas.app.component.AbstractEditActivity;
import de.symeda.sormas.app.component.HelpDialog;
import de.symeda.sormas.app.contact.ContactNewActivity;
import de.symeda.sormas.app.person.SyncPersonsTask;

public class CaseEditActivity extends AbstractEditActivity {

    public static final String KEY_CASE_UUID = "caseUuid";
    public static final String KEY_PARENT_TASK_UUID = "taskUuid";

    private CaseEditPagerAdapter adapter;
    private String caseUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.case_edit_activity_layout);

        // This makes sure that the given amount of tabs is kept in memory, which means that
        // Android doesn't call onResume when the tab has no focus which would otherwise lead
        // to certain spinners not displaying their values
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(10);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_case));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle params = getIntent().getExtras();
        if(params!=null) {
            if(params.containsKey(KEY_CASE_UUID)) {
                caseUuid = params.getString(KEY_CASE_UUID);
            }
            if (params.containsKey(KEY_PAGE)) {
                currentTab = params.getInt(KEY_PAGE);
            }
        }
        adapter = new CaseEditPagerAdapter(getSupportFragmentManager(), caseUuid);
        createTabViews(adapter);


        pager.setCurrentItem(currentTab);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Bundle params = getIntent().getExtras();
        if(params!=null) {
            if(params.containsKey(KEY_CASE_UUID)) {
                outState.putString(KEY_CASE_UUID, caseUuid);
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
        switch(tab) {
            case CASE_DATA:
                updateActionBarGroups(menu, false, false, true);
                break;

            case PATIENT:
                updateActionBarGroups(menu, false, false, true);
                break;

            case SYMPTOMS:
                updateActionBarGroups(menu, true, false, true);
                break;

            case CONTACTS:
                updateActionBarGroups(menu,false, true, false);
                break;

            case TASKS:
                updateActionBarGroups(menu, false, false, false);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setCurrentTab(pager.getCurrentItem());
        CaseEditTabs tab = CaseEditTabs.values()[currentTab];
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);

                //Home/back button
                return true;

            // Help button
            case R.id.action_help:
                HelpDialog helpDialog = new HelpDialog(this);

                switch(tab) {
                    case SYMPTOMS:
                        String helpText = HelpDialog.getHelpForForm((LinearLayout) this.findViewById(R.id.case_symptoms_form));
                        helpDialog.setMessage(Html.fromHtml(helpText).toString());
                        break;
                }

                helpDialog.show();

                return true;

            // Save button
            case R.id.action_save:
                CaseDao caseDao = DatabaseHelper.getCaseDao();


                switch (tab) {
                    case CASE_DATA:

                        Case caze = (Case) adapter.getData(0);

                        caseDao.save(caze);
                        Toast.makeText(this, "case " + DataHelper.getShortUuid(caze.getUuid()) + " saved", Toast.LENGTH_SHORT).show();

                        SyncCasesTask.syncCases(getSupportFragmentManager());
                        break;

                    case PATIENT:
                        LocationDao locLocationDao = DatabaseHelper.getLocationDao();
                        PersonDao personDao = DatabaseHelper.getPersonDao();

                        Person person = (Person) adapter.getData(1);

                        if (person.getAddress() != null) {
                            locLocationDao.save(person.getAddress());
                        }
                        personDao.save(person);
                        Toast.makeText(this, "person " + person.toString() + " saved", Toast.LENGTH_SHORT).show();

                        new SyncPersonsTask().execute();
                        break;

                    case SYMPTOMS:
                        SymptomsDao symptomsDao = DatabaseHelper.getSymptomsDao();

                        Symptoms symptoms = (Symptoms) adapter.getData(CaseEditTabs.SYMPTOMS.ordinal());

                        boolean anySymptomSetToYes = isAnySymptomSetToYes(symptoms);
                        boolean otherHemorrhagicSymTextReq = symptoms.getOtherHemorrhagicSymptoms() == SymptomState.YES &&
                                (symptoms.getOtherHemorrhagicSymptomsText() == null || symptoms.getOtherHemorrhagicSymptomsText().isEmpty());
                        boolean otherNonHemorrhagicSymTextReq = symptoms.getOtherNonHemorrhagicSymptoms() == SymptomState.YES &&
                                (symptoms.getOtherNonHemorrhagicSymptomsText() == null || symptoms.getOtherNonHemorrhagicSymptomsText().isEmpty());

                        // data is valid if at least one symptom is set to yes AND the onset date
                        // and onset symptom string have been set or no symptom is set to YES
                        boolean validData = (!anySymptomSetToYes || ((symptoms.getOnsetDate() != null &&
                                symptoms.getOnsetSymptom() != null && !symptoms.getOnsetSymptom().isEmpty()))) &&
                                !otherHemorrhagicSymTextReq && !otherNonHemorrhagicSymTextReq;
                        if(validData) {
                            if (symptoms != null) {
                                symptomsDao.save(symptoms);
                            }

                            caseDao.markAsModified(caseUuid);

                            Toast.makeText(this, "symptoms saved", Toast.LENGTH_SHORT).show();

                            SyncCasesTask.syncCases(getSupportFragmentManager());
                            break;
                        } else {
                            if(otherHemorrhagicSymTextReq) {
                                Toast.makeText(this, "Please specify the additional hemorrhagic symptoms.", Toast.LENGTH_LONG).show();
                            } else if(otherNonHemorrhagicSymTextReq) {
                                Toast.makeText(this, "Please specify the additional clinical symptoms.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, "Please specify an onset date and an onset symptom.", Toast.LENGTH_LONG).show();
                            }
                            return true;
                        }
                }

                onResume();
                pager.setCurrentItem(currentTab);

                return true;

            // Add button
            case R.id.action_add:

                Bundle contactCreateBundle = new Bundle();
                contactCreateBundle.putString(KEY_CASE_UUID, caseUuid);
                Intent intentContactNew = new Intent(this, ContactNewActivity.class);
                intentContactNew.putExtras(contactCreateBundle);
                startActivity(intentContactNew);

                return true;


        }
        return super.onOptionsItemSelected(item);
    }

}
