/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.component.dialog;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.FirebaseEvent;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.DialogUserReportLayoutBinding;
import de.symeda.sormas.app.util.Callback;

public class UserReportDialog extends AbstractDialog {

    public static final String TAG = UserReportDialog.class.getSimpleName();

    private AsyncTask dialogTask;
    private String uuid;
    private String viewName;
    private DialogUserReportLayoutBinding contentBinding;

    // Constructor

    public UserReportDialog(final FragmentActivity activity, String viewName, String uuid) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_user_report_layout,
                R.layout.dialog_root_two_button_panel_layout, R.string.heading_user_report,
                R.string.info_user_report);

        this.uuid = uuid;
        this.viewName = viewName;
    }

    // Overrides

    @Override
    protected void setContentBinding(Context context, ViewDataBinding binding, String layoutName) {
        this.contentBinding = (DialogUserReportLayoutBinding) binding;
    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding buttonPanelBinding) {
        setPositiveCallback(new Callback() {
            @Override
            public void call() {
                DefaultAsyncTask executor = new DefaultAsyncTask(getContext()) {
                    @Override
                    public void doInBackground(TaskResultHolder resultHolder) {
                        try {
                            String description = contentBinding.userReportMessage.getValue();

                            Bundle eventBundle = new Bundle();
                            eventBundle.putString(FirebaseAnalytics.Param.CONTENT, description);
                            ((BaseActivity) getActivity()).getFirebaseAnalytics().logEvent(FirebaseEvent.USER_ERROR_REPORT, eventBundle);

                            resultHolder.setResultStatus(new BoolResult(true,  getActivity().getResources().getString(R.string.message_report_sent)));
                        } catch (Exception ex) {
                            resultHolder.setResultStatus(new BoolResult(true, getActivity().getResources().getString(R.string.message_report_not_sent)));
                        }
                    }

                    @Override
                    public void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                        BoolResult resultStatus = taskResult.getResultStatus();
                        if (taskResult.getResultStatus().isSuccess()) {
                            NotificationHelper.showDialogNotification(UserReportDialog.this, NotificationType.SUCCESS, resultStatus.getMessage());
                        } else {
                            NotificationHelper.showDialogNotification(UserReportDialog.this, NotificationType.ERROR, resultStatus.getMessage());
                        }
                    }
                };

                dialogTask = executor.executeOnThreadPool();
            }
        });

        setNegativeCallback(new Callback() {
            @Override
            public void call() {
                if (dialogTask != null && !dialogTask.isCancelled()) {
                    dialogTask.cancel(true);
                }

                dismiss();
            }
        });
    }

    @Override
    public int getPositiveButtonText() {
        return R.string.action_send;
    }

}
