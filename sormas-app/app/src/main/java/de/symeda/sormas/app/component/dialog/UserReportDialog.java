package de.symeda.sormas.app.component.dialog;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;
import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.component.TeboTextInputEditText;
import de.symeda.sormas.app.core.ICallback;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.DialogUserReportLayoutBinding;

/**
 * Created by Orson on 01/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class UserReportDialog extends BaseTeboAlertDialog {

    public static final String TAG = UserReportDialog.class.getSimpleName();

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
    protected void onOkClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, ICallback callback) {
        TeboTextInputEditText txtMessage = (TeboTextInputEditText)rootView.findViewById(R.id.txtMessage);
        txtMessage.enableErrorState((INotificationContext) getActivity(), "Hello");
        /*String description = this.data.getHeading();
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("User Report")
                .setAction("Error Report")
                .setText("Location: " + viewName + (uuid!=null?" - UUID: " + uuid:"") +
                        (ConfigProvider.getUser()!=null?" - User: " +
                                ConfigProvider.getUser().getUuid():"") +
                        " - Description: " + description)
                .build());
        Snackbar.make(activity.findViewById(R.id.base_layout),
                activity.getString(R.string.snackbar_report_sent), Snackbar.LENGTH_LONG).show();*/

    }

    @Override
    protected void onDismissClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, ICallback callback) {

    }

    @Override
    protected void onDeleteClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, ICallback callback) {

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
