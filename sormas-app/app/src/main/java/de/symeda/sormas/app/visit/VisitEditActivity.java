package de.symeda.sormas.app.visit;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.caze.SymptomsEditForm;
import de.symeda.sormas.app.component.AbstractEditActivity;
import de.symeda.sormas.app.component.HelpDialog;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.ConnectionHelper;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.SyncCallback;
import de.symeda.sormas.app.util.ValidationFailedException;


public class VisitEditActivity extends AbstractEditActivity {

    public static final String VISIT_UUID = "visitUuid";
    public static final String KEY_CONTACT_UUID = "contactUuid";
    public static final String KEY_PAGE = "page";
    public static final String KEY_PARENT_TASK_UUID = "taskUuid";

    private VisitEditPagerAdapter adapter;
    private String contactUuid;
//    private String visitUuid;

    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.case_edit_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_visit) + " - " + ConfigProvider.getUser().getUserRole().toShortString());
        }

        SormasApplication application = (SormasApplication) getApplication();
        tracker = application.getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle params = getIntent().getExtras();
        adapter = new VisitEditPagerAdapter(getSupportFragmentManager(), params);
        createTabViews(adapter);

        if (params != null && params.containsKey(KEY_PAGE)) {
            currentTab = params.getInt(KEY_PAGE);
        }
        if (params != null && params.containsKey(KEY_CONTACT_UUID)) {
            this.contactUuid = (String) params.get(KEY_CONTACT_UUID);
        }
        pager.setCurrentItem(currentTab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_action_bar, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        VisitEditTabs tab = VisitEditTabs.values()[currentTab];
        switch(tab) {
            case VISIT_DATA:
                updateActionBarGroups(menu, false, true, false, true);
                break;

            case SYMPTOMS:
                updateActionBarGroups(menu, true, true, false, true);
                break;

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        currentTab = pager.getCurrentItem();
        VisitEditTabs tab = VisitEditTabs.values()[currentTab];

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;

            // Help button
            case R.id.action_help:
                HelpDialog helpDialog = new HelpDialog(this);

                switch(tab) {
                    case SYMPTOMS:
                        String helpText = HelpDialog.getHelpForForm((LinearLayout) this.findViewById(R.id.case_symptoms_form));
                        helpDialog.setMessage(Html.fromHtml(helpText).toString());
                        break;
                }

                helpDialog.show();

                return true;

            // Report problem button
            case R.id.action_report:
                Visit visit = (Visit) adapter.getData(VisitEditTabs.VISIT_DATA.ordinal());

                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName() + ":" + tab.toString(), visit.getUuid());
                AlertDialog dialog = userReportDialog.create();
                dialog.show();

                return true;

            // Save button
            case R.id.action_save:
                visit = (Visit) adapter.getData(VisitEditTabs.VISIT_DATA.ordinal());
                Symptoms symptoms = (Symptoms)adapter.getData(VisitEditTabs.SYMPTOMS.ordinal());
                SymptomsEditForm symptomsEditForm = (SymptomsEditForm) adapter.getTabByPosition(VisitEditTabs.SYMPTOMS.ordinal());

                Contact contact = DatabaseHelper.getContactDao().queryUuid(contactUuid);
                if (visit.getVisitDateTime().before(contact.getLastContactDate()) &&
                        DateHelper.getDaysBetween(visit.getVisitDateTime(), contact.getLastContactDate()) > 10) {
                    Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_visit_10_days_before, Snackbar.LENGTH_LONG).show();
                    return true;
                }

                if (contact.getFollowUpUntil() != null && visit.getVisitDateTime().after(contact.getFollowUpUntil()) &&
                        DateHelper.getDaysBetween(contact.getFollowUpUntil(), visit.getVisitDateTime()) > 10) {
                    Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_visit_10_days_after, Snackbar.LENGTH_LONG).show();
                    return true;
                }

                try {
                    symptomsEditForm.validateVisitData(symptoms, visit.getVisitStatus() == VisitStatus.COOPERATIVE);
                } catch (ValidationFailedException e) {
                    Snackbar.make(findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();

                    // if any symptomsfield is required, change pager to symptoms tab
                    if (currentTab != VisitEditTabs.SYMPTOMS.ordinal()) {
                        pager.setCurrentItem(VisitEditTabs.SYMPTOMS.ordinal());
                    }
                    return true;
                }

                boolean dateTimeReq = visit.getVisitDateTime() == null;
                boolean visitStatusReq = visit.getVisitStatus() == null;

                boolean isValid = !dateTimeReq && !visitStatusReq;

                // method returns a String, null means that there is no error message and thus
                // the data is valid
                if (isValid) {
                    try {
                        if (symptoms != null) {
                            visit.setSymptoms(symptoms);
                            if (!DatabaseHelper.getSymptomsDao().save(symptoms)) {
                                throw new DaoException();
                            }
                        }

                        visit.setVisitUser(ConfigProvider.getUser());

                        if (!DatabaseHelper.getVisitDao().save(visit)) {
                            throw new DaoException();
                        }

                        if (ConnectionHelper.isConnectedToInternet(getApplicationContext())) {
                            SyncVisitsTask.syncVisitsWithProgressDialog(this, new SyncCallback() {
                                @Override
                                public void call(boolean syncFailed) {
                                    if (syncFailed) {
                                        Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_sync_error_saved), getResources().getString(R.string.entity_visit)), Snackbar.LENGTH_LONG).show();
                                    } else {
                                        Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_visit)), Snackbar.LENGTH_LONG).show();
                                    }
                                    finish();
                                }
                            });
                        } else {
                            Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_visit)), Snackbar.LENGTH_LONG).show();
                            finish();
                        }
                    } catch (DaoException e) {
                        Log.e(getClass().getName(), "Error while trying to save visit", e);
                        Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_visit)), Snackbar.LENGTH_LONG).show();
                        ErrorReportingHelper.sendCaughtException(tracker, e, visit, true);
                    }
                } else {
                    if (dateTimeReq) {
                        Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_visit_date_time, Snackbar.LENGTH_LONG).show();
                    } else if (visitStatusReq) {
                        Snackbar.make(findViewById(R.id.base_layout), R.string.snackbar_visit_status, Snackbar.LENGTH_LONG).show();
                    }
                }

                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
