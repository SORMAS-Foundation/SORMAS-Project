package de.symeda.sormas.app.event;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.caze.CasesListFilterAdapter;
import de.symeda.sormas.app.caze.SyncCasesTask;
import de.symeda.sormas.app.component.AbstractRootTabActivity;
import de.symeda.sormas.app.util.SlidingTabLayout;

public class EventsActivity extends AbstractRootTabActivity {

    private ViewPager pager;
    private EventsListFilterAdapter adapter;
    private SlidingTabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.cases_activity_layout);
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.main_menu_events));

        SyncEventsTask.syncEvents(getSupportFragmentManager());
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
                SyncEventsTask.syncEvents(getSupportFragmentManager());
                return true;

            case R.id.action_new_case:
                showCaseNewView();
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void createTabViews() {
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new EventsListFilterAdapter(getSupportFragmentManager());

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
}
