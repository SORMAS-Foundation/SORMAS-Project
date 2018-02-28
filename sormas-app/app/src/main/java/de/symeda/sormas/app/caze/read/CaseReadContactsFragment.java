package de.symeda.sormas.app.caze.read;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.caze.CaseFormNavigationCapsule;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentCaseReadContactLayoutBinding;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import java.util.List;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.contact.Contact;

/**
 * Created by Orson on 11/01/2018.
 */

public class CaseReadContactsFragment extends BaseReadActivityFragment<FragmentCaseReadContactLayoutBinding> implements OnListItemClickListener {

    private String caseUuid = null;
    private InvestigationStatus filterStatus = null;
    private CaseClassification pageStatus = null;
    private List<Contact> record;
    private FragmentCaseReadContactLayoutBinding binding;

    private CaseReadReadContactListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveFilterStatusState(outState, filterStatus);
        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, caseUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        caseUuid = getRecordUuidArg(arguments);
        filterStatus = (InvestigationStatus) getFilterStatusArg(arguments);
        pageStatus = (CaseClassification) getPageStatusArg(arguments);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        //Get binding
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding = DataBindingUtil.inflate(inflater, getRootReadLayout(), container, false);

        //Get Data
        record = MemoryDatabaseHelper.CONTACT.getContacts(20);

        //Create adapter and set data
        adapter = new CaseReadReadContactListAdapter(this.getActivity(), R.layout.row_read_contact_list_item_layout, this, record);

        binding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        binding.recyclerViewForList.setAdapter(adapter);


        adapter.notifyDataSetChanged();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        //adapter.replaceAll(new ArrayList<EventParticipant>(record));
        adapter.notifyDataSetChanged();

        binding.swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBaseReadActivity().synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly, true, false, binding.swiperefresh, null);
            }
        });
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
    public FragmentCaseReadContactLayoutBinding getBinding() {
        return binding;
    }

    @Override
    public Object getRecord() {
        return record;
    }

    public void showRecordInfoReadView(Contact contact) {
    }

    @Override
    public int getRootReadLayout() {
        return R.layout.fragment_case_read_contact_layout;
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
    }

    public static CaseReadContactsFragment newInstance(CaseFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(CaseReadContactsFragment.class, capsule);
    }

}