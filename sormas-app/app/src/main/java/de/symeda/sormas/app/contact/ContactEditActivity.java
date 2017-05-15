package de.symeda.sormas.app.contact;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.caze.CaseEditTabs;
import de.symeda.sormas.app.component.AbstractEditActivity;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.task.TaskEditActivity;
import de.symeda.sormas.app.task.TaskForm;
import de.symeda.sormas.app.util.ConnectionHelper;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.SyncCallback;
import de.symeda.sormas.app.visit.VisitEditActivity;
import de.symeda.sormas.app.visit.VisitEditDataForm;

public class ContactEditActivity extends AbstractEditActivity {

    public static final String KEY_CASE_UUID = "caseUuid";
    public static final String KEY_CONTACT_UUID = "contactUuid";
    public static final String KEY_PAGE = "page";

    private ContactEditPagerAdapter adapter;
    private String caseUuid;
    private String contactUuid;
    private String taskUuid;

    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.case_edit_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_contact) + " - " + ConfigProvider.getUser().getUserRole().toShortString());
        }

        SormasApplication application = (SormasApplication) getApplication();
        tracker = application.getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle params = getIntent().getExtras();
        if (params != null) {
            if (params.containsKey(KEY_CASE_UUID)) {
                caseUuid = params.getString(KEY_CASE_UUID);
            }
            if (params.containsKey(KEY_CONTACT_UUID)) {
                contactUuid = params.getString(KEY_CONTACT_UUID);
            }
            if (params.containsKey(TaskForm.KEY_TASK_UUID)) {
                taskUuid = params.getString(TaskForm.KEY_TASK_UUID);
            }
            if (params.containsKey(KEY_PAGE)) {
                currentTab = params.getInt(KEY_PAGE);
            }
        }
        adapter = new ContactEditPagerAdapter(getSupportFragmentManager(), contactUuid);
        createTabViews(adapter);

        pager.setCurrentItem(currentTab);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Bundle params = getIntent().getExtras();
        if (params != null) {
            if (params.containsKey(KEY_CASE_UUID)) {
                outState.putString(KEY_CASE_UUID, caseUuid);
            }
            if (params.containsKey(KEY_CONTACT_UUID)) {
                outState.putString(KEY_CONTACT_UUID, contactUuid);
            }
            if (params.containsKey(TaskForm.KEY_TASK_UUID)) {
                outState.putString(TaskForm.KEY_TASK_UUID, taskUuid);
            }
            if (params.containsKey(KEY_PAGE)) {
                outState.putInt(KEY_PAGE, currentTab);
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
        ContactEditTabs tab = ContactEditTabs.values()[currentTab];
        switch (tab) {
            // contact data tab
            case CONTACT_DATA:
                updateActionBarGroups(menu, false, true, false, true);
                break;

            // person tab
            case PERSON:
                updateActionBarGroups(menu, true, true, false, true);
                break;

            // tasks tab
            case VISITS:
                updateActionBarGroups(menu, false, true, true, false);
                break;

            // tasks tab
            case TASKS:
                updateActionBarGroups(menu, false, true, false, false);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        currentTab = pager.getCurrentItem();
        ContactEditTabs tab = ContactEditTabs.values()[currentTab];

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if (taskUuid != null) {
                    finish();
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }

                return true;

            // Help button
            case R.id.action_help:
                // @TODO help for contact edit tabs
                return true;

            // Report problem button
            case R.id.action_report:
                Contact contact = (Contact) adapter.getData(ContactEditTabs.CONTACT_DATA.ordinal());

                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName() + ":" + tab.toString(), contact.getUuid());
                AlertDialog dialog = userReportDialog.create();
                dialog.show();

                return true;

            // Save button
            case R.id.action_save:
                ContactDao contactDao = DatabaseHelper.getContactDao();
                contact = (Contact) adapter.getData(ContactEditTabs.CONTACT_DATA.ordinal());

                boolean validData = true;

                if (contact.getLastContactDate()==null || contact.getLastContactDate().getTime() > contact.getReportDateTime().getTime()) {
                    validData = false;
                    Snackbar.make(findViewById(R.id.base_layout), "Please make sure contact date is set and not in the future.", Snackbar.LENGTH_LONG).show();
                }

                if (contact.getContactProximity()==null) {
                    validData = false;
                    Snackbar.make(findViewById(R.id.base_layout), "Please select a contact type.", Snackbar.LENGTH_LONG).show();
                }

                if (contact.getPerson().getFirstName().isEmpty() || contact.getPerson().getLastName().isEmpty() ) {
                    validData = false;
                    Snackbar.make(findViewById(R.id.base_layout), "Please select a person.", Snackbar.LENGTH_LONG).show();
                }

                if (contact.getRelationToCase() == null) {
                    validData = false;
                    Snackbar.make(findViewById(R.id.base_layout), "Please select a relationship with the case.", Snackbar.LENGTH_LONG).show();
                }

                if (validData) {
                    try {
                        contactDao.save(contact);

                        Person person = (Person) adapter.getData(ContactEditTabs.PERSON.ordinal());
                        if (person.getAddress() == null) {
                            person.setAddress(DataUtils.createNew(Location.class));
                        }

                        if (contact.getRelationToCase() == ContactRelation.SAME_HOUSEHOLD && person.getAddress().isEmptyLocation()) {
                            person.getAddress().setRegion(contact.getCaze().getRegion());
                            person.getAddress().setDistrict(contact.getCaze().getDistrict());
                            person.getAddress().setCommunity(contact.getCaze().getCommunity());
                        }

                        DatabaseHelper.getLocationDao().save(person.getAddress());
                        DatabaseHelper.getPersonDao().save(person);


                        Snackbar.make(findViewById(R.id.base_layout), "Contact " + DataHelper.getShortUuid(contact.getUuid()) + " saved", Snackbar.LENGTH_LONG).show();

                        if (ConnectionHelper.isConnectedToInternet(getApplicationContext())) {
                            SyncContactsTask.syncContactsWithProgressDialog(this, new SyncCallback() {
                                @Override
                                public void call(boolean syncFailed) {
                                    onResume();
                                    pager.setCurrentItem(currentTab);
                                    if (syncFailed) {
                                        Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_sync_error_saved), getResources().getString(R.string.entity_contact)), Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    } catch (DaoException e) {
                        Log.e(getClass().getName(), "Error while trying to save contact", e);
                        Snackbar.make(findViewById(R.id.base_layout), "Contact could not be saved because of an internal error.", Snackbar.LENGTH_LONG).show();
                        ErrorReportingHelper.sendCaughtException(tracker, e, contact, true);
                    }
                }
                return true;

            // Add button
            case R.id.action_add:
                switch (tab) {
                    case VISITS:
                        // only contact officer is allowd to create visits
//                        if(UserRole.CONTACT_OFFICER.equals(ConfigProvider.getUser().getUserRoleName())) {
                        Bundle visitBundle = new Bundle();
                        visitBundle.putString(KEY_CONTACT_UUID, contactUuid);
                        visitBundle.putBoolean(VisitEditDataForm.NEW_VISIT, true);
                        Intent intentVisitEdit = new Intent(this, VisitEditActivity.class);
                        intentVisitEdit.putExtras(visitBundle);
                        startActivity(intentVisitEdit);
//                        }
                        break;
                }

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

}
