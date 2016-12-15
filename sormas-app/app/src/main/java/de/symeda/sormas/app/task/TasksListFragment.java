package de.symeda.sormas.app.task;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
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
import de.symeda.sormas.app.backend.task.Task;

/**
 * Created by Stefan Szczesny on 24.10.2016.
 */
public class TasksListFragment extends ListFragment {

    public static final String ARG_FILTER_STATUS = "filterStatus";
    public static final String KEY_CASE_UUID = "caseUuid";
    public static final String KEY_CONTACT_UUID = "contactUuid";

    private String parentCaseUuid;
    private String parentContactUuid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cases_list_layout, container, false);
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
            final Case caze = caseDao.queryUuid((String)arguments.get(KEY_CASE_UUID));
            if(caze != null) {
                tasks = DatabaseHelper.getTaskDao().queryForCase(caze);
            } else {
                tasks = new ArrayList<>();
            }
        } else if(arguments.containsKey(KEY_CONTACT_UUID)) {
            parentContactUuid = (String)arguments.get(KEY_CONTACT_UUID);
            final ContactDao contactDao = DatabaseHelper.getContactDao();
            final Contact contact = contactDao.queryUuid((String)arguments.get(KEY_CONTACT_UUID));
            if(contact != null) {
                tasks = DatabaseHelper.getTaskDao().queryForContact(contact);
            } else {
                tasks = new ArrayList<>();
            }
        } else {
            if (taskStatus == TaskStatus.PENDING) {
                tasks = DatabaseHelper.getTaskDao().queryPending();
            } else if (taskStatus == TaskStatus.NOT_EXECUTABLE) {
                tasks = DatabaseHelper.getTaskDao().queryNotExecutable();
            } else {
                tasks = DatabaseHelper.getTaskDao().queryDoneOrDiscarded();
            }
        }

        ArrayAdapter<Task> listAdapter = (ArrayAdapter<Task>)getListAdapter();
        listAdapter.clear();
        listAdapter.addAll(tasks);
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
        startActivity(intent);
    }
}
