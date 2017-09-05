package de.symeda.sormas.app.sample;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.Date;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleDao;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.databinding.SampleDataFragmentLayoutBinding;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.SyncCallback;
import de.symeda.sormas.app.validation.SampleValidator;

/**
 * Created by Mate Strysewske on 07.02.2017.
 */

public class SampleEditActivity extends AbstractSormasActivity {

    public static final String NEW_SAMPLE = "newSample";
    public static final String KEY_SAMPLE_UUID = "sampleUuid";
    public static final String KEY_CASE_UUID = "caseUuid";

    private SampleEditForm sampleTab;

    private String sampleUuid;

    private Bundle params;

    @Override
    public boolean isEditing() {
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle extras = intent.getExtras();
        if(extras != null) {

        }
    }

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
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_sample) + " - " + ConfigProvider.getUser().getUserRole().toShortString());
        }

        params = getIntent().getExtras();
        if (params != null) {
            if (params.containsKey(NEW_SAMPLE)) {
                getSupportActionBar().setTitle(getResources().getText(R.string.headline_new_sample));
            } else {
                getSupportActionBar().setTitle(getResources().getText(R.string.headline_sample));
            }

            if (params.containsKey(KEY_SAMPLE_UUID)) {
                sampleUuid = params.getString(KEY_SAMPLE_UUID);
                Sample initialEntity = DatabaseHelper.getSampleDao().queryUuid(sampleUuid);
                // If the sample has been removed from the database in the meantime, redirect the user to the samples overview
                if (initialEntity == null) {
                    Intent intent = new Intent(this, SamplesActivity.class);
                    startActivity(intent);
                    finish();
                }

                DatabaseHelper.getSampleDao().markAsRead(initialEntity);
            }
        }

        setAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sampleUuid != null) {
            Sample currentEntity = DatabaseHelper.getSampleDao().queryUuid(sampleUuid);
            // If the sample has been removed from the database in the meantime, redirect the user to the samples overview
            if (currentEntity == null) {
                Intent intent = new Intent(this, SamplesActivity.class);
                startActivity(intent);
                finish();
            }

            if (currentEntity.isUnreadOrChildUnread()) {
                // Resetting the adapter will reload the form and therefore also override any unsaved changes
                DatabaseHelper.getSampleDao().markAsRead(currentEntity);
                setAdapter();
                final Snackbar snackbar = Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_entity_overridden), getResources().getString(R.string.entity_sample)), Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.snackbar_okay, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Bundle params = getIntent().getExtras();
        if (params != null) {
            if (params.containsKey(KEY_SAMPLE_UUID)) {
                outState.putString(KEY_SAMPLE_UUID, sampleUuid);
            }
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_action_bar, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.setGroupVisible(R.id.group_action_help,false);
        menu.setGroupVisible(R.id.group_action_add,false);
        menu.setGroupVisible(R.id.group_action_save,true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            // Report problem button
            case R.id.action_report:
                Sample sample = (Sample) sampleTab.getData();

                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), sample.getUuid());
                AlertDialog dialog = userReportDialog.create();
                dialog.show();

                return true;

            case R.id.action_save:
                SampleDao sampleDao = DatabaseHelper.getSampleDao();
                sample = (Sample) sampleTab.getData();

                if (sample.getReportingUser() == null) {
                    sample.setReportingUser(ConfigProvider.getUser());
                }
                if (sample.getReportDateTime() == null) {
                    sample.setReportDateTime(new Date());
                }

                // Validation
                SampleDataFragmentLayoutBinding binding = sampleTab.getBinding();
                SampleValidator.clearErrorsForSampleData(binding);
                if (!SampleValidator.validateSampleData(sample, binding)) {
                    return true;
                }

                try {
                    sampleDao.saveAndSnapshot(sample);
                    Snackbar.make(findViewById(R.id.fragment_frame), "Sample " + DataHelper.getShortUuid(sample.getUuid()) + " saved", Snackbar.LENGTH_LONG).show();

                    if (RetroProvider.isConnected()) {
                        SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.ChangesOnly, this, new SyncCallback() {
                            @Override
                            public void call(boolean syncFailed, String syncFailedMessage) {
                                if (syncFailed) {
                                    Snackbar.make(findViewById(R.id.fragment_frame), String.format(getResources().getString(R.string.snackbar_sync_error_saved), getResources().getString(R.string.entity_sample)), Snackbar.LENGTH_LONG).show();
                                } else {
                                    Snackbar.make(findViewById(R.id.fragment_frame), String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_sample)), Snackbar.LENGTH_LONG).show();
                                }
                                finish();
                            }
                        });
                    } else {
                        Snackbar.make(findViewById(R.id.fragment_frame), String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_sample)), Snackbar.LENGTH_LONG).show();
                        finish();
                    }
                } catch (DaoException e) {
                    Log.e(getClass().getName(), "Error while trying to save sample", e);
                    Snackbar.make(findViewById(R.id.fragment_frame), String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_sample)), Snackbar.LENGTH_LONG).show();
                    ErrorReportingHelper.sendCaughtException(tracker, e, sample, true);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setAdapter() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        sampleTab = new SampleEditForm();
        sampleTab.setArguments(params);
        ft.replace(R.id.fragment_frame, sampleTab).commit();
    }

}
