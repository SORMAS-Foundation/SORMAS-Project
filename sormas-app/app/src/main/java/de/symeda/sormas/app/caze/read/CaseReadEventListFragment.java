package de.symeda.sormas.app.caze.read;

import java.util.List;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.event.list.EventListAdapter;
import de.symeda.sormas.app.event.list.EventListViewModel;
import de.symeda.sormas.app.event.read.EventReadActivity;

public class CaseReadEventListFragment extends BaseReadFragment<FragmentFormListLayoutBinding, List<Event>, Case> implements OnListItemClickListener {

	private EventListAdapter eventListAdapter;

	public static CaseReadEventListFragment newInstance(Case activityRootData) {
		return newInstance(CaseReadEventListFragment.class, null, activityRootData);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((CaseReadActivity) getActivity()).showPreloader();
		eventListAdapter = new EventListAdapter();
		EventListViewModel model = ViewModelProviders.of(this).get(EventListViewModel.class);
		model.initializeViewModel(getActivityRootData());
		model.getEvents().observe(this, events -> {
			eventListAdapter.submitList(events);
			((CaseReadActivity) getActivity()).hidePreloader();
			updateEmptyListHint(events);
		});

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		eventListAdapter.setOnListItemClickListener(this);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {

	}

	@Override
	protected void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
		contentBinding.recyclerViewForList.setAdapter(eventListAdapter);
	}

	@Override
	protected String getSubHeadingTitle() {
		Resources r = getResources();
		return r.getString(R.string.caption_case_events);
	}

	@Override
	public List<Event> getPrimaryData() {
		throw new UnsupportedOperationException("Sub list fragments don't hold their data");
	}

	@Override
	public int getRootReadLayout() {
		return R.layout.fragment_root_list_form_layout;
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_form_list_layout;
	}

	@Override
	public void onListItemClick(View v, int position, Object item) {
		Event event = (Event) item;
		EventReadActivity.startActivity(getActivity(), event.getUuid());
	}
}
