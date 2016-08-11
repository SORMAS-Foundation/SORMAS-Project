package de.symeda.sormas.app;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutionException;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.caze.CaseEditActivity;
import de.symeda.sormas.app.caze.CaseListArrayAdapter;
import de.symeda.sormas.app.caze.CaseNewActivity;
import de.symeda.sormas.app.caze.SyncCasesTask;
import de.symeda.sormas.app.person.SyncPersonsTask;
import de.symeda.sormas.app.user.UserActivity;
import de.symeda.sormas.app.util.SyncInfrastructureTask;

public class SurveillanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cases_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Cases");
        }

        refreshLocalDB();
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshCaseList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cases_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_reload:
                refreshLocalDB();
                refreshCaseList();
                return true;

            case R.id.action_new_case:
                showCaseNewView();
                return true;

            case R.id.action_user:
                showUserView();
                return true;

            default:
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void refreshCaseList() {
        CaseDao caseDao = DatabaseHelper.getCaseDao();
        populateListView(caseDao.queryForAll());
    }

    private void refreshLocalDB() {
        try {
            // todo asynchronous calls: Cases have to wait for Persons
            new SyncPersonsTask().execute().get();
            new SyncCasesTask().execute().get();

            List<Facility> facilities = DatabaseHelper.getFacilityDao().queryForAll();

            Toast toast = Toast.makeText(this, "refreshed local db", Toast.LENGTH_SHORT);
            toast.show();
        } catch (InterruptedException e) {
            Log.e(SurveillanceActivity.class.getName(), e.toString(), e);
        } catch (ExecutionException e) {
            Log.e(SurveillanceActivity.class.getName(), e.toString(), e);
        }
    }

    public void showCaseEditView(Case caze) {
        Intent intent = new Intent(this, CaseEditActivity.class);
        intent.putExtra(Case.UUID, caze.getUuid());
        startActivity(intent);
    }

    public void showUserView() {
        Intent intent = new Intent(this, UserActivity.class);
        //intent.putExtra(Case.UUID, caze.getUuid());
        startActivity(intent);
    }

    public void showCaseNewView() {
        Intent intent = new Intent(this, CaseNewActivity.class);
        //intent.putExtra(Case.UUID, caze.getUuid());
        startActivity(intent);
    }


    /**
     * Create a list of cases and bind a itemClickListener
     * @param cases
     */
    private void populateListView(final List<Case> cases) {
        CaseListArrayAdapter adapter = new CaseListArrayAdapter(
                this,                       // Context for the activity.
                R.layout.case_list_item,    // Layout to use (create)
                cases);                     // Items to be displayed // Configure the list view.

        ListView list = (ListView) findViewById(R.id.cases_list_view);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent,
                    View viewClicked,
                    int position, long id) {
                showCaseEditView(cases.get(position));
            }
        });
    }

}
