package de.symeda.sormas.app.task;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventDao;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.util.SyncCallback;

/**
 * Created by Stefan Szczesny on 24.10.2016.
 */
public class TasksListFragment extends ListFragment {

    public static final String ARG_FILTER_STATUS = "filterStatus";
    public static final String KEY_CASE_UUID = "caseUuid";
    public static final String KEY_CONTACT_UUID = "contactUuid";
    public static final String KEY_EVENT_UUID = "eventUuid";

    private String parentCaseUuid;
    private String parentContactUuid;
    private String parentEventUuid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_layout, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle arguments = getArguments();
        TaskStatus taskStatus = null;
        if (arguments.containsKey(ARG_FILTER_STATUS)) {
            taskStatus = (TaskStatus)arguments.getSerializable(ARG_FILTER_STATUS);
        }

        List<Task> tasks;
        if(arguments.containsKey(KEY_CASE_UUID)) {
            parentCaseUuid = (String)arguments.get(KEY_CASE_UUID);
            final CaseDao caseDao = DatabaseHelper.getCaseDao();
            final Case caze = caseDao.queryUuid(parentCaseUuid);
            if(caze != null) {
                tasks = DatabaseHelper.getTaskDao().queryByCase(caze);
            } else {
                tasks = new ArrayList<>();
            }
        } else if(arguments.containsKey(KEY_CONTACT_UUID)) {
            parentContactUuid = (String) arguments.get(KEY_CONTACT_UUID);
            final ContactDao contactDao = DatabaseHelper.getContactDao();
            final Contact contact = contactDao.queryUuid(parentContactUuid);
            if (contact != null) {
                tasks = DatabaseHelper.getTaskDao().queryByContact(contact);
            } else {
                tasks = new ArrayList<>();
            }
        } else if(arguments.containsKey(KEY_EVENT_UUID)) {
            parentEventUuid = (String) arguments.get(KEY_EVENT_UUID);
            final EventDao eventDao = DatabaseHelper.getEventDao();
            final Event event = eventDao.queryUuid(parentEventUuid);
            if (event != null) {
                tasks = DatabaseHelper.getTaskDao().queryByEvent(event);
            } else {
                tasks = new ArrayList<>();
            }
        } else {
            if (taskStatus == TaskStatus.PENDING) {
                tasks = DatabaseHelper.getTaskDao().queryMyPending();
            } else if (taskStatus == TaskStatus.NOT_EXECUTABLE) {
                tasks = DatabaseHelper.getTaskDao().queryMyNotExecutable();
            } else {
                tasks = DatabaseHelper.getTaskDao().queryMyDoneOrRemoved();
            }
        }

        ArrayAdapter<Task> listAdapter = (ArrayAdapter<Task>)getListAdapter();
        listAdapter.clear();
        listAdapter.addAll(tasks);

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout)getView().findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (RetroProvider.isConnected()) {
                    SyncTasksTask.syncTasksWithCallback(getActivity().getSupportFragmentManager(), getContext(), getContext(), new SyncCallback() {
                        @Override
                        public void call(boolean syncFailed) {
                            refreshLayout.setRefreshing(false);
                            if (!syncFailed) {
                                Snackbar.make(getActivity().findViewById(R.id.base_layout), R.string.snackbar_sync_success, Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(getActivity().findViewById(R.id.base_layout), R.string.snackbar_sync_error, Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    refreshLayout.setRefreshing(false);
                    Snackbar.make(getActivity().findViewById(R.id.base_layout), R.string.snackbar_no_connection, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TasksListArrayAdapter adapter = new TasksListArrayAdapter(
                this.getActivity(),           // Context for the activity.
                R.layout.tasks_list_item);    // Layout to use (create)

        setListAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent,
                    View viewClicked,
                    int position, long id) {
                Task task = (Task)getListAdapter().getItem(position);
                showTaskEditView(task);
            }
        });
    }

    public void showTaskEditView(Task task) {
        Intent intent = new Intent(getActivity(), TaskEditActivity.class);
        intent.putExtra(Task.UUID, task.getUuid());
        if(parentCaseUuid != null) {
            intent.putExtra(KEY_CASE_UUID, parentCaseUuid);
        }
        if(parentContactUuid != null) {
            intent.putExtra(KEY_CONTACT_UUID, parentContactUuid);
        }
        if(parentEventUuid != null) {
            intent.putExtra(KEY_EVENT_UUID, parentEventUuid);
        }
        startActivity(intent);
    }
}
