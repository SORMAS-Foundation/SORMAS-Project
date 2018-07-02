package de.symeda.sormas.app.caze.edit;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.List;

import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.shared.TaskFormNavigationCapsule;
import de.symeda.sormas.app.task.edit.TaskEditActivity;

public class CaseEditTaskListFragment extends BaseEditFragment<FragmentFormListLayoutBinding, List<Task>, Case> implements OnListItemClickListener {

    private List<Task> record;

    private CaseEditTaskListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

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
    protected void prepareFragmentData(Bundle savedInstanceState) {
        Case caze = getActivityRootData();
        record = DatabaseHelper.getTaskDao().queryByCase(caze);
    }

    @Override
    public void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
        showEmptyListHint(record, R.string.entity_task);

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
    public boolean isShowAddAction() {
        return false;
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Task task = (Task) item;
        TaskFormNavigationCapsule dataCapsule = new TaskFormNavigationCapsule(getContext(), task.getUuid(), task.getTaskStatus());
        TaskEditActivity.goToActivity(getActivity(), dataCapsule);
    }

    public static CaseEditTaskListFragment newInstance(CaseFormNavigationCapsule capsule, Case activityRootData) {
        return newInstance(CaseEditTaskListFragment.class, capsule, activityRootData);
    }
}
