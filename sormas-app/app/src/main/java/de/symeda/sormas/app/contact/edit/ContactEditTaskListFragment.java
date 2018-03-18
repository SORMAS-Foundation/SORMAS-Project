package de.symeda.sormas.app.contact.edit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.contact.ContactFormNavigationCapsule;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentEditListLayoutBinding;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.task.TaskFormNavigationCapsule;
import de.symeda.sormas.app.task.edit.TaskEditActivity;

/**
 * Created by Orson on 15/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class ContactEditTaskListFragment extends BaseEditActivityFragment<FragmentEditListLayoutBinding, List<Task>> implements OnListItemClickListener {

    private String recordUuid;
    private ContactClassification pageStatus = null;
    private List<Task> record;
    private FragmentEditListLayoutBinding binding;
    private ContactEditTaskListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        pageStatus = (ContactClassification) getPageStatusArg(arguments);
    }

    @Override
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public List<Task> getPrimaryData() {
        return null;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Contact contact = DatabaseHelper.getContactDao().queryUuidReference(recordUuid);

            if (contact != null) {
                resultHolder.forList().add(DatabaseHelper.getTaskDao().queryByContact(contact));
            } else {
                resultHolder.forList().add(new ArrayList<Task>());
            }
        } else {
            ITaskResultHolderIterator listIterator = resultHolder.forList().iterator();
            if (listIterator.hasNext()) {
                record = listIterator.next();

            }
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentEditListLayoutBinding contentBinding) {
        adapter = new ContactEditTaskListAdapter(this.getActivity(), R.layout.row_edit_task_list_item_layout, this, record);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        contentBinding.recyclerViewForList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAfterLayoutBinding(FragmentEditListLayoutBinding contentBinding) {

    }

    @Override
    public void onResume() {
        super.onResume();

        final SwipeRefreshLayout swiperefresh = (SwipeRefreshLayout)getRootBinding().getRoot()
                .findViewById(R.id.swiperefresh);

        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBaseEditActivity().synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly,
                        true, false, swiperefresh, null);
            }
        });
    }

    @Override
    public int getRootEditLayout() {
        return R.layout.fragment_root_list_edit_layout;
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_edit_list_layout;
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Task task = (Task)item;
        TaskFormNavigationCapsule dataCapsule = new TaskFormNavigationCapsule(getContext(),
                task.getUuid(), task.getTaskStatus());
        TaskEditActivity.goToActivity(getActivity(), dataCapsule);

        /*Task r = (Task)item;
        TaskFormNavigationCapsule dataCapsule = new TaskFormNavigationCapsule(getContext(), r.getUuid(), r.getTaskStatus());
        ContactEditTaskInfoActivity.goToActivity(getActivity(), dataCapsule);*/
    }

    public static ContactEditTaskListFragment newInstance(IActivityCommunicator activityCommunicator, ContactFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, ContactEditTaskListFragment.class, capsule);
    }

}