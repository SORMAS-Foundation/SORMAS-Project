package de.symeda.sormas.app.contact.read;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.contact.ContactFormFollowUpNavigationCapsule;
import de.symeda.sormas.app.contact.ContactFormNavigationCapsule;
import de.symeda.sormas.app.contact.read.sub.ContactReadFollowUpVisitInfoActivity;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentContactReadFollowupLayoutBinding;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;

/**
 * Created by Orson on 01/01/2018.
 */

public class ContactReadFollowUpVisitListFragment extends BaseReadActivityFragment<FragmentContactReadFollowupLayoutBinding, List<Visit>> implements OnListItemClickListener {

    private String recordUuid;
    private ContactClassification contactClassification = null;
    private List<Visit> record;

    private ContactReadFollowupListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SavePageStatusState(outState, contactClassification);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        contactClassification = (ContactClassification) getPageStatusArg(arguments);
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            List<Visit> visits = new ArrayList<Visit>();

            if (recordUuid != null && !recordUuid.isEmpty()) {
                Contact contact = DatabaseHelper.getContactDao().queryUuid(recordUuid);
                if (contact != null)
                    visits = DatabaseHelper.getVisitDao().getByContact(contact);
            }

            resultHolder.forList().add(visits);
        } else {
            ITaskResultHolderIterator listIterator = resultHolder.forList().iterator();

            if (listIterator.hasNext())
                record = listIterator.next();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentContactReadFollowupLayoutBinding contentBinding) {
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new ContactReadFollowupListAdapter(ContactReadFollowUpVisitListFragment.this.getActivity(),
                R.layout.row_read_followup_list_item_layout, ContactReadFollowUpVisitListFragment.this, record);

        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        contentBinding.recyclerViewForList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAfterLayoutBinding(FragmentContactReadFollowupLayoutBinding contentBinding) {

    }

    @Override
    public void onResume() {
        super.onResume();

        final SwipeRefreshLayout swiperefresh = (SwipeRefreshLayout)getRootBinding().getRoot()
                .findViewById(R.id.swiperefresh);

        if (swiperefresh != null) {
            swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getBaseReadActivity().synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly,
                            true, false, swiperefresh, null);
                }
            });
        }
    }

    @Override
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public List<Visit> getPrimaryData() {
        return record;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //recyclerViewForList.setLayoutManager(linearLayoutManager);
        //recyclerViewForList.setAdapter(adapter);
        //binding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        //binding.recyclerViewForList.setAdapter(adapter);
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_contact_read_followup_layout;
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Visit record = (Visit)item;
        ContactFormFollowUpNavigationCapsule dataCapsule = new ContactFormFollowUpNavigationCapsule(getContext(), record.getUuid(), record.getVisitStatus());
        ContactReadFollowUpVisitInfoActivity.goToActivity(getActivity(), dataCapsule);
        /*if(record != null) {
            Intent intent = new Intent(getActivity(), ContactReadFollowUpVisitInfoActivity.class);
            intent.putExtra(ConstantHelper.KEY_DATA_UUID, record.getUuid());
            intent.putExtra(IStatusElaborator.ARG_VISIT_STATUS, record.getVisitStatus());
            //intent.putExtra(IStatusElaborator.ARG_FOLLOW_UP_STATUS, followUpStatus);
            //TODO: Receive Contact Information Here from Parent
            startActivity(intent);
        }*/
    }

    public static ContactReadFollowUpVisitListFragment newInstance(IActivityCommunicator activityCommunicator, ContactFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, ContactReadFollowUpVisitListFragment.class, capsule);
    }

}