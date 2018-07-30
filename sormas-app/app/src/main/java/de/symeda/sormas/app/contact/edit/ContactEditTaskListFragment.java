package de.symeda.sormas.app.contact.edit;

import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.List;

import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.databinding.FragmentFormListLayoutBinding;
import de.symeda.sormas.app.task.edit.TaskEditActivity;

public class ContactEditTaskListFragment extends BaseEditFragment<FragmentFormListLayoutBinding, List<Task>, Contact> implements OnListItemClickListener {

    private List<Task> record;

    private ContactEditTaskListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    public static ContactEditTaskListFragment newInstance(Contact activityRootData) {
        return newInstance(ContactEditTaskListFragment.class, null, activityRootData);
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_contact_tasks);
    }

    @Override
    public List<Task> getPrimaryData() {
        return null;
    }

    @Override
    protected void prepareFragmentData() {
        Contact contact = getActivityRootData();
        record = DatabaseHelper.getTaskDao().queryByContact(contact);
    }

    @Override
    public void onLayoutBinding(FragmentFormListLayoutBinding contentBinding) {
        showEmptyListHint(record, R.string.entity_task);

        adapter = new ContactEditTaskListAdapter(R.layout.row_edit_task_list_item_layout, this, record);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
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
    public void onListItemClick(View view, int position, Object item) {
        Task task = (Task) item;
        TaskEditActivity.startActivity(getContext(), task.getUuid());
    }
}