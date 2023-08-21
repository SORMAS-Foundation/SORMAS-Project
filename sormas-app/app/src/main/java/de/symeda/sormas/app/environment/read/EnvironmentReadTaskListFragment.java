package de.symeda.sormas.app.environment.read;

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

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.environment.Environment;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.task.edit.TaskEditActivity;
import de.symeda.sormas.app.task.list.TaskListAdapter;
import de.symeda.sormas.app.task.list.TaskListViewModel;

public class EnvironmentReadTaskListFragment extends BaseReadFragment<FragmentFormListLayoutBinding, List<Task>, Environment>
	implements OnListItemClickListener {

	private List<Task> record;

	private TaskListAdapter adapter;
	private TaskListViewModel model;
	private LinearLayoutManager linearLayoutManager;

	public static EnvironmentReadTaskListFragment newInstance(Environment activityRootData) {
		return newInstance(EnvironmentReadTaskListFragment.class, null, activityRootData);
	}

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {

	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((EnvironmentReadActivity) getActivity()).showPreloader();
		adapter = new TaskListAdapter();
		model = new ViewModelProvider(this).get(TaskListViewModel.class);
		model.initializeViewModel(getActivityRootData());
		model.getTasks().observe(this, tasks -> {
			((EnvironmentReadActivity) getActivity()).hidePreloader();
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
	protected void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
		linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
		contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
		contentBinding.recyclerViewForList.setAdapter(adapter);
	}

	@Override
	protected String getSubHeadingTitle() {
		Resources r = getResources();
		return r.getString(R.string.caption_environment_tasks);
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_form_list_layout;
	}

	@Override
	public int getRootReadLayout() {
		return R.layout.fragment_root_list_form_layout;
	}

	@Override
	public List<Task> getPrimaryData() {
		return record;
	}

	@Override
	public void onListItemClick(View view, int position, Object item) {
		Task task = (Task) item;
		TaskEditActivity.startActivity(getActivity(), task.getUuid());
	}
}
