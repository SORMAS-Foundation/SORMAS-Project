package de.symeda.sormas.app.environmentsample.edit;

import java.util.List;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.environment.environmentsample.EnvironmentSample;
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.pathogentest.list.PathogenTestListAdapter;
import de.symeda.sormas.app.pathogentest.list.PathogenTestListViewModel;
import de.symeda.sormas.app.pathogentest.read.PathogenTestReadActivity;

public class EnvironmentSampleEditPathogenTestListFragment
	extends BaseEditFragment<FragmentFormListLayoutBinding, List<PathogenTest>, EnvironmentSample>
	implements OnListItemClickListener {

	public static final String TAG = EnvironmentSampleEditPathogenTestListFragment.class.getSimpleName();

	private PathogenTestListAdapter adapter;

	public static EnvironmentSampleEditPathogenTestListFragment newInstance(EnvironmentSample activityRootData) {
		return newInstance(EnvironmentSampleEditPathogenTestListFragment.class, null, activityRootData);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((EnvironmentSampleEditActivity) getActivity()).showPreloader();
		adapter = new PathogenTestListAdapter();
		PathogenTestListViewModel model = new ViewModelProvider(this).get(PathogenTestListViewModel.class);
		model.initializeViewModel(getActivityRootData());
		model.getPathogenTests().observe(this, contacts -> {
			((EnvironmentSampleEditActivity) getActivity()).hidePreloader();
			adapter.submitList(contacts);
			updateEmptyListHint(contacts);
		});
	}

	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
		adapter.setOnListItemClickListener(this);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected String getSubHeadingTitle() {
		Resources r = getResources();
		return r.getString(R.string.heading_pathogen_tests_list);
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
	public List<PathogenTest> getPrimaryData() {
		throw new UnsupportedOperationException("Sub list fragments don't hold their data");
	}

	@Override
	protected void prepareFragmentData() {

	}

	@Override
	protected void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
		contentBinding.recyclerViewForList.setAdapter(adapter);
	}

	@Override
	public void onListItemClick(View view, int position, Object item) {
		PathogenTest contact = (PathogenTest) item;
		PathogenTestReadActivity.startActivity(getActivity(), contact.getUuid());
	}

	@Override
	public boolean isShowSaveAction() {
		return false;
	}

	@Override
	public boolean isShowNewAction() {
		return ConfigProvider.hasUserRight(UserRight.ENVIRONMENT_PATHOGEN_TEST_CREATE);
	}
}
