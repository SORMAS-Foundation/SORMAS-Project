package de.symeda.sormas.app.campaign.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.app.PagedBaseListFragment;
import de.symeda.sormas.app.R;

public class CampaignFormDataListFragment extends PagedBaseListFragment<CampaignFormDataListAdapter> {

    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerViewForList;

    public static CampaignFormDataListFragment newInstance() {
        return newInstance(CampaignFormDataListFragment.class, null, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerViewForList = view.findViewById(R.id.recyclerViewForList);

        return view;
    }

    @Override
    public CampaignFormDataListAdapter getNewListAdapter() {
        return (CampaignFormDataListAdapter) ((CampaignFormDataListActivity) getActivity()).getAdapter();
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {

    }

    @Override
    public void onResume() {
        super.onResume();
        getSubHeadingHandler().updateSubHeadingTitle();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerViewForList.setAdapter(getListAdapter());
        recyclerViewForList.setLayoutManager(linearLayoutManager);
    }
}
