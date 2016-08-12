package de.symeda.sormas.app.user;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;


/**
 * Created by Stefan Szczesny on 21.07.2016.
 */
public class UserActivity extends AppCompatActivity {


    private UserTab userTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.basic_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_user));
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        userTab = new UserTab();
        ft.add(R.id.fragment_frame, userTab).commit();

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

            case android.R.id.home:
                //Home/back button
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_save:
                User user = userTab.getUser();
                if (user != null) {
                    ConfigProvider.setUser(user);
                    return true;
                }

                String serverUrl = userTab.getServerUrl();
                if (serverUrl != null) {
                    ConfigProvider.setServerUrl(serverUrl);
                    return true;
                }

                return false;
        }
        return super.onOptionsItemSelected(item);
    }



}
