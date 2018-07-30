package de.symeda.sormas.app.contact.list;

import android.os.AsyncTask;
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
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.contact.ContactSection;
import de.symeda.sormas.app.contact.read.ContactReadActivity;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.searchstrategy.ISearchExecutor;
import de.symeda.sormas.app.searchstrategy.ISearchResultCallback;
import de.symeda.sormas.app.searchstrategy.SearchStrategyFor;
import de.symeda.sormas.app.util.SubheadingHelper;

public class ContactListFragment extends BaseListFragment<ContactListAdapter> implements OnListItemClickListener {

    private AsyncTask searchTask;
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
    public ContactListAdapter getNewListAdapter() {
        return new ContactListAdapter(R.layout.row_read_contact_list_item_layout, this, this.contacts);
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Contact contact = (Contact) item;
        ContactReadActivity.startActivity(getContext(), contact.getUuid());
    }

    @Override
    protected int getEmptyListEntityResId() {
        return R.string.entity_contact;
    }

    @Override
    public void onResume() {
        super.onResume();

        getSubHeadingHandler().updateSubHeadingTitle(SubheadingHelper.getSubHeading(getResources(), SearchBy.BY_FILTER_STATUS, getListFilter(), "Contact"));

        ISearchExecutor<Contact> executor = SearchStrategyFor.CONTACT.selector(SearchBy.BY_FILTER_STATUS, getListFilter(), null);
        searchTask = executor.search(new ISearchResultCallback<Contact>() {
            @Override
            public void preExecute() {
                getBaseActivity().showPreloader();

            }

            @Override
            public void searchResult(List<Contact> result, BoolResult resultStatus) {
                getBaseActivity().hidePreloader();

                if (!resultStatus.isSuccess()) {
                    String message = String.format(getResources().getString(R.string.notification_records_not_retrieved), "Contacts");
                    NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.ERROR, message);
                    return;
                }

                contacts = result;

                if (ContactListFragment.this.isResumed()) {
                    ContactListFragment.this.getListAdapter().replaceAll(contacts);
                    ContactListFragment.this.getListAdapter().notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerViewForList.setLayoutManager(linearLayoutManager);
        recyclerViewForList.setAdapter(getListAdapter());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (searchTask != null && !searchTask.isCancelled())
            searchTask.cancel(true);
    }
}