package de.symeda.sormas.app.contact;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.List;

import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.app.AbstractTabActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;

public class ContactsListFragment extends ListFragment {

    public static final String ARG_FILTER_STATUS = "filterStatus";

    private String caseUuid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_layout, container, false);
        SormasApplication application = (SormasApplication) getActivity().getApplication();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getArguments().containsKey(Case.UUID)) {
            updateCaseContactsArrayAdapter();
        } else {
            updateContactsArrayAdapter();
        }

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout)getView().findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((AbstractTabActivity)getActivity()).synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly, false, true, true, refreshLayout, null);
            }
        });

        ((ContactsListArrayAdapter)getListAdapter()).updateUnreadIndicator();
    }

    public void updateContactsArrayAdapter() {
        List<Contact> contacts;
        Bundle arguments = getArguments();
        if(arguments.containsKey(ARG_FILTER_STATUS)) {
            FollowUpStatus filterStatus = (FollowUpStatus) arguments.getSerializable(ARG_FILTER_STATUS);
            contacts = DatabaseHelper.getContactDao().queryForEq(Contact.FOLLOW_UP_STATUS, filterStatus, Contact.REPORT_DATE_TIME, false);
        } else {
            contacts = DatabaseHelper.getContactDao().queryForAll(Contact.REPORT_DATE_TIME, false);
        }

        ArrayAdapter<Contact> listAdapter = (ArrayAdapter<Contact>)getListAdapter();
        listAdapter.clear();
        listAdapter.addAll(contacts);
    }

    public void updateCaseContactsArrayAdapter() {
        caseUuid = getArguments().getString(Case.UUID);
        final CaseDao caseDao = DatabaseHelper.getCaseDao();
        final Case caze = caseDao.queryUuidReference(caseUuid);

        List<Contact> contacts = DatabaseHelper.getContactDao().getByCase(caze);
        ArrayAdapter<Contact> listAdapter = (ArrayAdapter<Contact>)getListAdapter();
        listAdapter.clear();
        listAdapter.addAll(contacts);

        if (listAdapter.getCount() == 0) {
            getView().findViewById(R.id.empty_list_hint).setVisibility(View.VISIBLE);
            getView().findViewById(android.R.id.list).setVisibility(View.GONE);
        } else {
            getView().findViewById(R.id.empty_list_hint).setVisibility(View.GONE);
            getView().findViewById(android.R.id.list).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ContactsListArrayAdapter adapter = new ContactsListArrayAdapter(
                this.getActivity(),              // Context for the activity.
                R.layout.contacts_list_item);    // Layout to use (build)

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
