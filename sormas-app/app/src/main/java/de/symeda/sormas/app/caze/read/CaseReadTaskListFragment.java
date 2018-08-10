package de.symeda.sormas.app.caze.read;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.List;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.task.read.TaskReadActivity;

public class CaseReadTaskListFragment extends BaseReadFragment<FragmentFormListLayoutBinding, List<Task>, Case> implements OnListItemClickListener {

    private List<Task> record;

    private CaseReadTaskListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    public static CaseReadTaskListFragment newInstance(Case activityRootData) {
        return newInstance(CaseReadTaskListFragment.class, null, activityRootData);
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        Case caze = getActivityRootData();
        record = DatabaseHelper.getTaskDao().queryByCase(caze);
    }

    @Override
    public void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
        updateEmptyListHint(record);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new CaseReadTaskListAdapter(R.layout.row_read_case_task_list_item_layout, this, record);
        getContentBinding().recyclerViewForList.setLayoutManager(linearLayoutManager);
        getContentBinding().recyclerViewForList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_case_tasks);
    }

    @Override
    public List<Task> getPrimaryData() {
        return null;
    }

    @Override
    public int getRootReadLayout() {
        return R.layout.fragment_root_list_form_layout;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_form_list_layout;
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Task task = (Task) item;
        TaskReadActivity.startActivity(getContext(), task.getUuid());
    }
}
