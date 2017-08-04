package de.symeda.sormas.app.visit;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.backend.visit.VisitDao;
import de.symeda.sormas.app.caze.SymptomsEditForm;
import de.symeda.sormas.app.AbstractEditTabActivity;
import de.symeda.sormas.app.component.HelpDialog;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.databinding.CaseSymptomsFragmentLayoutBinding;
import de.symeda.sormas.app.databinding.VisitDataFragmentLayoutBinding;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.SyncCallback;
import de.symeda.sormas.app.validation.SymptomsValidator;
import de.symeda.sormas.app.validation.VisitValidator;


public class VisitEditActivity extends AbstractEditTabActivity {

    public static final String VISIT_UUID = "visitUuid";
    public static final String KEY_CONTACT_UUID = "contactUuid";
    public static final String KEY_PAGE = "page";
    public static final String KEY_PARENT_TASK_UUID = "taskUuid";

    private VisitEditPagerAdapter adapter;
    private String contactUuid;
    private String visitUuid;

    private Bundle params;

    @Override
    public boolean isEditing() {
        return true;
    }

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

        params = getIntent().getExtras();

        if (params != null && params.containsKey(Visit.UUID)) {
            visitUuid = params.getString(Visit.UUID);
            Visit initialEntity = DatabaseHelper.getVisitDao().queryUuid(visitUuid);
            DatabaseHelper.getVisitDao().markAsRead(initialEntity);
        }
        if (params != null && params.containsKey(KEY_PAGE)) {
            currentTab = params.getInt(KEY_PAGE);
        }
        if (params != null && params.containsKey(KEY_CONTACT_UUID)) {
            this.contactUuid = (String) params.get(KEY_CONTACT_UUID);
        }

        setAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (visitUuid != null) {
            Visit currentEntity = DatabaseHelper.getVisitDao().queryUuid(visitUuid);
            if (currentEntity.isUnreadOrChildUnread()) {
                // Resetting the adapter will reload the form and therefore also override any unsaved changes
                setAdapter();
                final Snackbar snackbar = Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_entity_overridden), getResources().getString(R.string.entity_visit)), Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.snackbar_okay, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
            }

            DatabaseHelper.getVisitDao().markAsRead(currentEntity);
        }
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
                updateActionBarGroups(menu, false, false, true, false, true);
                break;

            case SYMPTOMS:
                updateActionBarGroups(menu, true, false, true, false, true);
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

                VisitDataFragmentLayoutBinding visitDataBinding = ((VisitEditDataForm)adapter.getTabByPosition(VisitEditTabs.VISIT_DATA.ordinal())).getBinding();
                CaseSymptomsFragmentLayoutBinding symptomsBinding = symptomsEditForm.getBinding();

                // Necessary because the entry could've been automatically set, in which case the setValue method of the
                // custom field has not been called
                symptoms.setOnsetSymptom((String) symptomsBinding.symptomsOnsetSymptom1.getValue());

                VisitValidator.clearErrorsForVisitData(visitDataBinding);
                SymptomsValidator.clearErrorsForSymptoms(symptomsBinding);

                int validationErrorTab = -1;

                if (!SymptomsValidator.validateVisitSymptoms(visit, symptoms, symptomsBinding)) {
                    validationErrorTab = VisitEditTabs.SYMPTOMS.ordinal();
                }
                if (!VisitValidator.validateVisitData(visit, contact, visitDataBinding)) {
                    validationErrorTab = VisitEditTabs.VISIT_DATA.ordinal();
                }

                if (validationErrorTab >= 0) {
                    pager.setCurrentItem(validationErrorTab);
                    return true;
                }

                try {
                    visit.setSymptoms(symptoms);
                    visit.setVisitUser(ConfigProvider.getUser());
                    VisitDao visitDao = DatabaseHelper.getVisitDao();
                    visitDao.saveAndSnapshot(visit);

                    if (RetroProvider.isConnected()) {
                        SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.ChangesOnly, this, new SyncCallback() {
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

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void notifyVisitStatusChange(boolean cooperative) {
        SymptomsEditForm symptomsEditForm = (SymptomsEditForm) adapter.getTabByPosition(VisitEditTabs.SYMPTOMS.ordinal());
        symptomsEditForm.changeVisitCooperative(cooperative);
    }

    private void setAdapter() {
        adapter = new VisitEditPagerAdapter(getSupportFragmentManager(), params);
        createTabViews(adapter);
        pager.setCurrentItem(currentTab);
    }

}
