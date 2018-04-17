package de.symeda.sormas.app.component.dialog;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.app.BR;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.component.TeboButtonType;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.DialogConfirmationLayoutBinding;

/**
 * Created by Orson on 18/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class ConfirmationDialog extends de.symeda.sormas.app.component.dialog.BaseTeboAlertDialog {

    public static final String TAG = ConfirmationDialog.class.getSimpleName();

    private String uuid;
    private String viewName;
    private Tracker tracker;
    private de.symeda.sormas.app.component.dialog.DialogViewConfig data;
    private String subHeading;
    private DialogConfirmationLayoutBinding mContentBinding;

    public ConfirmationDialog(final FragmentActivity activity) {
        this(activity, R.string.heading_confirmation_dialog, R.string.heading_sub_confirmation_notification_dialog);
    }

    public ConfirmationDialog(final FragmentActivity activity, int headingResId, String subHeading) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_confirmation_layout,
                R.layout.dialog_root_two_button_panel_edge_aligned_layout, headingResId, subHeading);

        this.uuid = "";
        this.viewName = "";
        this.tracker = ((SormasApplication) activity.getApplication()).getDefaultTracker();

        this.data = null;
    }

    public ConfirmationDialog(final FragmentActivity activity, int headingResId, int subHeadingResId) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_confirmation_layout,
                R.layout.dialog_root_two_button_panel_edge_aligned_layout, headingResId, subHeadingResId);

        this.uuid = "";
        this.viewName = "";
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
        this.mContentBinding = (DialogConfirmationLayoutBinding)binding;
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
    public TeboButtonType dismissButtonType() {
        return TeboButtonType.BTN_LINE_DANGER;
    }

    @Override
    public boolean iconOnlyDismissButtons() {
        return true;
    }

    @Override
    public int getPositiveButtonText() {
        return R.string.action_confirm;
    }
}

