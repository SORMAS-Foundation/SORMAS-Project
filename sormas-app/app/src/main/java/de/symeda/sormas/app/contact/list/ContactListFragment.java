package de.symeda.sormas.app.contact.list;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.app.BaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.contact.read.ContactReadActivity;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;

public class ContactListFragment extends BaseListFragment<ContactListAdapter> implements OnListItemClickListener {

    private List<Contact> contacts;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerViewForList;

    public static ContactListFragment newInstance(FollowUpStatus listFilter) {
        return BaseListFragment.newInstance(ContactListFragment.class, null, listFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewForList = (RecyclerView) view.findViewById(R.id.recyclerViewForList);
        return view;
    }

    @Override
    protected void prepareFragmentData() {
        contacts = DatabaseHelper.getContactDao().queryForEq(Contact.FOLLOW_UP_STATUS, getListFilter(), Contact.REPORT_DATE_TIME, false);
        getListAdapter().replaceAll(contacts);
    }

    @Override
    public ContactListAdapter getNewListAdapter() {
        return new ContactListAdapter(R.layout.row_read_contact_list_item_layout, this, this.contacts, (FollowUpStatus) getListFilter());
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Contact contact = (Contact) item;
        ContactReadActivity.startActivity(getContext(), contact.getUuid(), false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerViewForList.setLayoutManager(linearLayoutManager);
        recyclerViewForList.setAdapter(getListAdapter());
    }
}