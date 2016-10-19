package de.symeda.sormas.app.caze;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.Toast;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasRootActivity;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.person.SyncPersonsTask;
import de.symeda.sormas.app.util.SlidingTabLayout;

public class CasesActivity extends SormasRootActivity {

    private ViewPager pager;
    private CasesListFilterAdapter adapter;
    private SlidingTabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.cases_activity_layout);

        super.onCreate(savedInstanceState);
        refreshLocalDB();
    }

    @Override
    protected void onResume() {
        super.onResume();

        createTabViews();
        refreshCaseList();
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

    private void refreshCaseList() {
        CaseDao caseDao = DatabaseHelper.getCaseDao();
        //populateListView(caseDao.queryForAll());
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

                        Toast toast = Toast.makeText(CasesActivity.this, "refreshed local db", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }.execute();
            }
        }.execute();
    }
}
