package de.symeda.sormas.app.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.caze.CaseEditActivity;
import de.symeda.sormas.app.component.SelectOrCreatePersonDialogBuilder;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.util.Consumer;
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

                boolean lastContactDateReq = contact.getLastContactDate() == null || contact.getLastContactDate().getTime() > contact.getReportDateTime().getTime();
                boolean proximityReq = contact.getContactProximity() == null;
                boolean firstNameReq = contact.getPerson().getFirstName().isEmpty();
                boolean lastNameReq = contact.getPerson().getLastName().isEmpty();
                boolean relationReq = contact.getRelationToCase() == null;

                boolean validData = !lastContactDateReq && !proximityReq && !firstNameReq && !lastNameReq && !relationReq;

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
                                            Snackbar.make(findViewById(R.id.fragment_frame), String.format(getResources().getString(R.string.snackbar_create_error), getResources().getString(R.string.entity_contact)), Snackbar.LENGTH_LONG).show();
                                            ErrorReportingHelper.sendCaughtException(tracker, e, null, true);
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
                        Snackbar.make(findViewById(R.id.fragment_frame), String.format(getResources().getString(R.string.snackbar_create_error), getResources().getString(R.string.entity_contact)), Snackbar.LENGTH_LONG).show();
                        ErrorReportingHelper.sendCaughtException(tracker, e, null, true);
                    }
                } else {
                    if (lastContactDateReq) {
                        Snackbar.make(findViewById(R.id.fragment_frame), R.string.snackbar_contact_last_contact_date, Snackbar.LENGTH_LONG).show();
                    } else if (proximityReq) {
                        Snackbar.make(findViewById(R.id.fragment_frame), R.string.snackbar_contact_proximity, Snackbar.LENGTH_LONG).show();
                    } else if (firstNameReq) {
                        Snackbar.make(findViewById(R.id.fragment_frame), R.string.snackbar_contact_firstName, Snackbar.LENGTH_LONG).show();
                    } else if (lastNameReq) {
                        Snackbar.make(findViewById(R.id.fragment_frame), R.string.snackbar_contact_lastName, Snackbar.LENGTH_LONG).show();
                    } else if (relationReq) {
                        Snackbar.make(findViewById(R.id.fragment_frame), R.string.snackbar_contact_relation, Snackbar.LENGTH_LONG).show();
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

        if(contact.getRelationToCase() == ContactRelation.SAME_HOUSEHOLD && person.getAddress().isEmptyLocation()) {
            person.getAddress().setRegion(contact.getCaze().getRegion());
            person.getAddress().setDistrict(contact.getCaze().getDistrict());
            person.getAddress().setCommunity(contact.getCaze().getCommunity());
        }

        // save
        PersonDao personDao = DatabaseHelper.getPersonDao();
        ContactDao contactDao = DatabaseHelper.getContactDao();
        personDao.saveAndSnapshot(person);
        personDao.markAsRead(person);
        contactDao.saveAndSnapshot(contact);
        contactDao.markAsRead(contact);

        if (RetroProvider.isConnected()) {
            SyncContactsTask.syncContactsWithProgressDialog(this, new SyncCallback() {
                @Override
                public void call(boolean syncFailed) {
                    if (syncFailed) {
                        Snackbar.make(findViewById(R.id.fragment_frame), String.format(getResources().getString(R.string.snackbar_sync_error_created), getResources().getString(R.string.entity_contact)), Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(findViewById(R.id.fragment_frame), String.format(getResources().getString(R.string.snackbar_create_success), getResources().getString(R.string.entity_contact)), Snackbar.LENGTH_LONG).show();
                    }
                    navBackToCaseContacts();
                }
            });
        } else {
            Snackbar.make(findViewById(R.id.fragment_frame), String.format(getResources().getString(R.string.snackbar_create_success), getResources().getString(R.string.entity_contact)), Snackbar.LENGTH_LONG).show();
            navBackToCaseContacts();
        }

    }



}
