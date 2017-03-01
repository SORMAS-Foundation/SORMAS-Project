package de.symeda.sormas.app.task;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import de.symeda.sormas.api.task.TaskHelper;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventDao;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskDao;
import de.symeda.sormas.app.caze.CaseEditActivity;
import de.symeda.sormas.app.contact.ContactEditActivity;
import de.symeda.sormas.app.databinding.TaskFragmentLayoutBinding;
import de.symeda.sormas.app.event.EventEditActivity;
import de.symeda.sormas.app.util.FormTab;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 *
 */
public class TaskTab extends FormTab {

    private TaskFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.task_fragment_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {

        final String taskUuid = (String) getArguments().getString(Task.UUID);
        final TaskDao taskDao = DatabaseHelper.getTaskDao();
        final Task task = taskDao.queryUuid(taskUuid);

        binding.setTask(task);
        super.onResume();

        if(binding.getTask().getCaze() == null) {
            binding.taskCaze.setVisibility(View.GONE);
        }
        if(binding.getTask().getContact() == null) {
            binding.taskContact.setVisibility(View.GONE);
        }
        if(binding.getTask().getEvent() == null) {
            binding.taskEvent.setVisibility(View.GONE);
        }

        if(binding.getTask().getCreatorUser() == null) {
            binding.taskCreatorUser.setVisibility(View.GONE);
        }

        List<TaskStatus> possibleStatusChanges = TaskHelper.getPossibleStatusChanges(task.getTaskStatus(), ConfigProvider.getUser().getUserRole());

        if (possibleStatusChanges.contains(TaskStatus.NOT_EXECUTABLE)) {
            binding.taskNotExecutableBtn.setVisibility(View.VISIBLE);
            binding.taskNotExecutableBtn.setText(TaskStatus.NOT_EXECUTABLE.getChangeString());
            binding.taskNotExecutableBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!binding.taskAssigneeReply.getValue().isEmpty()) {
                        taskDao.changeTaskStatus(task, TaskStatus.NOT_EXECUTABLE);
                        reloadFragment();
                    } else {
                        Toast.makeText(getContext(), "An assignee reply is required to set the task status to not executable.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            binding.taskNotExecutableBtn.setVisibility(View.GONE);
        }

        if (possibleStatusChanges.contains(TaskStatus.DONE)) {
            binding.taskDoneBtn.setVisibility(View.VISIBLE);
            binding.taskDoneBtn.setText(TaskStatus.DONE.getChangeString());
            binding.taskDoneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    taskDao.changeTaskStatus(task, TaskStatus.DONE);
                    reloadFragment();
                }
            });
        }
        else {
            binding.taskDoneBtn.setVisibility(View.GONE);
        }

        if (!binding.getTask().getAssigneeUser().equals(ConfigProvider.getUser())) {
            binding.taskAssigneeReply.setVisibility(View.GONE);
            binding.taskDoneBtn.setVisibility(View.GONE);
            binding.taskNotExecutableBtn.setVisibility(View.GONE);
        }

        if (binding.getTask().getCreatorComment() == null || binding.getTask().getCreatorComment().isEmpty()) {
            binding.taskCreatorComment.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.taskCaze.makeLink(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.getTask().getCaze() != null) {
                    final CaseDao caseDao = DatabaseHelper.getCaseDao();
                    final Case caze = caseDao.queryUuid(binding.getTask().getCaze().getUuid());
                    showCaseEditView(caze);
                }
            }
        });
        binding.taskContact.makeLink(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.getTask().getContact() != null) {
                    final ContactDao contactDao = DatabaseHelper.getContactDao();
                    final Contact contact = contactDao.queryUuid(binding.getTask().getContact().getUuid());
                    showContactEditView(contact);
                }
            }
        });
        binding.taskEvent.makeLink(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.getTask().getEvent() != null) {
                    final EventDao eventDao = DatabaseHelper.getEventDao();
                    final Event event = eventDao.queryUuid(binding.getTask().getEvent().getUuid());
                    showEventEditView(event);
                }
            }
        });
    }

    public void showCaseEditView(Case caze) {
        Intent intent = new Intent(getActivity(), CaseEditActivity.class);
        intent.putExtra(CaseEditActivity.KEY_CASE_UUID, caze.getUuid());
        startActivity(intent);
    }

    public void showContactEditView(Contact contact) {
        Intent intent = new Intent(getActivity(), ContactEditActivity.class);
        intent.putExtra(ContactEditActivity.KEY_CONTACT_UUID, contact.getUuid());
        startActivity(intent);
    }

    public void showEventEditView(Event event) {
        Intent intent = new Intent(getActivity(), EventEditActivity.class);
        intent.putExtra(EventEditActivity.KEY_EVENT_UUID, event.getUuid());
        startActivity(intent);
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getTask();
    }

}