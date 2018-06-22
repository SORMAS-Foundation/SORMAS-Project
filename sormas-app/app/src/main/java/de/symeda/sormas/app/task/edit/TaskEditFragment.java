package de.symeda.sormas.app.task.edit;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskDao;
import de.symeda.sormas.app.caze.edit.CaseEditActivity;
import de.symeda.sormas.app.component.OnLinkClickListener;
import de.symeda.sormas.app.contact.edit.ContactEditActivity;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.FragmentTaskEditLayoutBinding;
import de.symeda.sormas.app.event.edit.EventEditActivity;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;
import de.symeda.sormas.app.shared.TaskFormNavigationCapsule;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.ErrorReportingHelper;

/**
 * Created by Orson on 22/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TaskEditFragment extends BaseEditActivityFragment<FragmentTaskEditLayoutBinding, Task, Task> {

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

    private AsyncTask doneCallbackTask;
    private AsyncTask notExecCallbackTask;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        savePageStatusState(outState, pageStatus);
        saveRecordUuidState(outState, recordUuid);
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

            if (itemIterator.hasNext())
                record =  itemIterator.next();

            if (record == null) {
                getActivity().finish();
            }

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

        updateButtonState();

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
    public int getEditLayout() {
        return R.layout.fragment_task_edit_layout;
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

    private void setupCallback() {

        doneCallback = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                        private View button;
                        private String msgTaskClassificationError;

                        @Override
                        public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                            this.button.setEnabled(false);
                            this.msgTaskClassificationError = getResources().getString(R.string.snackbar_task_case_classification_error);
                            //getActivityCommunicator().showPreloader();
                            //getActivityCommunicator().hideFragmentView();
                        }

                        @Override
                        public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) throws Exception {
                            try {
                                final TaskDao taskDao = DatabaseHelper.getTaskDao();
                                taskDao.saveAndSnapshot(record);
                                taskDao.changeTaskStatus(record, TaskStatus.DONE);
                            } catch (DaoException e) {
                                String errorMessage = "Error while trying to update task status";
                                resultHolder.setResultStatus(new BoolResult(false, errorMessage));
                                Log.e(getClass().getName(), errorMessage, e);
                                ErrorReportingHelper.sendCaughtException(tracker, e, record, true);
                            } catch (ValidationException e) {
                                resultHolder.setResultStatus(new BoolResult(false, this.msgTaskClassificationError));
                            }
                        }
                        private IJobDefinition init(View button){
                            this.button = button;
                            return this;
                        }
                    }.init(v));
                    doneCallbackTask = executor.execute(new ITaskResultCallback() {
                        private View button;

                        @Override
                        public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                            //getActivityCommunicator().hidePreloader();
                            //getActivityCommunicator().showFragmentView();

                            if (resultHolder == null) {
                                this.button.setEnabled(true);
                                return;
                            }

                            if (resultStatus.isSuccess()) {
                                if (resultStatus.getMessage().isEmpty()) {
                                    NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.SUCCESS, resultStatus.getMessage());
                                } else {
                                    NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.SUCCESS, R.string.notification_save_task_successful);
                                }

                                getActivityCommunicator().synchronizeChangedData(new Callback() {
                                    @Override
                                    public void call() {
                                        getActivity().finish();
                                    }
                                });
                            } else if (!resultStatus.isSuccess() && !resultStatus.getMessage().isEmpty()) {
                                NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.ERROR, resultStatus.getMessage());
                            } else {
                                NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.ERROR, R.string.notification_save_task_failed);
                            }

                            this.button.setEnabled(true);
                            updateButtonState();
                        }
                        private ITaskResultCallback init(View button){
                            this.button = button;
                            return this;
                        }
                    }.init(v));
                } catch (Exception ex) {
                    v.setEnabled(true);
                    //getActivityCommunicator().hidePreloader();
                    //getActivityCommunicator().showFragmentView();
                }
            }
        };

        notExecCallback = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                try {
                    if(record.getAssigneeReply().isEmpty()) {
                        NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.ERROR, R.string.snackbar_task_reply);
                        return;
                    }

                    ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                        private View button;
                        private String msgTaskStatusChangeError = "";

                        @Override
                        public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                            this.button.setEnabled(false);
                            this.msgTaskStatusChangeError = getResources().getString(R.string.snackbar_task_status_change_error);
                            //getActivityCommunicator().showPreloader();
                            //getActivityCommunicator().hideFragmentView();
                        }

                        @Override
                        public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                            try {
                                final TaskDao taskDao = DatabaseHelper.getTaskDao();
                                taskDao.saveAndSnapshot(record);
                                taskDao.changeTaskStatus(record, TaskStatus.NOT_EXECUTABLE);
                            } catch (DaoException e) {
                                resultHolder.setResultStatus(new BoolResult(false, this.msgTaskStatusChangeError));
                                Log.e(getClass().getName(), "Error while trying to update task status", e);
                                ErrorReportingHelper.sendCaughtException(tracker, e, record, true);
                            } catch (ValidationException e) {
                                resultHolder.setResultStatus(BoolResult.FALSE);
                                // Will not happen here
                            }
                        }
                        private IJobDefinition init(View button){
                            this.button = button;
                            return this;
                        }
                    }.init(v));
                    notExecCallbackTask = executor.execute(new ITaskResultCallback() {
                        private View button;

                        @Override
                        public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                            //getActivityCommunicator().hidePreloader();
                            //getActivityCommunicator().showFragmentView();

                            if (resultHolder == null){
                                this.button.setEnabled(true);
                                return;
                            }

                            if (resultStatus.isSuccess()) {
                                if (resultStatus.getMessage().isEmpty()) {
                                    NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.SUCCESS, resultStatus.getMessage());
                                } else {
                                    NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.SUCCESS, R.string.notification_save_task_successful);
                                }

                                getActivityCommunicator().synchronizeChangedData(new Callback() {
                                    @Override
                                    public void call() {
                                        getActivity().finish();
                                    }
                                });
                            } else if (!resultStatus.isSuccess() && !resultStatus.getMessage().isEmpty()) {
                                NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.ERROR, resultStatus.getMessage());
                            } else {
                                NotificationHelper.showNotification((NotificationContext) getActivity(), NotificationType.ERROR, R.string.notification_save_task_failed);
                            }

                            this.button.setEnabled(true);
                            updateButtonState();
                        }
                        private ITaskResultCallback init(View button){
                            this.button = button;
                            return this;
                        }
                    }.init(v));
                } catch (Exception ex) {
                    v.setEnabled(true);
                    //getActivityCommunicator().hidePreloader();
                    //getActivityCommunicator().showFragmentView();
                }
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

                CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(getContext(),
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

                ContactFormNavigationCapsule dataCapsule = new ContactFormNavigationCapsule(getContext(),
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

                EventFormNavigationCapsule dataCapsule = new EventFormNavigationCapsule(getContext(),
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

    @Override
    public boolean showSaveAction() {
        return false;
    }

    @Override
    public boolean showAddAction() {
        return false;
    }

    private void updateButtonState() {
        int btnDoneVisibleStatus = (record.getTaskStatus() == TaskStatus.PENDING)? View.VISIBLE : View.GONE;
        int btnNotExecutableStatus = (record.getTaskStatus() == TaskStatus.PENDING)? View.VISIBLE : View.GONE;

        getContentBinding().btnDone.setVisibility(btnDoneVisibleStatus);
        getContentBinding().btnNotExecutable.setVisibility(btnNotExecutableStatus);

        if (btnDoneVisibleStatus == View.GONE && btnNotExecutableStatus == View.GONE) {
            getContentBinding().taskButtonPanel.setVisibility(View.GONE);
        } else if (btnDoneVisibleStatus == View.GONE || btnNotExecutableStatus == View.GONE) {
            getContentBinding().btnDivider.setVisibility(View.GONE);
        }
    }

    public static TaskEditFragment newInstance(IActivityCommunicator activityCommunicator, TaskFormNavigationCapsule capsule, Task activityRootData) {
        return newInstance(activityCommunicator, TaskEditFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (doneCallbackTask != null && !doneCallbackTask.isCancelled())
            doneCallbackTask.cancel(true);

        if (notExecCallbackTask != null && !notExecCallbackTask.isCancelled())
            notExecCallbackTask.cancel(true);

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }
}
