package de.symeda.sormas.app.sample;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import java.util.Date;

import de.symeda.sormas.api.sample.ShipmentStatus;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleDao;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.ErrorReportingHelper;

/**
 * Created by Mate Strysewske on 07.02.2017.
 */

public class SampleEditActivity extends AppCompatActivity {

    public static final String NEW_SAMPLE = "newSample";
    public static final String KEY_SAMPLE_UUID = "sampleUuid";
    public static final String KEY_CASE_UUID = "caseUuid";

    private SampleEditForm sampleTab;

    private String sampleUuid;

    private Tracker tracker;

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

        setContentView(R.layout.sormas_root_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_sample) + " - " + ConfigProvider.getUser().getUserRole().toShortString());
        }

        // setting the fragment_frame
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        sampleTab = new SampleEditForm();
        sampleTab.setArguments(getIntent().getExtras());
        ft.add(R.id.fragment_frame, sampleTab).commit();

        SormasApplication application = (SormasApplication) getApplication();
        tracker = application.getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle params = getIntent().getExtras();
        if (params != null) {
            if (params.containsKey(NEW_SAMPLE)) {
                getSupportActionBar().setTitle(getResources().getText(R.string.headline_new_sample));
            } else {
                getSupportActionBar().setTitle(getResources().getText(R.string.headline_sample));
            }

            if (params.containsKey(KEY_SAMPLE_UUID)) {
                sampleUuid = params.getString(KEY_SAMPLE_UUID);
            }
        }

        sampleTab.onResume();
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
                CheckBox shipped = (CheckBox) findViewById(R.id.sample_shipmentStatus);
                if (shipped.isEnabled()) {
                    if (shipped.isChecked()) {
                        sample.setShipmentStatus(ShipmentStatus.SHIPPED);
                    } else {
                        sample.setShipmentStatus(ShipmentStatus.NOT_SHIPPED);
                        sample.setShipmentDate(null);
                    }
                }

                if (sample.getReportingUser() == null) {
                    sample.setReportingUser(ConfigProvider.getUser());
                }
                if (sample.getReportDateTime() == null) {
                    sample.setReportDateTime(new Date());
                }

                // check required fields
                boolean sampleDateTimeReq = sample.getSampleDateTime() == null;
                boolean sampleMaterialReq = sample.getSampleMaterial() == null;
                boolean sampleMaterialTextReq = sample.getSampleMaterial() != null && sample.getSampleMaterialText() == null;
                boolean shipmentStatusReq = sample.getShipmentStatus() == null;
                boolean shipmentDateReq = sample.getShipmentStatus() != null && sample.getShipmentStatus() != ShipmentStatus.NOT_SHIPPED && sample.getShipmentDate() == null;
                boolean sampleLabReq = sample.getLab() == null;

                boolean validData = !sampleDateTimeReq
                        && !sampleMaterialReq
                        && !sampleMaterialTextReq
                        && !shipmentStatusReq
                        && !shipmentDateReq
                        && !sampleLabReq;

                if (validData) {
                    try {
                        sampleDao.save(sample);
                        Toast.makeText(this, "sample " + DataHelper.getShortUuid(sample.getUuid()) + " saved", Toast.LENGTH_SHORT).show();

                        SyncSamplesTask.syncSamplesWithProgressDialog(this, new Callback() {
                            @Override
                            public void call() {
                                // go back to the list
                                finish();
                            }
                        });
                    } catch (DaoException e) {
                        Log.e(getClass().getName(), "Error while trying to save sample", e);
                        Toast.makeText(this, "Sample could not be saved because of an internal error.", Toast.LENGTH_LONG).show();
                        ErrorReportingHelper.sendCaughtException(tracker, this.getClass().getSimpleName(), e, sample, true);
                    }
                } else {
                    if (sampleDateTimeReq) {
                        Toast.makeText(this, "Not saved. Please specify the date and time of sampling.", Toast.LENGTH_LONG).show();
                    } else if (sampleMaterialReq) {
                        Toast.makeText(this, "Not saved. Please specify the sample material.", Toast.LENGTH_LONG).show();
                    } else if (sampleMaterialTextReq) {
                        Toast.makeText(this, "Not saved. Please specify the sample material.", Toast.LENGTH_LONG).show();
                    } else if (sampleLabReq) {
                        Toast.makeText(this, "Not saved. Please specify the laboratory.", Toast.LENGTH_LONG).show();
                    } else if (shipmentStatusReq) {
                        Toast.makeText(this, "Not saved. Please specify the shipment status..", Toast.LENGTH_LONG).show();
                    } else if (shipmentDateReq) {
                        Toast.makeText(this, "Not saved. Please specify the shipment date.", Toast.LENGTH_LONG).show();
                    }
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
