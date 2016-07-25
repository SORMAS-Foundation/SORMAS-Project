package de.symeda.sormas.app;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;
import java.util.concurrent.ExecutionException;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.app.caze.CaseEditActivity;
import de.symeda.sormas.app.caze.CaseListArrayAdapter;
import de.symeda.sormas.app.caze.SyncCasesTask;
import de.symeda.sormas.app.person.SyncPersonsTask;

public class SurveillanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cases_view);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        try {
            // todo asynchronous calls: Cases have to wait for Persons
            Integer syncedPersons = new SyncPersonsTask().execute().get();
            List<CaseDataDto> syncedCases = new SyncCasesTask().execute().get();
            populateListView(syncedCases);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_action_bar, menu);
        return true;
    }

    public void showCaseEditView(CaseDataDto dto) {
        Intent intent = new Intent(this, CaseEditActivity.class);
        startActivity(intent);
    }


    /**
     * Create a list of cases and bind a itemClickListener
     * @param cases
     */
    private void populateListView(final List<CaseDataDto> cases) {
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
