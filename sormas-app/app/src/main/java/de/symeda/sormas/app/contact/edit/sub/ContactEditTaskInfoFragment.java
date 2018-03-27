package de.symeda.sormas.app.contact.edit.sub;

import android.content.res.Resources;
import android.os.AsyncTask;
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
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.caze.edit.CaseEditActivity;
import de.symeda.sormas.app.component.OnLinkClickListener;
import de.symeda.sormas.app.contact.edit.ContactEditActivity;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentTaskEditLayoutBinding;
import de.symeda.sormas.app.event.edit.EventEditActivity;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;
import de.symeda.sormas.app.shared.TaskFormNavigationCapsule;

/**
 * Created by Orson on 15/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class ContactEditTaskInfoFragment extends BaseEditActivityFragment<FragmentTaskEditLayoutBinding, Task, Task> {

    private AsyncTask onResumeTask;
    private Tracker tracker;
    private String recordUuid = null;
    private TaskStatus pageStatus = null;
    private Task record;

    private View.OnClickListener doneCallback;
    private View.OnClickListener notExecCallback;
    private OnLinkClickListener caseLinkCallback;
    private OnLinkClickListener contactLinkCallback;
    private OnLinkClickListener eventLinkCallback;

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
        Resources r = getResources();
        return r.getString(R.string.caption_task_information);
    }

    @Override
    public Task getPrimaryData() {
        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Task task = getActivityRootData();

            if (task != null) {
                if (task.isUnreadOrChildUnread())
                    DatabaseHelper.getTaskDao().markAsRead(task);
            }

            resultHolder.forItem().add(task);
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
    protected void updateUI(FragmentTaskEditLayoutBinding contentBinding, Task task) {

    }

    @Override
    public void onPageResume(FragmentTaskEditLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().showPreloader();
                    //getActivityCommunicator().hideFragmentView();
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    Task task = getActivityRootData();

                    if (task != null) {
                        if (task.isUnreadOrChildUnread())
                            DatabaseHelper.getTaskDao().markAsRead(task);
                    }

                    resultHolder.forItem().add(task);
                }
            });
            onResumeTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().hidePreloader();
                    //getActivityCommunicator().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

                    if (itemIterator.hasNext())
                        record =  itemIterator.next();

                    if (record != null)
                        requestLayoutRebind();
                    else {
                        getActivity().finish();
                    }
                }
            });
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }
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
                Case record = (Case)item;

                CaseFormNavigationCapsule dataCapsule = (CaseFormNavigationCapsule)new CaseFormNavigationCapsule(getContext(),
                        record.getUuid()).setEditPageStatus(record.getInvestigationStatus()).setTaskUuid(record.getUuid());
                CaseEditActivity.goToActivity(getActivity(), dataCapsule);

                //intent.putExtra(TaskForm.KEY_TASK_UUID, binding.getTask().getUuid());

            }
        };

        contactLinkCallback = new OnLinkClickListener() {
            @Override
            public void onClick(View v, Object item) {
                Contact record = (Contact)item;

                ContactFormNavigationCapsule dataCapsule = (ContactFormNavigationCapsule)new ContactFormNavigationCapsule(getContext(),
                        record.getUuid(), record.getContactClassification()).setTaskUuid(record.getUuid());
                ContactEditActivity.goToActivity(getActivity(), dataCapsule);
            }
        };

        eventLinkCallback = new OnLinkClickListener() {
            @Override
            public void onClick(View v, Object item) {
                Event record = (Event)item;

                EventFormNavigationCapsule dataCapsule = (EventFormNavigationCapsule)new EventFormNavigationCapsule(getContext(),
                        record.getUuid(), record.getEventStatus()).setTaskUuid(record.getUuid());
                EventEditActivity.goToActivity(getActivity(), dataCapsule);
            }
        };
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    @Override
    public boolean showSaveAction() {
        return true;
    }

    @Override
    public boolean showAddAction() {
        return false;
    }

    public static ContactEditTaskInfoFragment newInstance(IActivityCommunicator activityCommunicator, TaskFormNavigationCapsule capsule, Task activityRootData)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, ContactEditTaskInfoFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }
}
