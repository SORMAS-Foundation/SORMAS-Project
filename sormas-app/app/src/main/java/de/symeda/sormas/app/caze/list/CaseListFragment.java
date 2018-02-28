package de.symeda.sormas.app.caze.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.BaseListActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.caze.CaseFormNavigationCapsule;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.core.SearchStrategy;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import java.util.List;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.backend.caze.Case;

/**
 * Created by Orson on 05/12/2017.
 */

public class CaseListFragment extends BaseListActivityFragment<CaseListAdapter> implements OnListItemClickListener {

    private List<Case> cases;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerViewForList;
    private InvestigationStatus filterStatus = null;
    private SearchStrategy searchStrategy = null;
    String recordUuid = null;

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

        filterStatus = (InvestigationStatus) getFilterStatusArg(arguments);
        searchStrategy = (SearchStrategy) getSearchStrategyArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewForList = (RecyclerView)view.findViewById(R.id.recyclerViewForList);

        return view;
    }

    @Override
    public CaseListAdapter getNewListAdapter() {
        return new CaseListAdapter(this.getActivity(), R.layout.row_case_list_item_layout, this, this.cases);
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Case c = (Case)item;
        CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(getContext(),
                c.getUuid()).setReadPageStatus(c.getCaseClassification());
        CaseReadActivity.goToActivity(getActivity(), dataCapsule);
    }

    @Override
    public void onResume() {
        super.onResume();

        //TODO: Orson - Replace with real data
        if (searchStrategy == SearchStrategy.BY_FILTER_STATUS) {

            //TODO: Orson - reverse this relationship
            getCommunicator().updateSubHeadingTitle(filterStatus.toString());

            if (filterStatus == InvestigationStatus.PENDING) {
                cases = MemoryDatabaseHelper.CASE.getPendingCases(20);
            } else if (filterStatus == InvestigationStatus.DONE) {
                cases = MemoryDatabaseHelper.CASE.getDoneCases(20);
            } else if (filterStatus == InvestigationStatus.DISCARDED) {
                cases = MemoryDatabaseHelper.CASE.getDiscardedCases(20);
            } else {
                cases = MemoryDatabaseHelper.CASE.getCases(20);
                getCommunicator().updateSubHeadingTitle("All");
            }
        } else {
            cases = MemoryDatabaseHelper.CASE.getCases(20);
            getCommunicator().updateSubHeadingTitle("All");
        }


        this.getListAdapter().replaceAll(cases);
        this.getListAdapter().notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //recyclerViewForList.setHasFixedSize(true);
        recyclerViewForList.setLayoutManager(linearLayoutManager);
        recyclerViewForList.setAdapter(getListAdapter());
    }

    public void showCaseReadView(Case caze) {
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

    public static CaseListFragment newInstance(CaseListCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(CaseListFragment.class, capsule);
    }
}
