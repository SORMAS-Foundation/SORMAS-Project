/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.task.list;

import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.task.Task;

public class TaskListViewModel extends ViewModel {

    private MutableLiveData<List<Task>> tasks;
    private TaskStatus taskStatus = TaskStatus.PENDING;
    private Case caze;
    private Contact contact;
    private Event event;

    public LiveData<List<Task>> getTasks() {
        if (tasks == null) {
            tasks = new MutableLiveData<>();
            loadTasks();
        }

        return tasks;
    }

    public LiveData<List<Task>> getTasks(Case caze) {
        this.caze = caze;
        return getTasks();
    }

    public LiveData<List<Task>> getTasks(Contact contact) {
        this.contact = contact;
        return getTasks();
    }

    public LiveData<List<Task>> getTasks(Event event) {
        this.event = event;
        return getTasks();
    }

    void setTaskStatusAndReload(TaskStatus taskStatus) {
        if (this.taskStatus == taskStatus) {
            return;
        }

        this.taskStatus = taskStatus;
        loadTasks();
    }

    private void loadTasks() {
        new LoadTasksTask(this).execute();
    }

    private static class LoadTasksTask extends AsyncTask<Void, Void, List<Task>> {
        private TaskListViewModel model;

        LoadTasksTask(TaskListViewModel model) {
            this.model = model;
        }

        @Override
        protected List<Task> doInBackground(Void... args) {
            if (model.caze != null) {
                return DatabaseHelper.getTaskDao().queryByCase(model.caze);
            } else if (model.contact != null) {
                return DatabaseHelper.getTaskDao().queryByContact(model.contact);
            } else if (model.event != null) {
                return DatabaseHelper.getTaskDao().queryByEvent(model.event);
            } else {
                switch (model.taskStatus) {
                    case PENDING:
                        return DatabaseHelper.getTaskDao().queryAllPending();
                    case DONE:
                    case REMOVED:
                        return DatabaseHelper.getTaskDao().queryAllDoneOrRemoved();
                    case NOT_EXECUTABLE:
                        return DatabaseHelper.getTaskDao().queryAllNotExecutable();
                    default:
                        throw new IllegalArgumentException(model.taskStatus.toString());
                }
            }
        }

        @Override
        protected void onPostExecute(List<Task> data) {
            model.tasks.setValue(data);
        }
    }

}
