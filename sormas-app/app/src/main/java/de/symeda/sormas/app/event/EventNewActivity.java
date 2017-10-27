package de.symeda.sormas.app.event;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventDao;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.contact.ContactEditTabs;
import de.symeda.sormas.app.databinding.EventDataFragmentLayoutBinding;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.LocationService;
import de.symeda.sormas.app.util.SyncCallback;
import de.symeda.sormas.app.validation.EventValidator;

/**
 * Created by Mate Strysewske on 24.07.2017.
 */
public class EventNewActivity extends AppCompatActivity {

    private EventEditDataForm eventEditForm;

    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SormasApplication application = (SormasApplication) getApplication();
        tracker = application.getDefaultTracker();

        setContentView(R.layout.sormas_root_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_new_event) + " - " + ConfigProvider.getUser().getUserRole().toShortString());
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        eventEditForm = new EventEditDataForm();
        ft.add(R.id.fragment_frame, eventEditForm).commit();
    }

    @Override
    protected void onResume() {

        LocationService.instance().requestFreshLocation(this);

        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_action_bar, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.setGroupVisible(R.id.group_action_save, true);
        menu.setGroupVisible(R.id.group_action_report, true);
        menu.setGroupVisible(R.id.group_action_help, false);
        menu.setGroupVisible(R.id.group_action_add, false);
        menu.setGroupVisible(R.id.group_action_markAllAsRead, false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_report:
                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), null);
                android.app.AlertDialog dialog = userReportDialog.create();
                dialog.show();
                return true;

            case R.id.action_save:
                Event event = (Event) eventEditForm.getData();

                EventDataFragmentLayoutBinding binding = eventEditForm.getBinding();
                EventValidator.clearErrorsForEventData(binding);
                if (!EventValidator.validateEventData(event, binding)) {
                    return true;
                }

                try {
                    EventDao eventDao = DatabaseHelper.getEventDao();
                    eventDao.saveAndSnapshot(event);

                    if (RetroProvider.isConnected()) {
                        SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.ChangesOnly, this, new SyncCallback() {
                            @Override
                            public void call(boolean syncFailed, String syncFailedMessage) {
                                if (syncFailed) {
                                    Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_sync_error_saved), getResources().getString(R.string.entity_alert)), Snackbar.LENGTH_LONG).show();
                                } else {
                                    Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_alert)), Snackbar.LENGTH_LONG).show();
                                }
                                finish();
                            }
                        });
                    } else {
                        Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_alert)), Snackbar.LENGTH_LONG).show();
                        finish();
                    }
                } catch (DaoException e) {
                    Log.e(getClass().getName(), "Error while trying to build alert", e);
                    Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_alert)), Snackbar.LENGTH_LONG).show();
                    ErrorReportingHelper.sendCaughtException(tracker, e, event, true);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
