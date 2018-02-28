package de.symeda.sormas.app.event.edit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewStub;

import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentEditListLayoutBinding;
import de.symeda.sormas.app.event.EventFormNavigationCapsule;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import java.util.List;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.event.EventParticipant;

/**
 * Created by Orson on 12/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class EventEditPersonsInvolvedListFragment extends BaseEditActivityFragment<FragmentEditListLayoutBinding> implements OnListItemClickListener {

    private String recordUuid = null;
    //private EventStatus filterStatus = null;
    private EventStatus pageStatus = null;
    private List<EventParticipant> record;
    private FragmentEditListLayoutBinding binding;
    private EventEditPersonsInvolvedListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //SaveFilterStatusState(outState, filterStatus);
        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        //filterStatus = (EventStatus) getFilterStatusArg(arguments);
        pageStatus = (EventStatus) getPageStatusArg(arguments);
    }

    @Override
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public AbstractDomainObject getData() {
        return null;
    }

    @Override
    public void onBeforeLayoutBinding(Bundle savedInstanceState) {

        //Get Data
        record = MemoryDatabaseHelper.EVENT_PARTICIPANT.getEventParticipants(20);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    public void onLayoutBinding(ViewStub stub, View inflated, FragmentEditListLayoutBinding contentBinding) {
        //binding = DataBindingUtil.inflate(inflater, getEditLayout(), container, true);

        //Create adapter and set data
        adapter = new EventEditPersonsInvolvedListAdapter(this.getActivity(), R.layout.row_read_persons_involved_list_item_layout, this, record);

        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        contentBinding.recyclerViewForList.setAdapter(adapter);


        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAfterLayoutBinding(FragmentEditListLayoutBinding binding) {

    }

    @Override
    public int getRootEditLayout() {
        return R.layout.fragment_root_list_edit_layout;
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_edit_list_layout;
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();

        //adapter.replaceAll(new ArrayList<EventParticipant>(record));
        adapter.notifyDataSetChanged();

        final SwipeRefreshLayout swiperefresh = (SwipeRefreshLayout)getRootBinding().getRoot()
                .findViewById(R.id.swiperefresh);

        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBaseEditActivity().synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly,
                        true, false, swiperefresh, null);
            }
        });
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        EventParticipant o = (EventParticipant)item;

        EventFormNavigationCapsule dataCapsule = new EventFormNavigationCapsule(getContext(), o.getUuid(), pageStatus);
        EventEditPersonsInvolvedInfoActivity.goToActivity(getActivity(), dataCapsule);
    }

    public static EventEditPersonsInvolvedListFragment newInstance(EventFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(EventEditPersonsInvolvedListFragment.class, capsule);
    }
}

