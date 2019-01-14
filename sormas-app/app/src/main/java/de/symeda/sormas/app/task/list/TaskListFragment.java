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

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.BaseListFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.task.edit.TaskEditActivity;

public class TaskListFragment extends BaseListFragment<TaskListAdapter> implements OnListItemClickListener {

    private List<Task> tasks;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerViewForList;

    public static TaskListFragment newInstance(TaskStatus listFilter) {
        return newInstance(TaskListFragment.class, null, listFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewForList = view.findViewById(R.id.recyclerViewForList);

        return view;
    }

    @Override
    protected void prepareFragmentData() {
        switch ((TaskStatus) getListFilter()) {
            case PENDING:
                tasks = DatabaseHelper.getTaskDao().queryMyPending();
                break;
            case DONE:
            case REMOVED:
                tasks = DatabaseHelper.getTaskDao().queryMyDoneOrRemoved();
                break;
            case NOT_EXECUTABLE:
                tasks = DatabaseHelper.getTaskDao().queryMyNotExecutable();
                break;
            default:
                throw new IllegalArgumentException(getListFilter().toString());
        }
        getListAdapter().replaceAll(tasks);
    }

    @Override
    public TaskListAdapter getNewListAdapter() {
        return new TaskListAdapter(this.getActivity(), R.layout.row_task_list_item_layout, this, this.tasks);
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Task t = (Task) item;
        TaskEditActivity.startActivity(getContext(), t.getUuid());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerViewForList.setLayoutManager(linearLayoutManager);
        recyclerViewForList.setAdapter(getListAdapter());
    }
}
