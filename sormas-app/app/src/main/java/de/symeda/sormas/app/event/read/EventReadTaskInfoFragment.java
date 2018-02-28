package de.symeda.sormas.app.event.read;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.databinding.FragmentEventReadTaskInfoLayoutBinding;
import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.task.Task;

/**
 * Created by Orson on 30/12/2017.
 */

public class EventReadTaskInfoFragment extends BaseReadActivityFragment<FragmentEventReadTaskInfoLayoutBinding> {

    private Task record;
    private FragmentEventReadTaskInfoLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = DataBindingUtil.inflate(inflater, getRootReadLayout(), container, false);
        record = MemoryDatabaseHelper.TASK.getTasks(1).get(0);

        //TODO: Orson - remove, just for testing purposes only
        binding.txtAssocContact.setVisibility(View.GONE);
        binding.txtAssocEvent.setVisibility(View.GONE);

        binding.setData(record);



        return binding.getRoot();
    }

    @Override
    protected String getSubHeadingTitle() {
        String title = "";

        if (binding != null) {
            String taskTitle = binding.getRoot().getContext().getResources().getString(R.string.const_task);
            title = binding.getData().getTaskContext() + " " + taskTitle;
        }

        return title;
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getData();
    }

    @Override
    public FragmentEventReadTaskInfoLayoutBinding getBinding() {
        return binding;
    }

    @Override
    public Object getRecord() {
        return record;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void showRecordEditView(EventParticipant item) {
        /*Intent intent = new Intent(getActivity(), TaskEditActivity.class);
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
        startActivity(intent);*/
    }

    @Override
    public int getRootReadLayout() {
        return R.layout.fragment_event_read_task_info_layout;
    }
}
