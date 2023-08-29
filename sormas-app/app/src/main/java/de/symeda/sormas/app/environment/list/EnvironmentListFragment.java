package de.symeda.sormas.app.environment.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.app.PagedBaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.environment.Environment;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.environment.read.EnvironmentReadActivity;

public class EnvironmentListFragment extends PagedBaseListFragment<EnvironmentListAdapter> implements OnListItemClickListener {

	private LinearLayoutManager linearLayoutManager;

	private RecyclerView recyclerViewForList;

	public static EnvironmentListFragment newInstance() {
		return newInstance(EnvironmentListFragment.class, null, null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		recyclerViewForList = view.findViewById(R.id.recyclerViewForList);
		return view;
	}

	@Override
	public EnvironmentListAdapter getNewListAdapter() {
		return (EnvironmentListAdapter) ((EnvironmentListActivity) getActivity()).getAdapter();
	}

	@Override
	public void onListItemClick(View view, int position, Object item) {
		Environment environment = (Environment) item;
		EnvironmentReadActivity.startActivity(getContext(), environment.getUuid(), false);
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
