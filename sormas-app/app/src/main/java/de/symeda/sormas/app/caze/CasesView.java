package de.symeda.sormas.app.caze;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.app.SormasAppView;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SurveillanceActivity;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.person.SyncPersonsTask;

/**
 * Created by Stefan Szczesny on 21.07.2016.
 */
public class CasesView extends SormasAppView<SurveillanceActivity> {

    public CasesView(SurveillanceActivity context, int viewId) {
        super(context, viewId);
    }

    @Override
    protected void init() {
        getContext().setContentView(getViewId());
        TextView t = (TextView) getContext().findViewById(R.id.view_header_label);
        t.setText("Cases");

        try {
            // temp: delete old data
            RuntimeExceptionDao<Person, Long> personDao = getContext().getHelper().getSimplePersonDao();
            TableUtils.clearTable(personDao.getConnectionSource(), Case.class);
            TableUtils.clearTable(personDao.getConnectionSource(), Person.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void show() {
        try {
            // todo asynchronous calls: Cases have to wait for Persons
            Integer syncedPersons = new SyncPersonsTask(getContext()).execute().get();
            List<CaseDataDto> syncedCases = new SyncCasesTask(getContext()).execute().get();
            populateListView(syncedCases);
            //new AlertDialog.Builder(this).setTitle("synced: " + syncedCases.size()).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a list of cases and bind a itemClickListener
     * @param cases
     */
    private void populateListView(final List<CaseDataDto> cases) {
        CaseListArrayAdapter adapter = new CaseListArrayAdapter(
                getContext(),                       // Context for the activity.
                R.layout.case_list_item,    // Layout to use (create)
                cases);                     // Items to be displayed // Configure the list view.

        ListView list = (ListView) getContext().findViewById(R.id.cases_list_view);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent,
                    View viewClicked,
                    int position, long id) {
                String message = "You clicked # " + position + ", which is: " + cases.get(position).getPerson().toString();
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }


}
