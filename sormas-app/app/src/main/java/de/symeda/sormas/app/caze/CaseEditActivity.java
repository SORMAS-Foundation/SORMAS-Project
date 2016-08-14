package de.symeda.sormas.app.caze;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.LocationDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.person.SyncPersonsTask;
import de.symeda.sormas.app.util.SlidingTabLayout;


/**
 * Created by Stefan Szczesny on 21.07.2016.
 */
public class CaseEditActivity extends AppCompatActivity {

    public static final String KEY_CASE_UUID = "caseUuid";
    public static final String KEY_PAGE = "page";

    private ViewPager pager;
    private CaseEditPagerAdapter adapter;
    private SlidingTabLayout tabs;
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
                getResources().getText(R.string.headline_patient)
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle params = getIntent().getExtras();
        caseUuid = params.getString(KEY_CASE_UUID);
        createTabViews(caseUuid);

        if (params.containsKey(KEY_PAGE)) {
            pager.setCurrentItem(params.getInt(KEY_PAGE));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_caze_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);

                //Home/back button
                return true;

            case R.id.action_save:

                int currentTab = pager.getCurrentItem();

                // case data tab
                if(currentTab==0) {
                    CaseDao caseDao = DatabaseHelper.getCaseDao();

                    Case caze = (Case) adapter.getData(0);

                    caseDao.save(caze);
                    Toast.makeText(this, "case "+ DataHelper.getShortUuid(caze.getUuid()) +" saved", Toast.LENGTH_SHORT).show();

                    new SyncCasesTask().execute();
                }

                // case person tab
                else if(currentTab==1) {
                    LocationDao locLocationDao = DatabaseHelper.getLocationDao();
                    PersonDao personDao = DatabaseHelper.getPersonDao();

                    Person person = (Person)adapter.getData(1);

                    if(person.getAddress()!=null) {
                        locLocationDao.save(person.getAddress());
                    }
                    personDao.save(person);
                    Toast.makeText(this, person.toString() + " saved", Toast.LENGTH_SHORT).show();

                    new SyncPersonsTask().execute();
                }

                onResume();
                pager.setCurrentItem(currentTab);

                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    private void createTabViews(String caseUuid) {
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new CaseEditPagerAdapter(getSupportFragmentManager(), titles, caseUuid);

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
