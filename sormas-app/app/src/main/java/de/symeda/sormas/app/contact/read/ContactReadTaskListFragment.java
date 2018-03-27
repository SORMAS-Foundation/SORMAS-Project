package de.symeda.sormas.app.contact.read;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentContactReadTaskLayoutBinding;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.shared.TaskFormNavigationCapsule;
import de.symeda.sormas.app.task.read.TaskReadActivity;

/**
 * Created by Orson on 01/01/2018.
 */

public class ContactReadTaskListFragment extends BaseReadActivityFragment<FragmentContactReadTaskLayoutBinding, List<Task>> implements OnListItemClickListener {

    private String recordUuid;
    private FollowUpStatus followUpStatus;
    private ContactClassification contactClassification = null;
    private List<Task> record;

    private ContactReadTaskListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveFilterStatusState(outState, followUpStatus);
        SavePageStatusState(outState, contactClassification);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        followUpStatus = (FollowUpStatus) getFilterStatusArg(arguments);
        contactClassification = (ContactClassification) getPageStatusArg(arguments);
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Contact contact = DatabaseHelper.getContactDao().queryUuid(recordUuid);

            if (contact != null) {
                resultHolder.forList().add(DatabaseHelper.getTaskDao().queryByContact(contact));
            } else {
                resultHolder.forList().add(new ArrayList<Task>());
            }
        } else {
            ITaskResultHolderIterator listIterator = resultHolder.forList().iterator();

            if (listIterator.hasNext())
                record =  listIterator.next();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentContactReadTaskLayoutBinding contentBinding) {
        adapter = new ContactReadTaskListAdapter(ContactReadTaskListFragment.this.getActivity(),
                R.layout.row_read_contact_task_list_item_layout, ContactReadTaskListFragment.this, record);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        contentBinding.recyclerViewForList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAfterLayoutBinding(FragmentContactReadTaskLayoutBinding contentBinding) {

    }

    @Override
    public void onPageResume(FragmentContactReadTaskLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        final SwipeRefreshLayout swiperefresh = (SwipeRefreshLayout)this.getView().findViewById(R.id.swiperefresh);
        if (swiperefresh != null) {
            swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getActivityCommunicator().synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly, true, false, true, swiperefresh, null);
                }
            });
        }

        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

    }

    @Override
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public List<Task> getPrimaryData() {
        return record;
    }

    @Override
    public int getRootReadLayout() {
        return R.layout.fragment_root_list_edit_layout;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_contact_read_task_layout;
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Task task = (Task)item;
        TaskFormNavigationCapsule dataCapsule = new TaskFormNavigationCapsule(getContext(),
                task.getUuid(), task.getTaskStatus());
        TaskReadActivity.goToActivity(getActivity(), dataCapsule);

        /*Task task = (Task)item;

        TaskFormNavigationCapsule dataCapsule = new TaskFormNavigationCapsule(getContext(),
                task.getUuid(), task.getTaskStatus());
        ContactReadTaskInfoActivity.goToActivity(getActivity(), dataCapsule);*/

        /*if(record != null) {
            Intent intent = new Intent(getActivity(), ContactReadTaskInfoActivity.class);
            //intent.putExtra(ConstantHelper.ARG_FILTER_STATUS, record.getContact().getFollowUpStatus());
            intent.putExtra(ConstantHelper.KEY_DATA_UUID, record.getUuid());
            intent.putExtra(ConstantHelper.ARG_PAGE_STATUS, record.getTaskStatus());
            intent.putExtra(IStatusElaborator.ARG_TASK_STATUS, record.getTaskStatus());
            intent.putExtra(IStatusElaborator.ARG_FOLLOW_UP_STATUS, followUpStatus);

            startActivity(intent);
        }*/
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    public static ContactReadTaskListFragment newInstance(IActivityCommunicator activityCommunicator, ContactFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, ContactReadTaskListFragment.class, capsule);
    }
}