package de.symeda.sormas.app.contact;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.AbstractEditTabActivity;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskDao;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.backend.visit.VisitDao;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.databinding.ContactDataFragmentLayoutBinding;
import de.symeda.sormas.app.databinding.PersonEditFragmentLayoutBinding;
import de.symeda.sormas.app.person.PersonEditForm;
import de.symeda.sormas.app.task.TaskForm;
import de.symeda.sormas.app.task.TasksListFragment;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.validation.PersonValidator;
import de.symeda.sormas.app.visit.VisitEditActivity;
import de.symeda.sormas.app.visit.VisitEditDataForm;
import de.symeda.sormas.app.visit.VisitsListFragment;

public class ContactEditActivity extends AbstractEditTabActivity {

    public static final String KEY_CASE_UUID = "caseUuid";
    public static final String KEY_CONTACT_UUID = "contactUuid";
    public static final String KEY_PAGE = "page";

    private ContactEditPagerAdapter adapter;
    private String caseUuid;
    private String contactUuid;
    private String taskUuid;

    @Override
    public boolean isEditing() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.case_edit_activity_layout);

        // This makes sure that the given amount of tabs is kept in memory, which means that
        // Android doesn't call onResume when the tab has no focus which would otherwise lead
        // to certain spinners not displaying their values
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getText(R.string.headline_contact));
        }

        Bundle params = getIntent().getExtras();
        if (params != null) {
            if (params.containsKey(KEY_CASE_UUID)) {
                caseUuid = params.getString(KEY_CASE_UUID);
            }
            if (params.containsKey(KEY_CONTACT_UUID)) {
                contactUuid = params.getString(KEY_CONTACT_UUID);
                Contact initialEntity = DatabaseHelper.getContactDao().queryUuid(contactUuid);
                // If the contact has been removed from the database in the meantime, redirect the user to the contacts overview
                if (initialEntity == null) {
                    Intent intent = new Intent(this, ContactsActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }

                if (toolbar != null) {
                    getSupportActionBar().setSubtitle(initialEntity.toString());
                }

                DatabaseHelper.getContactDao().markAsRead(initialEntity);
            }
            if (params.containsKey(TaskForm.KEY_TASK_UUID)) {
                taskUuid = params.getString(TaskForm.KEY_TASK_UUID);
            }
            if (params.containsKey(KEY_PAGE)) {
                currentTab = params.getInt(KEY_PAGE);
            }
        }

        setAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Contact currentEntity = DatabaseHelper.getContactDao().queryUuid(contactUuid);
        // If the contact has been removed from the database in the meantime, redirect the user to the contacts overview
        if (currentEntity == null) {
            Intent intent = new Intent(this, ContactsActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (currentEntity.isUnreadOrChildUnread()) {
            // Resetting the adapter will reload the form and therefore also override any unsaved changes
            DatabaseHelper.getContactDao().markAsRead(currentEntity);
            setAdapter();
            final Snackbar snackbar = Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_entity_overridden), getResources().getString(R.string.entity_contact)), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.snackbar_okay, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }
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
        User user = ConfigProvider.getUser();
        ContactEditTabs tab = adapter.getTabForPosition(currentTab);
        switch (tab) {
            // contact data tab
            case CONTACT_DATA:
                updateActionBarGroups(menu, false, false, true, false, true);
                break;

            // person tab
            case PERSON:
                updateActionBarGroups(menu, true, false, true, false, true);
                break;

            // tasks tab
            case VISITS:
                updateActionBarGroups(menu, false, true, true, user.hasUserRight(UserRight.VISIT_CREATE), false);
                break;

            // tasks tab
            case TASKS:
                updateActionBarGroups(menu, false, true, true, false, false);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        currentTab = pager.getCurrentItem();
        ContactEditTabs tab = adapter.getTabForPosition(currentTab);
        Contact contact = (Contact) getData(adapter.getPositionOfTab(ContactEditTabs.CONTACT_DATA));
        Person person = (Person) getData(adapter.getPositionOfTab(ContactEditTabs.PERSON));

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

            case R.id.action_markAllAsRead:
                switch (tab) {
                    case VISITS:
                        VisitDao visitDao = DatabaseHelper.getVisitDao();
                        List<Visit> visits = visitDao.getByContact(contact);
                        for (Visit visitToMark : visits) {
                            visitDao.markAsRead(visitToMark);
                        }

                        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                            if (fragment instanceof VisitsListFragment) {
                                fragment.onResume();
                            }
                        }
                        break;
                    case TASKS:
                        TaskDao taskDao = DatabaseHelper.getTaskDao();
                        List<Task> tasks = taskDao.queryByContact(contact);
                        for (Task taskToMark : tasks) {
                            taskDao.markAsRead(taskToMark);
                        }

                        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                            if (fragment instanceof TasksListFragment) {
                                fragment.onResume();
                            }
                        }
                        break;
                }
                return true;

            // Report problem button
            case R.id.action_report:
                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName() + ":" + tab.toString(), contact.getUuid());
                AlertDialog dialog = userReportDialog.create();
                dialog.show();

                return true;

            // Save button
            case R.id.action_save:
                ContactDao contactDao = DatabaseHelper.getContactDao();

                // Validation
                ContactDataFragmentLayoutBinding contactDataBinding = ((ContactEditDataForm)getTabByPosition(adapter.getPositionOfTab(ContactEditTabs.CONTACT_DATA))).getBinding();
                PersonEditFragmentLayoutBinding personBinding = ((PersonEditForm)getTabByPosition(adapter.getPositionOfTab(ContactEditTabs.PERSON))).getBinding();

                PersonValidator.clearErrors(personBinding);

                int validationErrorTab = -1;

                if (!PersonValidator.validatePersonData(person, personBinding)) {
                    validationErrorTab = adapter.getPositionOfTab(ContactEditTabs.PERSON);
                }

                if (validationErrorTab >= 0) {
                    pager.setCurrentItem(validationErrorTab);
                    return true;
                }

                try {

                    if (contact.getRelationToCase() == ContactRelation.SAME_HOUSEHOLD && person.getAddress().isEmptyLocation()) {
                        Case contactCase = DatabaseHelper.getCaseDao().queryUuidBasic(contact.getCaseUuid());
                        if (contactCase != null) {
                            person.getAddress().setRegion(contactCase.getRegion());
                            person.getAddress().setDistrict(contactCase.getDistrict());
                            person.getAddress().setCommunity(contactCase.getCommunity());
                        }
                    }

                    PersonDao personDao = DatabaseHelper.getPersonDao();
                    personDao.saveAndSnapshot(person);
                    contactDao.saveAndSnapshot(contact);

                    reloadTabs();
                    pager.setCurrentItem(currentTab);
                    Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_contact)), Snackbar.LENGTH_LONG).show();

                } catch (DaoException e) {
                    Log.e(getClass().getName(), "Error while trying to saveAndSnapshot contact", e);
                    Snackbar.make(findViewById(R.id.base_layout), String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_contact)), Snackbar.LENGTH_LONG).show();
                    ErrorReportingHelper.sendCaughtException(tracker, e, contact, true);
                }

                return true;

            // Add button
            case R.id.action_add:
                switch (tab) {
                    case VISITS:
                        // only contact officer is allowd to build visits
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

    private void setAdapter() {
        List<ContactEditTabs> visibleTabs = buildVisibleTabsList();
        adapter = new ContactEditPagerAdapter(getSupportFragmentManager(), contactUuid, visibleTabs);
        createTabViews(adapter);

        pager.setCurrentItem(currentTab);
    }

    private List<ContactEditTabs> buildVisibleTabsList() {
        User user = ConfigProvider.getUser();
        List<ContactEditTabs> visibleTabs = new ArrayList<>();
        visibleTabs.addAll(Arrays.asList(ContactEditTabs.CONTACT_DATA, ContactEditTabs.PERSON,
                ContactEditTabs.VISITS));

        if (user.hasUserRight(UserRight.TASK_VIEW)) {
            visibleTabs.add(ContactEditTabs.TASKS);
        }

        return visibleTabs;
    }

}
