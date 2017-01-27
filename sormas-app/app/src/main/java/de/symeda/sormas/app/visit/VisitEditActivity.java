package de.symeda.sormas.app.visit;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.caze.SymptomsEditTab;
import de.symeda.sormas.app.component.AbstractEditActivity;
import de.symeda.sormas.app.component.HelpDialog;
import de.symeda.sormas.app.util.ValidationFailedException;


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
        adapter = new VisitEditPagerAdapter(getSupportFragmentManager(), params);
        createTabViews(adapter);

        if (params != null && params.containsKey(KEY_PAGE)) {
            currentTab = params.getInt(KEY_PAGE);
        }
        pager.setCurrentItem(currentTab);
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

                SymptomsEditTab symptomsEditTab = (SymptomsEditTab) adapter.getTabByPosition(VisitEditTabs.SYMPTOMS.ordinal());

                // method returns a String, null means that there is no error message and thus
                // the data is valid
                try {
                    symptomsEditTab.validateVisitData(symptoms, visit.getVisitStatus() == VisitStatus.COOPERATIVE);

                    if (symptoms != null) {
                        visit.setSymptoms(symptoms);
                        DatabaseHelper.getSymptomsDao().save(symptoms);
                    }

                    visit.setVisitUser(ConfigProvider.getUser());

                    DatabaseHelper.getVisitDao().save(visit);
                    Toast.makeText(this, "visit " + DataHelper.getShortUuid(visit.getUuid()) + " saved", Toast.LENGTH_SHORT).show();

                    finish();

                    return true;
                } catch(ValidationFailedException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();

                    // if any symptomsfield is required, change pager to symptoms tab
                    if(currentTab!=VisitEditTabs.SYMPTOMS.ordinal()) {
                        pager.setCurrentItem(VisitEditTabs.SYMPTOMS.ordinal());
                    }
                    return true;
                }

        }
        return super.onOptionsItemSelected(item);
    }
}
