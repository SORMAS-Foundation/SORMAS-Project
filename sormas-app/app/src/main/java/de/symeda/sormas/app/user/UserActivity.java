package de.symeda.sormas.app.user;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.AbstractRootTabActivity;
import de.symeda.sormas.app.component.SyncLogDialog;
import de.symeda.sormas.app.component.UserReportDialog;


public class UserActivity extends AbstractRootTabActivity {

    private UserForm userForm;

    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.sormas_root_activity_layout);

        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.main_menu_settings));

        SormasApplication application = (SormasApplication) getApplication();
        tracker = application.getDefaultTracker();

        // setting the fragment_frame
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        userForm = new UserForm();
        ft.add(R.id.fragment_frame, userForm).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        userForm.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_action_bar, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_options).getSubMenu().setGroupVisible(R.id.group_action_help,false);
        menu.setGroupVisible(R.id.group_action_add,false);
        menu.setGroupVisible(R.id.group_action_save,true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Report problem button
            case R.id.action_report:
                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), null);
                AlertDialog dialog = userReportDialog.create();
                dialog.show();

                return true;

            case R.id.action_save:
//                User user = userForm.getUser();
//                ConfigProvider.setUser(user);

                String serverUrl = userForm.getServerUrl();
                ConfigProvider.setServerRestUrl(serverUrl);

                onResume();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openSyncLog(View view) {
        SyncLogDialog syncLogDialog = new SyncLogDialog(this);
        syncLogDialog.show(this);
    }

}
