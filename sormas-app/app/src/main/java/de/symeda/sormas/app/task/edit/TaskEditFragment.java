package de.symeda.sormas.app.task.edit;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.api.task.TaskHelper;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditFragment;
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
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
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

public class TaskEditFragment extends BaseEditFragment<FragmentTaskEditLayoutBinding, Task, Task> {

    private Task record;

    private View.OnClickListener doneCallback;
    private View.OnClickListener notExecCallback;
    private OnLinkClickListener caseLinkCallback;
    private OnLinkClickListener contactLinkCallback;
    private OnLinkClickListener eventLinkCallback;

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
    public boolean isShowSaveAction() {
        // see updateButtonState
        return false;
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
    }

    @Override
    public void onLayoutBinding(FragmentTaskEditLayoutBinding contentBinding) {

        setupCallback();

        updateButtonState();

        if (!record.getAssigneeUser().equals(ConfigProvider.getUser())) {
            contentBinding.taskAssigneeReply.setVisibility(View.GONE);
            contentBinding.setDone.setVisibility(View.GONE);
            contentBinding.setNotExecutable.setVisibility(View.GONE);
        }

        if (record.getCreatorComment() == null || record.getCreatorComment().isEmpty()) {
            contentBinding.taskCreatorComment.setVisibility(View.GONE);
        }

        contentBinding.setData(record);
        contentBinding.setDoneCallback(doneCallback);
        contentBinding.setNotExecCallback(notExecCallback);
        contentBinding.setCaseLinkCallback(caseLinkCallback);
        contentBinding.setContactLinkCallback(contactLinkCallback);
        contentBinding.setEventLinkCallback(eventLinkCallback);
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_task_edit_layout;
    }

    private void setupCallback() {

        doneCallback = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record.setTaskStatus(TaskStatus.DONE);
                getBaseEditActivity().saveData();
            }
        };

        notExecCallback = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                record.setTaskStatus(TaskStatus.NOT_EXECUTABLE);
                getBaseEditActivity().saveData();
            }
        };

        caseLinkCallback = new OnLinkClickListener() {
            @Override
            public void onClick(View v, Object item) {
                if (item == null)
                    return;

                Task task = (Task) item;
                Case caze = task.getCaze();

                if (caze == null)
                    return;

                CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(getContext(),
                        caze.getUuid(), caze.getCaseClassification()).setTaskUuid(task.getUuid());
                CaseEditActivity.goToActivity(getActivity(), dataCapsule);
            }
        };

        contactLinkCallback = new OnLinkClickListener() {
            @Override
            public void onClick(View v, Object item) {
                if (item == null)
                    return;

                Task task = (Task) item;
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

                Task task = (Task) item;
                Event event = task.getEvent();

                if (event == null)
                    return;

                EventFormNavigationCapsule dataCapsule = new EventFormNavigationCapsule(getContext(),
                        event.getUuid(), event.getEventStatus()).setTaskUuid(task.getUuid());
                EventEditActivity.goToActivity(getActivity(), dataCapsule);
            }
        };
    }

    private void updateButtonState() {
        int setDoneVisibleStatus = (record.getTaskStatus() == TaskStatus.PENDING) ? View.VISIBLE : View.GONE;
        int setNotExecutableStatus = (record.getTaskStatus() == TaskStatus.PENDING) ? View.VISIBLE : View.GONE;

        getContentBinding().setDone.setVisibility(setDoneVisibleStatus);
        getContentBinding().setNotExecutable.setVisibility(setNotExecutableStatus);

        if (setDoneVisibleStatus == View.GONE && setNotExecutableStatus == View.GONE) {
            getContentBinding().taskButtonPanel.setVisibility(View.GONE);
        } else if (setDoneVisibleStatus == View.GONE || setNotExecutableStatus == View.GONE) {
            getContentBinding().btnDivider.setVisibility(View.GONE);
        }

        // it's possible to edit and save the assignee reply
        boolean isSaveAllowed = ConfigProvider.getUser().equals(record.getAssigneeUser())
                && (record.getTaskStatus() == TaskStatus.DONE || record.getTaskStatus() == TaskStatus.NOT_EXECUTABLE);
        getBaseEditActivity().getSaveMenu().setVisible(isSaveAllowed);
    }

    public static TaskEditFragment newInstance(TaskFormNavigationCapsule capsule, Task activityRootData) {
        return newInstance(TaskEditFragment.class, capsule, activityRootData);
    }
}
