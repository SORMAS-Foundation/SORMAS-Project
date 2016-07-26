package de.symeda.sormas.app;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;
import java.util.concurrent.ExecutionException;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.caze.CaseEditActivity;
import de.symeda.sormas.app.caze.CaseListArrayAdapter;
import de.symeda.sormas.app.caze.SyncCasesTask;
import de.symeda.sormas.app.person.SyncPersonsTask;

public class SurveillanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cases_view);

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
        inflater.inflate(R.menu.list_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reload) {
            refreshLocalDB();
            refreshCaseList();
            return true;
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
            Integer syncedPersons = new SyncPersonsTask().execute().get();
            List<CaseDataDto> syncedCases = new SyncCasesTask().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void showCaseEditView(Case caze) {
        Intent intent = new Intent(this, CaseEditActivity.class);
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
