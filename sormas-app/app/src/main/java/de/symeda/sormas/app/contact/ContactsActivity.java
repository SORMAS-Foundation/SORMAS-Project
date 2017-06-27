package de.symeda.sormas.app.contact;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.AbstractRootTabActivity;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.component.UserReportDialog;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.SyncCallback;

public class ContactsActivity extends AbstractRootTabActivity {

    private ContactsListFilterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.contacts_activity_layout);
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.main_menu_contacts));
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter = new ContactsListFilterAdapter(getSupportFragmentManager());
        createTabViews(adapter);
        pager.setCurrentItem(currentTab);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contacts_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_reload:
                synchronizeChangedData();
                return true;

            case R.id.action_markAllAsRead:
                ContactDao contactDao = DatabaseHelper.getContactDao();
                PersonDao personDao = DatabaseHelper.getPersonDao();
                List<Contact> contacts = contactDao.queryForAll();
                for (Contact contactToMark : contacts) {
                    contactDao.markAsRead(contactToMark);
                    personDao.markAsRead(contactToMark.getPerson());
                }

                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment instanceof ContactsListFragment) {
                        fragment.onResume();
                    }
                }
                return true;

            // Report problem button
            case R.id.action_report:
                UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), null);
                AlertDialog dialog = userReportDialog.create();
                dialog.show();

                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
