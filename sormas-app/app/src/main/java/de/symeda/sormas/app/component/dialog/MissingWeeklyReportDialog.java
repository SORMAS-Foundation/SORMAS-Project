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
import de.symeda.sormas.app.component.TeboButtonType;
import de.symeda.sormas.app.core.ICallback;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.DialogMissingWeeklyReportLayoutBinding;

/**
 * Created by Orson on 28/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class MissingWeeklyReportDialog extends BaseTeboAlertDialog {

    public static final String TAG = MissingWeeklyReportDialog.class.getSimpleName();

    private Tracker tracker;
    private de.symeda.sormas.app.component.dialog.DialogViewConfig data;
    private String subHeading;
    private DialogMissingWeeklyReportLayoutBinding mContentBinding;

    public MissingWeeklyReportDialog(final FragmentActivity activity) {
        this(activity, R.string.heading_missing_weekly_report_dialog, R.string.alert_missing_report);
    }

    public MissingWeeklyReportDialog(final FragmentActivity activity, int headingResId, String subHeading) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_missing_weekly_report_layout,
                R.layout.dialog_root_two_button_panel_edge_aligned_layout, headingResId, subHeading);


        this.tracker = ((SormasApplication) activity.getApplication()).getDefaultTracker();
        this.data = null;
    }

    public MissingWeeklyReportDialog(final FragmentActivity activity, int headingResId, int subHeadingResId) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_missing_weekly_report_layout,
                R.layout.dialog_root_two_button_panel_edge_aligned_layout, headingResId, subHeadingResId);

        this.tracker = ((SormasApplication) activity.getApplication()).getDefaultTracker();
        this.data = null;
    }

    @Override
    protected void onOkClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, ICallback callback) {
        callback.call(null);
    }

    @Override
    protected void onDismissClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, ICallback callback) {
        callback.call(null);
    }

    @Override
    protected void onDeleteClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, ICallback callback) {
        callback.call(null);
    }

    @Override
    protected void recieveViewDataBinding(Context context, ViewDataBinding binding) {
        this.mContentBinding = (DialogMissingWeeklyReportLayoutBinding)binding;
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
    public int getPositiveButtonText() {
        return R.string.action_open_reports;
    }

    @Override
    public boolean iconOnlyDismissButtons() {
        return true;
    }
}
