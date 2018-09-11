package de.symeda.sormas.app.component.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.app.BR;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.databinding.DialogConfirmationLayoutBinding;

public class ConfirmationDialog extends AbstractDialog {

    public static final String TAG = ConfirmationDialog.class.getSimpleName();

    // Constructors

    public ConfirmationDialog(final FragmentActivity activity, int headingResId, int subHeadingResId) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_confirmation_layout,
                R.layout.dialog_root_two_button_panel_edge_aligned_layout, headingResId, subHeadingResId);
    }

    public ConfirmationDialog(final FragmentActivity activity, int headingResId, int subHeadingResId,
                       int positiveButtonTextResId, int negativeButtonTextResId) {
        this(activity, headingResId, subHeadingResId);

        Resources resources = getContext().getResources();
        if (positiveButtonTextResId >= 0) {
            getConfig().setPositiveButtonText(resources.getString(positiveButtonTextResId));
        } else {
            getConfig().setPositiveButtonText(resources.getString(R.string.action_confirm));
        }
        if (negativeButtonTextResId >= 0) {
            getConfig().setNegativeButtonText(resources.getString(negativeButtonTextResId));
        } else {
            getConfig().setNegativeButtonText(resources.getString(R.string.action_dismiss));
        }
    }

    // Overrides

    @Override
    protected void setContentBinding(Context context, ViewDataBinding binding, String layoutName) {
        // Data variable is not needed in this dialog
    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding buttonPanelBinding) {
        // Nothing to initialize
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
    public ControlButtonType getNegativeButtonType() {
        return ControlButtonType.LINE_DANGER;
    }

    @Override
    public boolean isNegativeButtonIconOnly() {
        return true;
    }

}
