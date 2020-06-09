/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.task.list;

import java.util.List;
import java.util.Random;

import org.joda.time.DateTime;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import de.symeda.sormas.api.task.TaskAssignee;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.PagedBaseListActivity;
import de.symeda.sormas.app.PagedBaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.databinding.FilterTaskListLayoutBinding;
import de.symeda.sormas.app.task.edit.TaskNewActivity;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.DataUtils;

public class TaskListActivity extends PagedBaseListActivity {

	private static TaskStatus[] statusFilters = new TaskStatus[] {
		null,
		TaskStatus.PENDING,
		TaskStatus.DONE,
		TaskStatus.NOT_EXECUTABLE };
	private TaskListViewModel model;

	public static void startActivity(Context context, TaskStatus listFilter) {
		BaseListActivity.startActivity(context, TaskListActivity.class, buildBundle(getStatusFilterPosition(statusFilters, listFilter)));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		showPreloader();
		adapter = new TaskListAdapter();
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				// Scroll to the topmost position after cases have been inserted
				if (positionStart == 0) {
					RecyclerView recyclerView = findViewById(R.id.recyclerViewForList);
					if (recyclerView != null) {
						recyclerView.scrollToPosition(0);
					}
				}
			}
		});
		model = ViewModelProviders.of(this).get(TaskListViewModel.class);
		model.initializeViewModel();
		model.getTasks().observe(this, tasks -> {
			adapter.submitList(tasks);
			hidePreloader();
		});
		setOpenPageCallback(p -> {
			showPreloader();
			model.getTaskCriteria().taskStatus(statusFilters[((PageMenuItem) p).getPosition()]);
			model.notifyCriteriaUpdated();
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		getIntent().putExtra("refreshOnResume", true);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getIntent().getBooleanExtra("refreshOnResume", false)) {
			showPreloader();
			model.getTasks().getValue().getDataSource().invalidate();
		}
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		return PageMenuItem.fromEnum(statusFilters, getContext());
	}

	@Override
	protected Callback getSynchronizeResultCallback() {
		// Reload the list after a synchronization has been done
		return () -> {
			showPreloader();
			model.getTasks().getValue().getDataSource().invalidate();
		};
	}

	@Override
	public int onNotificationCountChangingAsync(AdapterView parent, PageMenuItem menuItem, int position) {
		//TODO: Call database and retrieve notification count
		return (int) (new Random(DateTime.now().getMillis() * 1000).nextInt() / 10000000);
	}

	@Override
	protected PagedBaseListFragment buildListFragment(PageMenuItem menuItem) {
		if (menuItem != null) {
			TaskStatus listFilter = statusFilters[menuItem.getPosition()];
			return TaskListFragment.newInstance(listFilter);
		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getNewMenu().setTitle(R.string.action_new_task);
		return true;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_tasks_list;
	}

	@Override
	public void goToNewView() {
		TaskNewActivity.startActivity(getContext());
		finish();
	}

	@Override
	protected boolean isEntryCreateAllowed() {
		return ConfigProvider.hasUserRight(UserRight.TASK_CREATE);
	}

	@Override
	public void addFiltersToPageMenu() {
		View taskListFilterView = getLayoutInflater().inflate(R.layout.filter_task_list_layout, null);
		FilterTaskListLayoutBinding taskListFilterBinding = DataBindingUtil.bind(taskListFilterView);

		List<Item> taskAssigneeList = DataUtils.getEnumItems(TaskAssignee.class, false);
		taskListFilterBinding.taskAssignee.initializeSpinner(taskAssigneeList);

		pageMenu.addFilter(taskListFilterView);

		taskListFilterBinding.taskAssignee.addValueChangedListener(e -> {
			if (model.getTaskCriteria().getTaskAssignee() != e.getValue()) {
				showPreloader();
				model.getTaskCriteria().taskAssignee((TaskAssignee) e.getValue());
				model.notifyCriteriaUpdated();
			}
		});
	}
}
