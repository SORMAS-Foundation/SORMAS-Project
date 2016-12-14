package de.symeda.sormas.app.contact;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.person.SyncPersonsTask;
import de.symeda.sormas.app.util.Callback;

public class ContactsListFragment extends ListFragment {

    private String caseUuid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cases_list_layout, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateContacsArrayAdapter();
    }

    public void updateContacsArrayAdapter() {
        caseUuid = getArguments().getString(Case.UUID);
        final CaseDao caseDao = DatabaseHelper.getCaseDao();
        final Case caze = caseDao.queryUuid(caseUuid);

        new SyncPersonsTask().execute();

        SyncContactsTask.syncContacts(new Callback() {
            @Override
            public void call() {
                List<Contact> contacts = null;
                try {
                    contacts = DatabaseHelper.getContactDao().getByCase(caze);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                ArrayAdapter<Contact> listAdapter = (ArrayAdapter<Contact>)getListAdapter();
                listAdapter.clear();
                listAdapter.addAll(contacts);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ContactsListArrayAdapter adapter = new ContactsListArrayAdapter(
                this.getActivity(),              // Context for the activity.
                R.layout.contacts_list_item);    // Layout to use (create)

        setListAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent,
                    View viewClicked,
                    int position, long id) {
                Contact contact = (Contact)getListAdapter().getItem(position);
                showContactEditView(contact);
            }
        });
    }

    public void showContactEditView(Contact contact) {
        Intent intent = new Intent(getActivity(), ContactEditActivity.class);
        intent.putExtra(ContactEditActivity.KEY_CONTACT_UUID, contact.getUuid());
        intent.putExtra(ContactEditActivity.KEY_CASE_UUID, caseUuid);
        startActivity(intent);
    }
}
