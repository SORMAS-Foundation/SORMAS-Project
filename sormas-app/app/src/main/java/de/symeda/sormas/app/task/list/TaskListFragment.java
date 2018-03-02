package de.symeda.sormas.app.task.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.BaseListActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventDao;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.core.SearchStrategy;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.task.TaskFormNavigationCapsule;
import de.symeda.sormas.app.task.read.TaskReadActivity;

/**
 * Created by Orson on 02/12/2017.
 */

public class TaskListFragment extends BaseListActivityFragment<TaskListAdapter> implements OnListItemClickListener {


    public static final String KEY_CASE_UUID = "caseUuid";
    public static final String KEY_CONTACT_UUID = "contactUuid";
    public static final String KEY_EVENT_UUID = "eventUuid";

    private TaskStatus filterStatus = null;
    private SearchStrategy searchStrategy = null;
    String recordUuid = null;

    private List<Task> tasks;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerViewForList;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveFilterStatusState(outState, filterStatus);
        SaveSearchStrategyState(outState, searchStrategy);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        filterStatus = (TaskStatus) getFilterStatusArg(arguments);
        searchStrategy = (SearchStrategy) getSearchStrategyArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewForList = (RecyclerView)view.findViewById(R.id.recyclerViewForList);

        return view;
    }

    @Override
    public TaskListAdapter getNewListAdapter() {
        return new TaskListAdapter(this.getActivity(), R.layout.row_task_list_item_layout, this,
                this.tasks);
    }

    @Override
    public void onListItemClick(View view, int position, Object item) {
        Task t = (Task)item;
        TaskFormNavigationCapsule dataCapsule = new TaskFormNavigationCapsule(getContext(),
                t.getUuid(), t.getTaskStatus());
        TaskReadActivity.goToActivity(getActivity(), dataCapsule);
    }

    @Override
    public void onResume() {
        super.onResume();


        if (searchStrategy == SearchStrategy.BY_FILTER_STATUS) {

            //TODO: Orson - Remove - used only during dev
            if (filterStatus == null)
                filterStatus = TaskStatus.PENDING;

            //TODO: Orson - Replace with real data
            if (filterStatus == TaskStatus.PENDING) {
                tasks = DatabaseHelper.getTaskDao().queryMyPending();
                //tasks = MemoryDatabaseHelper.TASK.getPendingTasks(20);
            } else if (filterStatus == TaskStatus.NOT_EXECUTABLE) {
                tasks = DatabaseHelper.getTaskDao().queryMyNotExecutable();
                //tasks = MemoryDatabaseHelper.TASK.getNotExecutableTasks(20);
            } else {
                tasks = DatabaseHelper.getTaskDao().queryMyDoneOrRemoved();
                //tasks = MemoryDatabaseHelper.TASK.getDoneTasks(20);
            }

            getCommunicator().updateSubHeadingTitle(filterStatus.toString());
        } else if (searchStrategy == SearchStrategy.BY_CASE_ID) {
            final CaseDao caseDao = DatabaseHelper.getCaseDao();
            final Case caze = caseDao.queryUuid(recordUuid);
            if(caze != null) {
                tasks = DatabaseHelper.getTaskDao().queryByCase(caze);
            } else {
                tasks = new ArrayList<>();
            }

            String format = getContext().getResources()
                    .getString(R.string.heading_level2_1_tasks_list_by_case);
            getCommunicator().updateSubHeadingTitle(String.format(format, caze.getUuid()));
        } else if (searchStrategy == SearchStrategy.BY_CONTACT_ID) {
            final ContactDao contactDao = DatabaseHelper.getContactDao();
            final Contact contact = contactDao.queryUuid(recordUuid);
            if (contact != null) {
                tasks = DatabaseHelper.getTaskDao().queryByContact(contact);
            } else {
                tasks = new ArrayList<>();
            }

            String format = getContext().getResources()
                    .getString(R.string.heading_level2_1_tasks_list_by_contact);
            getCommunicator().updateSubHeadingTitle(String.format(format, contact.getUuid()));
        } else if (searchStrategy == SearchStrategy.BY_EVENT_ID) {
            final EventDao eventDao = DatabaseHelper.getEventDao();
            final Event event = eventDao.queryUuid(recordUuid);
            if (event != null) {
                tasks = DatabaseHelper.getTaskDao().queryByEvent(event);
            } else {
                tasks = new ArrayList<>();
            }

            String format = getContext().getResources()
                    .getString(R.string.heading_level2_1_tasks_list_by_event);
            getCommunicator().updateSubHeadingTitle(String.format(format, event.getUuid()));
        }

        if (tasks == null)
            getCommunicator().updateSubHeadingTitle(getResources().getString(R.string.heading_no_record_found));

        this.getListAdapter().replaceAll(tasks);

        //((TasksListArrayAdapter)getListAdapter()).updateUnreadIndicator();
        this.getListAdapter().notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerViewForList.setLayoutManager(linearLayoutManager);
        recyclerViewForList.setAdapter(getListAdapter());
    }

    public static TaskListFragment newInstance(TaskListCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(TaskListFragment.class, capsule);
    }

}
