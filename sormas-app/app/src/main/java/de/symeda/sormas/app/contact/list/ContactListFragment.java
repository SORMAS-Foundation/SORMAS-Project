package de.symeda.sormas.app.contact.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.BaseListActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.contact.ContactFormNavigationCapsule;
import de.symeda.sormas.app.contact.read.ContactReadActivity;
import de.symeda.sormas.app.core.SearchStrategy;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.app.backend.contact.Contact;

/**
 * Created by Orson on 07/12/2017.
 */

public class ContactListFragment extends BaseListActivityFragment<ContactListAdapter> implements OnListItemClickListener {

    private List<Contact> contacts;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerViewForList;
    private String recordUuid = null;
    private FollowUpStatus filterStatus = null;
    private SearchStrategy searchStrategy = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveFilterStatusState(outState, filterStatus);
        SaveSearchStrategyState(outState, searchStrategy);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        filterStatus = (FollowUpStatus) getFilterStatusArg(arguments);
        searchStrategy = (SearchStrategy) getSearchStrategyArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewForList = (RecyclerView) view.findViewById(R.id.recyclerViewForList);

        return view;
    }

    @Override
    public ContactListAdapter getNewListAdapter() {
        return new ContactListAdapter(this.getActivity(), R.layout.row_read_contact_list_item_layout, this, this.contacts);
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Contact c = (Contact)item;
        ContactFormNavigationCapsule dataCapsule = new ContactFormNavigationCapsule(getContext(),
                c.getUuid(), c.getContactClassification());
        ContactReadActivity.goToActivity(getActivity(), dataCapsule);
    }

    @Override
    public void onResume() {
        super.onResume();

        IContactsSearchStrategy strategy;
        contacts = new ArrayList<>();

        //TODO: Orson - Remove - For Dev Only

        /*Guard.That.NotNull.isTrue(arguments);
        Guard.That.Bundle.contains(arguments, ConstantHelper.ARG_FILTER_STATUS);
        Guard.That.Bundle.contains(arguments, Case.UUID);*/

        //TODO: Orson - Remove
        filterStatus = filterStatus == null ? FollowUpStatus.FOLLOW_UP : filterStatus;

        if (searchStrategy == SearchStrategy.BY_FILTER_STATUS) {
            strategy = new ContactsSearchByFollowUpStatusStrategy(filterStatus);
            getCommunicator().updateSubHeadingTitle(filterStatus != null ? filterStatus.toString() : "");
        } else if (searchStrategy == SearchStrategy.BY_CASE_ID) {
            if (recordUuid != null && recordUuid != "") {
                strategy = new ContactsSearchByCaseStrategy(recordUuid);
            } else {
                throw new IllegalArgumentException("Case record uuid not found");
            }
        } else {
            strategy = new ContactsSearchByFollowUpStatusStrategy(filterStatus);
        }

        contacts = strategy.search();

        this.getListAdapter().replaceAll(contacts);
        this.getListAdapter().notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //recyclerViewForList.setHasFixedSize(true);
        recyclerViewForList.setLayoutManager(linearLayoutManager);
        recyclerViewForList.setAdapter(getListAdapter());
    }

    public void showContactReadView(Contact contact) {
        /*Intent intent = new Intent(getActivity(), TaskEditActivity.class);
        intent.putExtra(Task.UUID, task.getUuid());
        if(parentCaseUuid != null) {
            intent.putExtra(KEY_CASE_UUID, parentCaseUuid);
        }
        if(parentContactUuid != null) {
            intent.putExtra(KEY_CONTACT_UUID, parentContactUuid);
        }
        if(parentEventUuid != null) {
            intent.putExtra(KEY_EVENT_UUID, parentEventUuid);
        }
        startActivity(intent);*/
    }

    public static ContactListFragment newInstance(ContactListCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(ContactListFragment.class, capsule);
    }
}