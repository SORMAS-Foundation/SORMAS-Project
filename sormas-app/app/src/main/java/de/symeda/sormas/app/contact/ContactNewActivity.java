package de.symeda.sormas.app.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.caze.CaseEditActivity;
import de.symeda.sormas.app.component.SelectOrCreatePersonDialogBuilder;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.person.SyncPersonsTask;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.ConnectionHelper;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.SyncCallback;


/**
 * Created by Stefan Szczesny on 02.11.2016.
 */
public class ContactNewActivity extends AppCompatActivity {

    private String caseUuid;
    private ContactNewForm contactNewForm;

    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle params = getIntent().getExtras();
        caseUuid = params.getString(CaseEditActivity.KEY_CASE_UUID);

        setContentView(R.layout.sormas_root_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_new_contact) + " - " + ConfigProvider.getUser().getUserRole().toShortString());
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        contactNewForm = new ContactNewForm();
        ft.add(R.id.fragment_frame, contactNewForm).commit();

        SormasApplication application = (SormasApplication) getApplication();
        tracker = application.getDefaultTracker();
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
                //Home/back button
//                NavUtils.navigateUpFromSameTask(this);
                navBackToCaseContacts();
                return true;

            // Report problem button
            case R.id.action_report:
                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), null);
                android.app.AlertDialog dialog = userReportDialog.create();
                dialog.show();

                return true;

            case R.id.action_save:
                final Contact contact = contactNewForm.getData();

                contact.setContactClassification(ContactClassification.POSSIBLE);
                contact.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
                contact.setReportingUser(ConfigProvider.getUser());
                contact.setReportDateTime(new Date());
                contact.setCaze(DatabaseHelper.getCaseDao().queryUuid(caseUuid));

                boolean validData = true;

                if (contact.getLastContactDate()==null || contact.getLastContactDate().getTime() > contact.getReportDateTime().getTime()) {
                    validData = false;
                    Toast.makeText(this, "Please make sure contact date is set and not in the future.", Toast.LENGTH_SHORT).show();
                }

                if (contact.getContactProximity()==null) {
                    validData = false;
                    Toast.makeText(this, "Please set a contact type.", Toast.LENGTH_SHORT).show();
                }

                if (contact.getPerson().getFirstName().isEmpty() || contact.getPerson().getLastName().isEmpty() ) {
                    validData = false;
                    Toast.makeText(this, "Please select a person.", Toast.LENGTH_SHORT).show();
                }

                if (contact.getRelationToCase() == null) {
                    validData = false;
                    Toast.makeText(this, "Please select a relationship with the case.", Toast.LENGTH_SHORT).show();
                }

                if (validData) {
                    try {
                        List<Person> existingPersons = DatabaseHelper.getPersonDao().getAllByName(contact.getPerson().getFirstName(), contact.getPerson().getLastName());
                        if (existingPersons.size() > 0) {
                            AlertDialog.Builder dialogBuilder = new SelectOrCreatePersonDialogBuilder(this, contact.getPerson(), existingPersons, new Consumer() {
                                @Override
                                public void accept(Object parameter) {
                                    if (parameter instanceof Person) {
                                        try {
                                            contact.setPerson((Person) parameter);
                                            savePersonAndContact(contact);
                                        } catch (DaoException e) {
                                            Log.e(getClass().getName(), "Error while trying to create contact", e);
                                            Toast.makeText(getApplicationContext(), "Contact could not be created because of an internal error.", Toast.LENGTH_LONG).show();
                                            ErrorReportingHelper.sendCaughtException(tracker, this.getClass().getSimpleName(), e, null, true);
                                        }
                                    }
                                }
                            });
                            AlertDialog newPersonDialog = dialogBuilder.create();
                            newPersonDialog.show();
                            ((SelectOrCreatePersonDialogBuilder) dialogBuilder).setButtonListeners(newPersonDialog, this);
                        } else {
                            savePersonAndContact(contact);
                        }
                    } catch (DaoException e) {
                        Log.e(getClass().getName(), "Error while trying to create contact", e);
                        Toast.makeText(this, "Contact could not be created because of an internal error.", Toast.LENGTH_LONG).show();
                        ErrorReportingHelper.sendCaughtException(tracker, this.getClass().getSimpleName(), e, null, true);
                    }
                }

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void navBackToCaseContacts() {
        Intent intentCaseContacts = new Intent(this, CaseEditActivity.class);
        intentCaseContacts.putExtra(CaseEditActivity.KEY_PAGE, 5);
        intentCaseContacts.putExtra(CaseEditActivity.KEY_CASE_UUID, caseUuid);
        startActivity(intentCaseContacts);
    }

    private void savePersonAndContact(Contact contact) throws DaoException {
        Person person = contact.getPerson();

        if (person.getAddress() == null) {
            person.setAddress(DataUtils.createNew(Location.class));
        }

        if(contact.getRelationToCase() == ContactRelation.SAME_HOUSEHOLD && person.getAddress().isEmptyLocation()) {
            person.getAddress().setRegion(contact.getCaze().getRegion());
            person.getAddress().setDistrict(contact.getCaze().getDistrict());
            person.getAddress().setCommunity(contact.getCaze().getCommunity());
        }

        // save the person
        DatabaseHelper.getPersonDao().save(contact.getPerson());
//        new SyncPersonsTask(getApplicationContext()).execute();

        // save the contact
        DatabaseHelper.getContactDao().save(contact);

        Toast.makeText(this, "Contact to " + contact.getPerson().toString() + " saved", Toast.LENGTH_SHORT).show();

        if (ConnectionHelper.isConnectedToInternet(getApplicationContext())) {
            SyncContactsTask.syncContactsWithProgressDialog(this, new SyncCallback() {
                @Override
                public void call(boolean syncFailed) {
                    if (syncFailed) {
                        Toast.makeText(getApplicationContext(), "The case has been created, but could not yet be transferred to the server. An error report has been automatically sent and the synchronization will be repeated later.", Toast.LENGTH_LONG).show();
                    }
                    navBackToCaseContacts();
                }
            });
        } else {
            navBackToCaseContacts();
        }

    }



}
