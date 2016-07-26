package de.symeda.sormas.app.caze;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SurveillanceActivity;

/**
 * Created by Stefan Szczesny on 21.07.2016.
 */
public class CaseEditActivity extends AppCompatActivity {

    //private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.case_edit_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Case");
        }


        /*final Button button = (Button) findViewById(R.id.button_back);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showCasesView();            }
        });*/


        /*TabHost tabHost = (TabHost) findViewById(R.id.tab_host);

        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("simple").setIndicator("Simple"),
                FragmentStackSupport.CountingFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("contacts").setIndicator("Contacts"),
                LoaderCursorSupport.CursorLoaderListFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("custom").setIndicator("Custom"),
                LoaderCustomSupport.AppListFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("throttle").setIndicator("Throttle"),
                LoaderThrottleSupport.ThrottledLoaderListFragment.class, null);



        TabSpec tab1 = tabHost.newTabSpec("First Tab");
        tab1.setIndicator("Tab1");
        tab1.setContent(new Intent(this, CaseDataActivity.class));
        TabSpec tab2 = tabHost.newTabSpec("Second Tab");
        tab2.setIndicator("Tab2");
        tab2.setContent(new Intent(this, CasePersonActivity.class));

        // Add the tabs  to the TabHost to display.
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);*/


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
        }
        return super.onOptionsItemSelected(item);
    }

    public void setData(CaseDataDto dto) {
        //populateFormView(dto);
    }

    public void showCasesView() {
        Intent intent = new Intent(this, SurveillanceActivity.class);
        startActivity(intent);
    }


}
