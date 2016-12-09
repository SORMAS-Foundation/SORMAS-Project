package de.symeda.sormas.app.caze;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.contact.ContactNewActivity;
import de.symeda.sormas.app.person.SyncPersonsTask;


/**
 * Created by Stefan Szczesny on 21.07.2016.
 */
public class CaseEditActivity extends AbstractEditActivity {

    public static final String KEY_CASE_UUID = "caseUuid";
    public static final String KEY_PAGE = "page";

    private CaseEditPagerAdapter adapter;
    private CharSequence titles[];
    private String caseUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.case_edit_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_case));
        }

        // Creating titles for the tabs
        titles = new CharSequence[]{
                getResources().getText(R.string.headline_case_data),
                getResources().getText(R.string.headline_patient),
                getResources().getText(R.string.headline_symptoms),
                getResources().getText(R.string.headline_contacts)
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle params = getIntent().getExtras();
        caseUuid = params.getString(KEY_CASE_UUID);
        adapter = new CaseEditPagerAdapter(getSupportFragmentManager(), titles, caseUuid);
        createTabViews(adapter);

        if (params.containsKey(KEY_PAGE)) {
            pager.setCurrentItem(params.getInt(KEY_PAGE));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_action_bar, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch(currentTab) {
            // case data tab
            case 0:
                updateActionBarGroups(menu, false, false, true);
                break;

            // case person tab
            case 1:
                updateActionBarGroups(menu, false, false, true);
                break;

            // case symptoms tab
            case 2:
                updateActionBarGroups(menu, true, false, true);
                break;

            // case contacts tab
            case 3:
                updateActionBarGroups(menu,false, true, false);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        currentTab = pager.getCurrentItem();
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);

                //Home/back button
                return true;

            // Help button
            case R.id.action_help:
                switch(currentTab) {
                    // case data tab
                    case 0:

                        break;

                    // case person tab
                    case 1:
                        break;

                    // case symptoms tab
                    case 2:
                        StringBuilder sb = new StringBuilder();

                        LinearLayout caseSymptomsForm = (LinearLayout) this.findViewById(R.id.case_symptoms_form);

                        for (int i = 0; i < caseSymptomsForm.getChildCount(); i++) {
                            if (caseSymptomsForm.getChildAt(i) instanceof PropertyField) {
                                PropertyField propertyField = (PropertyField)caseSymptomsForm.getChildAt(i);
                                sb
                                        .append("<b>"+propertyField.getCaption()+"</b>").append("<br>")
                                        .append(propertyField.getDescription()).append("<br>").append("<br>");
                            }
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(Html.fromHtml(sb.toString())).setTitle(getResources().getText(R.string.headline_help));
                        builder.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                        AlertDialog dialog = builder.create();
                        dialog.setCancelable(true);
                        dialog.show();

                        break;
                }


                return true;

            // Save button
            case R.id.action_save:
                CaseDao caseDao = DatabaseHelper.getCaseDao();


                switch(currentTab) {
                    // case data tab
                    case 0:

                        Case caze = (Case) adapter.getData(0);

                        caseDao.save(caze);
                        Toast.makeText(this, "case "+ DataHelper.getShortUuid(caze.getUuid()) +" saved", Toast.LENGTH_SHORT).show();

                        SyncCasesTask.syncCases(getSupportFragmentManager());
                        break;

                    // case person tab
                    case 1:
                        LocationDao locLocationDao = DatabaseHelper.getLocationDao();
                        PersonDao personDao = DatabaseHelper.getPersonDao();

                        Person person = (Person)adapter.getData(1);

                        if(person.getAddress()!=null) {
                            locLocationDao.save(person.getAddress());
                        }
                        personDao.save(person);
                        Toast.makeText(this, "person " + person.toString() + " saved", Toast.LENGTH_SHORT).show();

                        new SyncPersonsTask().execute();
                        break;

                    // case symptoms tab
                    case 2:
                        SymptomsDao symptomsDao = DatabaseHelper.getSymptomsDao();

                        Symptoms symptoms = (Symptoms)adapter.getData(2);

                        if(symptoms!=null) {
                            symptomsDao.save(symptoms);
                        }

                        caseDao.markAsModified(caseUuid);

                        Toast.makeText(this, "symptoms saved", Toast.LENGTH_SHORT).show();

                        SyncCasesTask.syncCases(getSupportFragmentManager());
                        break;
                }

                onResume();
                pager.setCurrentItem(currentTab);

                return true;

            // Add button
            case R.id.action_add:
                Intent intentContactNew = new Intent(this, ContactNewActivity.class);
                startActivity(intentContactNew);

                return true;


        }
        return super.onOptionsItemSelected(item);
    }

}
