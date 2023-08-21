package de.symeda.sormas.app.environment.edit;

import java.util.List;

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
import de.symeda.sormas.app.backend.environment.Environment;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.task.edit.TaskEditActivity;
import de.symeda.sormas.app.task.list.TaskListAdapter;
import de.symeda.sormas.app.task.list.TaskListViewModel;

public class EnvironmentEditTaskListFragment extends BaseEditFragment<FragmentFormListLayoutBinding, List<Task>, Environment>
	implements OnListItemClickListener {

	private TaskListAdapter adapter;
	private TaskListViewModel model;
	private LinearLayoutManager linearLayoutManager;

	public static EnvironmentEditTaskListFragment newInstance(Environment activityRootData) {
		return newInstance(EnvironmentEditTaskListFragment.class, null, activityRootData);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((EnvironmentEditActivity) getActivity()).showPreloader();
		adapter = new TaskListAdapter();
		model = new ViewModelProvider(this).get(TaskListViewModel.class);
		model.initializeViewModel(getActivityRootData());
		model.getTasks().observe(this, tasks -> {
			((EnvironmentEditActivity) getActivity()).hidePreloader();
			adapter.submitList(tasks);
			updateEmptyListHint(tasks);
		});
	}

	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
		adapter.setOnListItemClickListener(this);

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_environment_tasks);
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
	public List<Task> getPrimaryData() {
		throw new UnsupportedOperationException("Sub list fragments don't hold their data");
	}

	@Override
	public boolean isShowSaveAction() {
		return false;
	}

	@Override
	public boolean isShowNewAction() {
		return ConfigProvider.hasUserRight(UserRight.TASK_CREATE);
	}

	@Override
	protected void prepareFragmentData() {

	}

	@Override
	protected void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
		linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
		contentBinding.recyclerViewForList.setAdapter(adapter);
	}

	@Override
	public void onListItemClick(View view, int position, Object item) {
		Task task = (Task) item;
		TaskEditActivity.startActivity(getActivity(), task.getUuid());
	}
}
