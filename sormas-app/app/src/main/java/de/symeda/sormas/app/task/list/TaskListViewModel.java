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

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.paging.PositionalDataSource;

import de.symeda.sormas.api.task.TaskAssignee;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskCriteria;

public class TaskListViewModel extends ViewModel {

	private LiveData<PagedList<Task>> tasks;
	private TaskDataFactory taskDataFactory;

	public void initializeViewModel(Case caze) {
		taskDataFactory = new TaskDataFactory();
		TaskCriteria taskCriteria = new TaskCriteria();
		taskCriteria.associatedCase(caze);
		taskDataFactory.setTaskCriteria(taskCriteria);
		initializeList();
	}

	public void initializeViewModel(Contact contact) {
		taskDataFactory = new TaskDataFactory();
		TaskCriteria taskCriteria = new TaskCriteria();
		taskCriteria.associatedContact(contact);
		taskDataFactory.setTaskCriteria(taskCriteria);
		initializeList();
	}

	public void initializeViewModel(Event event) {
		taskDataFactory = new TaskDataFactory();
		TaskCriteria taskCriteria = new TaskCriteria();
		taskCriteria.associatedEvent(event);
		taskDataFactory.setTaskCriteria(taskCriteria);
		initializeList();
	}

	public void initializeViewModel() {
		taskDataFactory = new TaskDataFactory();
		TaskCriteria taskCriteria = new TaskCriteria();
		taskCriteria.taskStatus(TaskStatus.PENDING);
		taskCriteria.taskAssignee(TaskAssignee.CURRENT_USER);
		taskDataFactory.setTaskCriteria(taskCriteria);
		initializeList();
	}

	public LiveData<PagedList<Task>> getTasks() {
		return tasks;
	}

	void notifyCriteriaUpdated() {
		if (tasks.getValue() != null) {
			tasks.getValue().getDataSource().invalidate();
			if (!tasks.getValue().isEmpty()) {
				tasks.getValue().loadAround(0);
			}
		}
	}

	public TaskCriteria getTaskCriteria() {
		return taskDataFactory.getTaskCriteria();
	}

	public static class TaskDataSource extends PositionalDataSource<Task> {

		private TaskCriteria taskCriteria;

		TaskDataSource(TaskCriteria taskCriteria) {
			this.taskCriteria = taskCriteria;
		}

		@Override
		public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Task> callback) {
			long totalCount = DatabaseHelper.getTaskDao().countByCriteria(taskCriteria);
			int offset = params.requestedStartPosition;
			int count = params.requestedLoadSize;
			if (offset + count > totalCount) {
				offset = (int) Math.max(0, totalCount - count);
			}
			List<Task> tasks = DatabaseHelper.getTaskDao().queryByCriteria(taskCriteria, offset, count);
			callback.onResult(tasks, offset, (int) totalCount);
		}

		@Override
		public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Task> callback) {
			List<Task> tasks = DatabaseHelper.getTaskDao().queryByCriteria(taskCriteria, params.startPosition, params.loadSize);
			callback.onResult(tasks);
		}
	}

	public static class TaskDataFactory extends DataSource.Factory {

		private MutableLiveData<TaskDataSource> mutableDataSource;
		private TaskDataSource taskDataSource;
		private TaskCriteria taskCriteria;

		TaskDataFactory() {
			this.mutableDataSource = new MutableLiveData<>();
		}

		@NonNull
		@Override
		public DataSource create() {
			taskDataSource = new TaskDataSource(taskCriteria);
			mutableDataSource.postValue(taskDataSource);
			return taskDataSource;
		}

		void setTaskCriteria(TaskCriteria taskCriteria) {
			this.taskCriteria = taskCriteria;
		}

		public TaskCriteria getTaskCriteria() {
			return taskCriteria;
		}
	}

	private void initializeList() {
		PagedList.Config config = new PagedList.Config.Builder().setEnablePlaceholders(true).setInitialLoadSizeHint(16).setPageSize(8).build();

		LivePagedListBuilder taskListBuilder = new LivePagedListBuilder(taskDataFactory, config);
		tasks = taskListBuilder.build();
	}
}
