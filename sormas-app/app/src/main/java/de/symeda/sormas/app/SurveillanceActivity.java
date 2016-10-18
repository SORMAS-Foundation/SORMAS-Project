package de.symeda.sormas.app;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.caze.CaseEditActivity;
import de.symeda.sormas.app.caze.CasesListFilterAdapter;
import de.symeda.sormas.app.caze.CaseNewActivity;
import de.symeda.sormas.app.caze.CasesListFragment;
import de.symeda.sormas.app.caze.SyncCasesTask;
import de.symeda.sormas.app.person.SyncPersonsTask;
import de.symeda.sormas.app.user.UserActivity;
import de.symeda.sormas.app.util.SlidingTabLayout;

public class SurveillanceActivity extends AppCompatActivity {

    private ViewPager pager;
    private CasesListFilterAdapter adapter;
    private SlidingTabLayout tabs;

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

        createTabViews();
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

    private void createTabViews() {
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new CasesListFilterAdapter(getSupportFragmentManager());

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assigning the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
    }

    private void refreshLocalDB() {

        new SyncPersonsTask() {
            @Override
            protected void onPostExecute(Void aVoid) {
                new SyncCasesTask() {
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        if (getSupportFragmentManager() != null && getSupportFragmentManager().getFragments() != null) {
                            for (Fragment fragement : getSupportFragmentManager().getFragments()) {
                                if (fragement instanceof CasesListFragment) {
                                    fragement.onResume();
                                }
                            }
                        }

                        Toast toast = Toast.makeText(SurveillanceActivity.this, "refreshed local db", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }.execute();
            }
        }.execute();
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
}
