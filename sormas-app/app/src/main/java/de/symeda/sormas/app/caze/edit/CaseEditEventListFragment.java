package de.symeda.sormas.app.caze.edit;

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

import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.event.edit.EventEditActivity;
import de.symeda.sormas.app.event.list.EventListAdapter;
import de.symeda.sormas.app.event.list.EventListViewModel;

public class CaseEditEventListFragment extends BaseEditFragment<FragmentFormListLayoutBinding, List<Event>, Case> implements OnListItemClickListener {

	private EventListAdapter adapter;

	public static CaseEditEventListFragment newInstance(Case activityRootData) {
		return newInstance(CaseEditEventListFragment.class, null, activityRootData);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((BaseActivity) getActivity()).showPreloader();
		adapter = new EventListAdapter();
		EventListViewModel model = ViewModelProviders.of(this).get(EventListViewModel.class);
		model.initializeViewModel(getActivityRootData());
		model.getEvents().observe(this, events -> {
			((CaseEditActivity) getActivity()).hidePreloader();
			adapter.submitList(events);
			updateEmptyListHint(events);
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		adapter.setOnListItemClickListener(this);
		return super.onCreateView(inflater, container, savedInstanceState);
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
	protected void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
		contentBinding.recyclerViewForList.setAdapter(adapter);
	}

	@Override
	public int getRootEditLayout() {
		return R.layout.fragment_root_list_form_layout;
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_form_list_layout;
	}

	@Override
	protected void prepareFragmentData() {

	}

	@Override
	public void onListItemClick(View view, int position, Object item) {
		Event event = (Event) item;
		EventEditActivity.startActivity(getActivity(), event.getUuid());
	}

	@Override
	public boolean isShowSaveAction() {
		return false;
	}

	@Override
	public boolean isShowNewAction() {
		return true;
	}
}
