package de.symeda.sormas.app.component.dialog;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.DialogUserReportLayoutBinding;
import de.symeda.sormas.app.util.TimeoutHelper;

/**
 * Created by Orson on 01/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class UserReportDialog extends BaseTeboAlertDialog {

    public static final String TAG = UserReportDialog.class.getSimpleName();

    private AsyncTask dialogTask;
    private String uuid;
    private String viewName;
    private Tracker tracker;
    private UserReport data;
    private DialogUserReportLayoutBinding mContentBinding;

    public UserReportDialog(final FragmentActivity activity, String viewName, String uuid) {
        this(activity, R.string.headline_user_report, R.string.hint_user_report, viewName, uuid);
    }

    public UserReportDialog(final FragmentActivity activity, int headingResId, int subHeadingResId, String viewName, String uuid) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_user_report_layout,
                R.layout.dialog_root_two_button_panel_layout, headingResId, subHeadingResId);

        this.uuid = uuid;
        this.viewName = viewName;
        this.tracker = ((SormasApplication) activity.getApplication()).getDefaultTracker();

        this.data = new UserReport(viewName, uuid);
    }

    @Override
    protected void onOkClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, final Callback.IAction callback) {
        try {
            DefaultAsyncTask executor = new DefaultAsyncTask(getContext()) {
                @Override
                public void onPreExecute() {
                    //getActivityCommunicator().showPreloader();
                    //getActivityCommunicator().hideFragmentView();
                }

                @Override
                public void doInBackground(TaskResultHolder resultHolder) {
                    try {
                        String description = UserReportDialog.this.data.getMessage();
                        Tracker tracker = ((SormasApplication) getActivity().getApplication()).getDefaultTracker();
                        tracker.send(new HitBuilders.EventBuilder()
                                .setCategory("User Report")
                                .setAction("Error Report")
                                .setLabel("Location: " + viewName + (uuid!=null?" - UUID: " + uuid:"") + (ConfigProvider.getUser()!=null?" - User: " + ConfigProvider.getUser().getUuid():"") + " - Description: " + description)
                                .build());

                        resultHolder.setResultStatus(new BoolResult(true, getActivity().getResources().getString(R.string.snackbar_report_sent)));
                    } catch (Exception ex) {
                        resultHolder.setResultStatus(new BoolResult(true, getActivity().getResources().getString(R.string.snackbar_report_not_sent)));
                    }
                }
            };
            dialogTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().hidePreloader();
                    //getActivityCommunicator().showFragmentView();

                    if (resultHolder == null){if (callback != null)
                        if (callback != null)
                            callback.call(null);

                        return;
                    }

                    if (resultStatus.isSuccess()) {
                        NotificationHelper.showDialogNotification(UserReportDialog.this, NotificationType.SUCCESS, resultStatus.getMessage());
                    } else {
                        NotificationHelper.showDialogNotification(UserReportDialog.this, NotificationType.ERROR, resultStatus.getMessage());
                    }

                    TimeoutHelper.executeIn5Seconds(new Callback.IAction<AsyncTask>() {
                        @Override
                        public void call(AsyncTask result) {
                            if (callback != null)
                                callback.call(null);
                        }
                    });
                }
            });
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }
    }

    @Override
    protected void onDismissClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
        if (dialogTask != null && !dialogTask.isCancelled())
            dialogTask.cancel(true);

        if (callback != null)
            callback.call(null);
    }

    @Override
    protected void onDeleteClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
        if (callback != null)
            callback.call(null);
    }

    @Override
    protected void recieveViewDataBinding(Context context, ViewDataBinding binding) {
        this.mContentBinding = (DialogUserReportLayoutBinding)binding;
    }

    @Override
    protected void setBindingVariable(Context context, ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, data)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }
    }

    @Override
    protected void initializeData(TaskResultHolder resultHolder, boolean executionComplete) {

    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding contentBinding, ViewDataBinding buttonPanelBinding) {

    }

    @Override
    public boolean isOkButtonVisible() {
        return true;
    }

    @Override
    public boolean isDismissButtonVisible() {
        return true;
    }

    @Override
    public int getPositiveButtonText() {
        return R.string.action_send;
    }
}
