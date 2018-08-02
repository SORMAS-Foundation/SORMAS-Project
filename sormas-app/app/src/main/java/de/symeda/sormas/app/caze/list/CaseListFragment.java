package de.symeda.sormas.app.caze.list;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.BaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;

public class CaseListFragment extends BaseListFragment<CaseListAdapter> implements OnListItemClickListener {

    private List<Case> cases;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerViewForList;

    public static CaseListFragment newInstance(InvestigationStatus listFilter) {
        return newInstance(CaseListFragment.class, null, listFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewForList = (RecyclerView) view.findViewById(R.id.recyclerViewForList);

        return view;
    }

    @Override
    public CaseListAdapter getNewListAdapter() {
        return new CaseListAdapter(this.getActivity(), R.layout.row_case_list_item_layout, this, this.cases);
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Case caze = (Case) item;
        CaseReadActivity.startActivity(getContext(), caze.getUuid(), false);
    }

    @Override
    protected int getEmptyListEntityResId() {
        return R.string.entity_case;
    }

    @Override
    protected boolean canAddToList() {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        getSubHeadingHandler().updateSubHeadingTitle();

//        getSubHeadingHandler().updateSubHeadingTitle(SubheadingHelper.getSubHeading(getResources(), SearchBy.BY_FILTER_STATUS, getListFilter(), "Case"));
    }

    @Override
    protected void prepareFragmentData() {
        cases = DatabaseHelper.getCaseDao().queryForEq(Case.INVESTIGATION_STATUS, getListFilter(), Case.REPORT_DATE, false);
        getListAdapter().replaceAll(cases);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerViewForList.setLayoutManager(linearLayoutManager);
        recyclerViewForList.setAdapter(getListAdapter());
    }
}
