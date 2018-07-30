package de.symeda.sormas.app.task.read;

import android.os.Bundle;
import android.view.View;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.contact.read.ContactReadActivity;
import de.symeda.sormas.app.databinding.FragmentTaskReadLayoutBinding;
import de.symeda.sormas.app.event.read.EventReadActivity;

public class TaskReadFragment extends BaseReadFragment<FragmentTaskReadLayoutBinding, Task, Task> {

    private Task record;

    public static TaskReadFragment newInstance(Task activityRootData) {
        return newInstance(TaskReadFragment.class, null, activityRootData);
    }

    private void setUpControlListeners(FragmentTaskReadLayoutBinding contentBinding) {
        if (record.getCaze() != null) {
            contentBinding.taskCaze.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Case caze = record.getCaze();
                    if (caze != null) {
                        CaseReadActivity.startActivity(getActivity(), caze.getUuid());
                    }
                }
            });
        }

        if (record.getContact() != null) {
            contentBinding.taskContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Contact contact = record.getContact();
                    if (contact != null) {
                        ContactReadActivity.startActivity(getActivity(), contact.getUuid());
                    }
                }
            });
        }

        if (record.getEvent() != null) {
            contentBinding.taskEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Event event = record.getEvent();
                    if (event != null) {
                        EventReadActivity.startActivity(getActivity(), event.getUuid());
                    }
                }
            });
        }
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
    }

    @Override
    public void onLayoutBinding(FragmentTaskReadLayoutBinding contentBinding) {
        setUpControlListeners(contentBinding);

        contentBinding.setData(record);
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_task_information);
    }

    @Override
    public Task getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_task_read_layout;
    }
}
