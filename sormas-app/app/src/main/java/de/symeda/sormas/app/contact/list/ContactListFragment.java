package de.symeda.sormas.app.contact.list;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.app.BaseListActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.contact.read.ContactReadActivity;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.IListNavigationCapsule;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.searchstrategy.ISearchExecutor;
import de.symeda.sormas.app.searchstrategy.ISearchResultCallback;
import de.symeda.sormas.app.searchstrategy.SearchStrategyFor;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;
import de.symeda.sormas.app.util.SubheadingHelper;

/**
 * Created by Orson on 07/12/2017.
 */

public class ContactListFragment extends BaseListActivityFragment<ContactListAdapter> implements OnListItemClickListener {

    private boolean dataLoaded = false;
    private AsyncTask searchTask;
    private List<Contact> contacts;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerViewForList;
    private String recordUuid = null;
    private FollowUpStatus filterStatus = null;
    private SearchBy searchBy = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveFilterStatusState(outState, filterStatus);
        SaveSearchStrategyState(outState, searchBy);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        filterStatus = (FollowUpStatus) getFilterStatusArg(arguments);
        searchBy = (SearchBy) getSearchStrategyArg(arguments);
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
    public void cancelTaskExec() {
        if (searchTask != null && !searchTask.isCancelled())
            searchTask.cancel(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        //TODO: Orson - reverse this relationship
        getSubHeadingHandler().updateSubHeadingTitle(SubheadingHelper.getSubHeading(getResources(), searchBy, filterStatus, "Contact"));

        try {
            dataLoaded = false;
            if (!dataLoaded) {
                ISearchExecutor<Contact> executor = SearchStrategyFor.CONTACT.selector(searchBy, filterStatus, recordUuid);
                searchTask = executor.search(new ISearchResultCallback<Contact>() {
                    @Override
                    public void preExecute() {
                        getActivityCommunicator().showPreloader();
                        getActivityCommunicator().hideFragmentView();
                    }

                    @Override
                    public void searchResult(List<Contact> result, BoolResult resultStatus) {
                        getActivityCommunicator().hidePreloader();

                        if (!resultStatus.isSuccess()) {
                            String message = String.format(getResources().getString(R.string.notification_records_not_retrieved), "Contacts");
                            NotificationHelper.showNotification((INotificationContext) getActivity(), NotificationType.ERROR, message);

                            return;
                        }

                        contacts = result;

                        ContactListFragment.this.getListAdapter().replaceAll(contacts);
                        ContactListFragment.this.getListAdapter().notifyDataSetChanged();

                        dataLoaded = true;

                        getActivityCommunicator().hidePreloader();
                        getActivityCommunicator().showFragmentView();
                    }

                    private ISearchResultCallback<Contact> init() {
                        getActivityCommunicator().showPreloader();

                        return this;
                    }
                }.init());
            }
        } catch (Exception ex) {
            getActivityCommunicator().hidePreloader();
            dataLoaded = false;
        }

        final SwipeRefreshLayout swiperefresh = (SwipeRefreshLayout)this.getView().findViewById(R.id.swiperefresh);
        if (swiperefresh != null) {
            swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getActivityCommunicator().synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly, false, true, true, swiperefresh, null);
                }
            });
        }

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

    public static ContactListFragment newInstance(IActivityCommunicator communicator, IListNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(communicator, ContactListFragment.class, capsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (searchTask != null && !searchTask.isCancelled())
            searchTask.cancel(true);
    }
}