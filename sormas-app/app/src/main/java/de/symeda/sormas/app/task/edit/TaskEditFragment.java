package de.symeda.sormas.app.task.edit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.caze.CaseFormNavigationCapsule;
import de.symeda.sormas.app.caze.edit.CaseEditActivity;
import de.symeda.sormas.app.component.OnLinkClickListener;
import de.symeda.sormas.app.contact.ContactFormNavigationCapsule;
import de.symeda.sormas.app.contact.edit.ContactEditActivity;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentTaskEditLayoutBinding;
import de.symeda.sormas.app.event.EventFormNavigationCapsule;
import de.symeda.sormas.app.event.edit.EventEditActivity;
import de.symeda.sormas.app.task.TaskFormNavigationCapsule;

/**
 * Created by Orson on 22/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TaskEditFragment extends BaseEditActivityFragment<FragmentTaskEditLayoutBinding, Task> {

    private Tracker tracker;

    private String recordUuid = null;
    private TaskStatus pageStatus = null;
    private Task record;

    private View.OnClickListener doneCallback;
    private View.OnClickListener notExecCallback;
    private OnLinkClickListener caseLinkCallback;
    private OnLinkClickListener contactLinkCallback;
    private OnLinkClickListener eventLinkCallback;

    private Symptoms symptom = null;
    private Person person = null;
    private Event event = null;


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
        pageStatus = (TaskStatus) getPageStatusArg(arguments);
    }

    @Override
    protected String getSubHeadingTitle() {
        String title = "";

        if (pageStatus != null) {
            title = pageStatus.toString();
        }

        return title;
    }

    @Override
    public Task getPrimaryData() {
        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            resultHolder.forItem().add(DatabaseHelper.getTaskDao().queryUuid(recordUuid));
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            //Item Data
            if (itemIterator.hasNext())
                record =  itemIterator.next();

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentTaskEditLayoutBinding contentBinding) {
        SormasApplication application = (SormasApplication) getContext().getApplicationContext();
        tracker = application.getDefaultTracker();

        if(record.getCaze() == null) {
            contentBinding.txtAssocCaze.setVisibility(View.GONE);
        }
        if(record.getContact() == null) {
            contentBinding.txtAssocContact.setVisibility(View.GONE);
        }
        if(record.getEvent() == null) {
            contentBinding.txtAssocEvent.setVisibility(View.GONE);
        }

        if(record.getCreatorUser() == null) {
            contentBinding.txtCreatorUser.setVisibility(View.GONE);
        }

        contentBinding.btnNotExecutable.setVisibility((record.getTaskStatus() == TaskStatus.PENDING) ? View.VISIBLE  : View.GONE);
        contentBinding.btnDone.setVisibility((record.getTaskStatus() == TaskStatus.PENDING) ? View.VISIBLE  : View.GONE);

        if (!record.getAssigneeUser().equals(ConfigProvider.getUser())) {
            contentBinding.txtCommentOnExec.setVisibility(View.GONE);
            contentBinding.btnDone.setVisibility(View.GONE);
            contentBinding.btnNotExecutable.setVisibility(View.GONE);
        }

        if (record.getCreatorComment() == null || record.getCreatorComment().isEmpty()) {
            contentBinding.txtCreatorComment.setVisibility(View.GONE);
        }

        contentBinding.setData(record);
        contentBinding.setDoneCallback(doneCallback);
        contentBinding.setNotExecCallback(notExecCallback);
        contentBinding.setCaseLinkCallback(caseLinkCallback);
        contentBinding.setContactLinkCallback(contactLinkCallback);
        contentBinding.setEventLinkCallback(eventLinkCallback);
    }

    @Override
    public void onAfterLayoutBinding(FragmentTaskEditLayoutBinding contentBinding) {

    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_task_edit_layout;
    }

    private void setupCallback() {

        doneCallback = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

        notExecCallback = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContentBinding().txtCommentOnExec.enableErrorState((INotificationContext)getActivity(),"There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable. There are many variations of passages of Lorem Ipsum available.");
                //binding.checkbox1.enableErrorState("Hello");
                Toast.makeText(getContext(), "Not Executable", Toast.LENGTH_SHORT).show();
            }
        };

        caseLinkCallback = new OnLinkClickListener() {
            @Override
            public void onClick(View v, Object item) {
                if (item == null)
                    return;

                Task task = (Task)item;
                Case caze = task.getCaze();

                if (caze == null)
                    return;

                CaseFormNavigationCapsule dataCapsule = (CaseFormNavigationCapsule)new CaseFormNavigationCapsule(getContext(),
                        caze.getUuid()).setEditPageStatus(caze.getInvestigationStatus()).setTaskUuid(task.getUuid());
                CaseEditActivity.goToActivity(getActivity(), dataCapsule);

            }
        };

        contactLinkCallback = new OnLinkClickListener() {
            @Override
            public void onClick(View v, Object item) {
                if (item == null)
                    return;

                Task task = (Task)item;
                Contact contact = task.getContact();

                if (contact == null)
                    return;

                ContactFormNavigationCapsule dataCapsule = (ContactFormNavigationCapsule)new ContactFormNavigationCapsule(getContext(),
                        contact.getUuid(), contact.getContactClassification()).setTaskUuid(task.getUuid());
                ContactEditActivity.goToActivity(getActivity(), dataCapsule);
            }
        };

        eventLinkCallback = new OnLinkClickListener() {
            @Override
            public void onClick(View v, Object item) {
                if (item == null)
                    return;

                Task task = (Task)item;
                Event event = task.getEvent();

                if (event == null)
                    return;

                EventFormNavigationCapsule dataCapsule = (EventFormNavigationCapsule)new EventFormNavigationCapsule(getContext(),
                        event.getUuid(), event.getEventStatus()).setTaskUuid(task.getUuid());
                EventEditActivity.goToActivity(getActivity(), dataCapsule);
            }
        };
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    public boolean makeHeightMatchParent() {
        return true;
    }

    public static TaskEditFragment newInstance(IActivityCommunicator activityCommunicator, TaskFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, TaskEditFragment.class, capsule);
    }
}
