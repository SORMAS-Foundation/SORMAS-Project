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

package de.symeda.sormas.app.caze.edit;

import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.List;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.task.edit.TaskEditActivity;

public class CaseEditTaskListFragment extends BaseEditFragment<FragmentFormListLayoutBinding, List<Task>, Case> implements OnListItemClickListener {

    public static final String TAG = CaseEditTaskListFragment.class.getSimpleName();

    private List<Task> record;
    private CaseEditTaskListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    public static CaseEditTaskListFragment newInstance(Case activityRootData) {
        return newInstance(CaseEditTaskListFragment.class, null, activityRootData);
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_case_tasks);
    }

    @Override
    public List<Task> getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData() {
        Case caze = getActivityRootData();
        record = DatabaseHelper.getTaskDao().queryByCase(caze);
    }

    @Override
    public void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
        updateEmptyListHint(record);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new CaseEditTaskListAdapter(R.layout.row_edit_task_list_item_layout, this, record);
        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        contentBinding.recyclerViewForList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
    public boolean isShowSaveAction() {
        return false;
    }

    @Override
    public boolean isShowNewAction() {
        return ConfigProvider.hasUserRight(UserRight.TASK_CREATE);
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Task task = (Task) item;
        TaskEditActivity.startActivity(getContext(), task.getUuid());
    }
}
