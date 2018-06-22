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
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.DialogErrorLayoutBinding;

/**
 * Created by Orson on 03/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class ErrorDialog extends BaseTeboAlertDialog {

    public static final String TAG = ErrorDialog.class.getSimpleName();

    private String uuid;
    private String viewName;
    private Tracker tracker;
    private DialogViewConfig data;
    private String subHeading;
    private DialogErrorLayoutBinding mContentBinding;

    public ErrorDialog(final FragmentActivity activity, String viewName, String uuid) {
        this(activity, R.string.heading_error_dialog, R.string.heading_sub_error_notification_dialog, viewName, uuid);
    }

    public ErrorDialog(final FragmentActivity activity, int headingResId, int subHeadingResId, String viewName, String uuid) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_error_layout,
                R.layout.dialog_root_one_button_panel_layout, headingResId, subHeadingResId);

        this.uuid = uuid;
        this.viewName = viewName;
        this.tracker = ((SormasApplication) activity.getApplication()).getDefaultTracker();

        this.data = null;
    }

    @Override
    protected void onOkClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
        /*TeboTextInputEditText txtMessage = (TeboTextInputEditText)rootView.findViewById(R.id.txtMessage);
        txtMessage.enableErrorState("Hello");*/
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
        if (callback != null)
            callback.call(null);
    }

    @Override
    protected void onDismissClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
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
        this.mContentBinding = (DialogErrorLayoutBinding)binding;
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
    public boolean isHeadingCentered() {
        return true;
    }

    @Override
    public boolean isRounded() {
        return true;
    }

    @Override
    public float getWidth() {
        return getContext().getResources().getDimension(R.dimen.notificationDialogWidth);
    }

    @Override
    public ControlButtonType dismissButtonType() {
        return ControlButtonType.LINE_DANGER;
    }
}
