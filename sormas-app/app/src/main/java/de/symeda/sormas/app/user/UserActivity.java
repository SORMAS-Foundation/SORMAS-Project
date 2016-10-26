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
import de.symeda.sormas.app.SormasRootActivity;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;


/**
 * Created by Stefan Szczesny on 21.07.2016.
 */
public class UserActivity extends SormasRootActivity {


    private UserTab userTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.sormas_root_activity_layout);

        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.main_menu_user));

        // setting the fragment_frame
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        userTab = new UserTab();
        ft.add(R.id.fragment_frame, userTab).commit();

    }

    @Override
    protected void onResume() {
        super.onResume();

        userTab.onResume();
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

            case R.id.action_save:
                User user = userTab.getUser();
                ConfigProvider.setUser(user);

                String serverUrl = userTab.getServerUrl();
                ConfigProvider.setServerUrl(serverUrl);

                onResume();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
