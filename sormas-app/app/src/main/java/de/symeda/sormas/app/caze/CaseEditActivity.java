package de.symeda.sormas.app.caze;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Date;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.LocationDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.symptoms.SymptomsDao;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.person.SyncPersonsTask;
import de.symeda.sormas.app.task.TaskEditActivity;
import de.symeda.sormas.app.task.TaskNotificationService;
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

    private int currentTab;

//
//    private AlarmManager alarmMgr;
//    private PendingIntent alarmIntent;

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
                getResources().getText(R.string.headline_patient),
                getResources().getText(R.string.headline_symptoms)
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch(currentTab) {
            // case data tab
            case 0:
                menu.setGroupVisible(R.id.group_action_help,false);
                break;

            // case person tab
            case 1:
                menu.setGroupVisible(R.id.group_action_help,false);
                break;

            // case symptoms tab
            case 2:
                menu.setGroupVisible(R.id.group_action_help,true);
                break;
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        currentTab = pager.getCurrentItem();
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);

                //Home/back button
                return true;

            // Help button
            case R.id.action_help:
                switch(currentTab) {
                    // case data tab
                    case 0:

                        break;

                    // case person tab
                    case 1:
                        break;

                    // case symptoms tab
                    case 2:
                        StringBuilder sb = new StringBuilder();

                        LinearLayout caseSymptomsForm = (LinearLayout) this.findViewById(R.id.case_symptoms_form);

                        for (int i = 0; i < caseSymptomsForm.getChildCount(); i++) {
                            if (caseSymptomsForm.getChildAt(i) instanceof PropertyField) {
                                PropertyField propertyField = (PropertyField)caseSymptomsForm.getChildAt(i);
                                sb
                                        .append("<b>"+propertyField.getCaption()+"</b>").append("<br>")
                                        .append(propertyField.getDescription()).append("<br>").append("<br>");
                            }
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(Html.fromHtml(sb.toString())).setTitle(getResources().getText(R.string.headline_help));
                        builder.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                        AlertDialog dialog = builder.create();
                        dialog.setCancelable(true);
                        dialog.show();

                        break;
                }


                return true;

            // Save button
            case R.id.action_save:
                CaseDao caseDao = DatabaseHelper.getCaseDao();


                switch(currentTab) {
                    // case data tab
                    case 0:

                        Case caze = (Case) adapter.getData(0);

                        caseDao.save(caze);
                        Toast.makeText(this, "case "+ DataHelper.getShortUuid(caze.getUuid()) +" saved", Toast.LENGTH_SHORT).show();

                        SyncCasesTask.syncCases(getSupportFragmentManager());
                        break;

                    // case person tab
                    case 1:
                        LocationDao locLocationDao = DatabaseHelper.getLocationDao();
                        PersonDao personDao = DatabaseHelper.getPersonDao();

                        Person person = (Person)adapter.getData(1);

                        if(person.getAddress()!=null) {
                            locLocationDao.save(person.getAddress());
                        }
                        personDao.save(person);
                        Toast.makeText(this, "person " + person.toString() + " saved", Toast.LENGTH_SHORT).show();

                        new SyncPersonsTask().execute();
                        break;

                    // case symptoms tab
                    case 2:
                        SymptomsDao symptomsDao = DatabaseHelper.getSymptomsDao();

                        Symptoms symptoms = (Symptoms)adapter.getData(2);

                        if(symptoms!=null) {
                            symptomsDao.save(symptoms);
                        }

                        caseDao.markAsModified(caseUuid);

                        Toast.makeText(this, "symptoms saved", Toast.LENGTH_SHORT).show();

                        SyncCasesTask.syncCases(getSupportFragmentManager());
                        break;
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
//        pager.addOnPageChangeListener();

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
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

            @Override
            public void onPageSelected(int position) {
                currentTab = position;
                invalidateOptionsMenu();
            }
        });
    }

}
