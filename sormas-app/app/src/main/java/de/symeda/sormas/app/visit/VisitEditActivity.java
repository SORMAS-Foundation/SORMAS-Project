package de.symeda.sormas.app.visit;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.symptoms.SymptomsDao;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.component.AbstractEditActivity;
import de.symeda.sormas.app.component.HelpDialog;
import de.symeda.sormas.app.task.SyncVisitsTask;


public class VisitEditActivity extends AbstractEditActivity {

    public static final String VISIT_UUID = "visitUuid";
    public static final String KEY_CONTACT_UUID = "contactUuid";
    public static final String KEY_PAGE = "page";
    public static final String KEY_PARENT_TASK_UUID = "taskUuid";

    private VisitEditPagerAdapter adapter;
//    private String visitUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.case_edit_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_visit));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle params = getIntent().getExtras();
//        visitUuid = params.getString(Visit.UUID);
        adapter = new VisitEditPagerAdapter(getSupportFragmentManager(), params);
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
        VisitEditTabs tab = VisitEditTabs.values()[currentTab];
        switch(tab) {
            case VISIT_DATA:
                updateActionBarGroups(menu, false, false, true);
                break;

            case SYMPTOMS:
                updateActionBarGroups(menu, true, false, true);
                break;

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        currentTab = pager.getCurrentItem();
        VisitEditTabs tab = VisitEditTabs.values()[currentTab];

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();

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
                Visit visit = (Visit) adapter.getData(VisitEditTabs.VISIT_DATA.ordinal());
                Symptoms symptoms = (Symptoms)adapter.getData(VisitEditTabs.SYMPTOMS.ordinal());

                if(symptoms!=null) {
                    visit.setSymptoms(symptoms);
                    DatabaseHelper.getSymptomsDao().save(symptoms);
                }

                DatabaseHelper.getVisitDao().save(visit);
                Toast.makeText(this, "visit "+ DataHelper.getShortUuid(visit.getUuid()) +" saved", Toast.LENGTH_SHORT).show();


//                switch(tab) {
//                    // contact data tab
//                    case VISIT_DATA:
//                        Visit visit = (Visit) adapter.getData(VisitEditTabs.VISIT_DATA.ordinal());
//
//                        DatabaseHelper.getVisitDao().save(visit);
//                        Toast.makeText(this, "visit "+ DataHelper.getShortUuid(visit.getUuid()) +" saved", Toast.LENGTH_SHORT).show();
//                        break;
//
//                    case SYMPTOMS:
//                        SymptomsDao symptomsDao = DatabaseHelper.getSymptomsDao();
//
//                        Symptoms symptoms = (Symptoms)adapter.getData(VisitEditTabs.SYMPTOMS.ordinal());
//
//                        if(symptoms!=null) {
//                            symptomsDao.save(symptoms);
//                        }
//
//                        Toast.makeText(this, "symptoms saved", Toast.LENGTH_SHORT).show();
//
//                        new SyncVisitsTask().execute();
//                        break;
//
//                }

//                onResume();
//                pager.setCurrentItem(currentTab);

                finish();

                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
