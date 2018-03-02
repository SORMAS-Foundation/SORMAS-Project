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

/**
 * Created by Orson on 02/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TeboProgressDialog extends BaseTeboAlertDialog {

    public static final String TAG = TeboProgressDialog.class.getSimpleName();

    /*private String uuid;
    private String viewName;*/
    private Tracker tracker;
    private DialogViewConfig data;
    private String subHeading;

    public TeboProgressDialog(final FragmentActivity activity) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_progress_layout, -1, -1, -1);

        /*this.uuid = uuid;
        this.viewName = viewName;*/
        this.tracker = ((SormasApplication) activity.getApplication()).getDefaultTracker();

        this.data = null;
    }

    @Override
    protected void onOkClicked(View v, Object item, View rootView, ViewDataBinding contentBinding) {
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

    }

    @Override
    protected void onDismissClicked(View v, Object item, View rootView, ViewDataBinding contentBinding) {

    }

    @Override
    protected void onDeleteClicked(View v, Object item, View rootView, ViewDataBinding contentBinding) {

    }

    @Override
    protected void setBindingVariable(Context context, ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, data)) {
            Log.w(TAG, "There is no variable 'data' in layout " + layoutName);
        }
    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding contentBinding, ViewDataBinding buttonPanelBinding) {

    }

    @Override
    public boolean isOkButtonVisible() {
        return false;
    }

    @Override
    public boolean isDismissButtonVisible() {
        return false;
    }

    @Override
    public boolean isDeleteButtonVisible() {
        return false;
    }

    @Override
    public boolean isHeadingVisible() {
        return false;
    }

    @Override
    public float getWidth() {
        return getContext().getResources().getDimension(R.dimen.progressDialogWidth);
    }
}