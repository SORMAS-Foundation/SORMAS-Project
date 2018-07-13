package de.symeda.sormas.app.caze.edit;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;

import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.epidata.EpiDataBurial;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.dialog.BaseTeboAlertDialog;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.DialogCaseEpidBurialEditLayoutBinding;

public class EpiDataBurialDialog extends BaseTeboAlertDialog {

    public static final String TAG = EpiDataBurialDialog.class.getSimpleName();

    private EpiDataBurial data;
    private DialogCaseEpidBurialEditLayoutBinding mContentBinding;


    public EpiDataBurialDialog(final FragmentActivity activity, EpiDataBurial epiDataBurial) {
        this(activity, R.string.heading_sub_case_epid_burial_visited, -1, epiDataBurial);
    }

    public EpiDataBurialDialog(final FragmentActivity activity, int headingResId, int subHeadingResId, EpiDataBurial epiDataBurial) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_case_epid_burial_edit_layout,
                R.layout.dialog_root_three_button_panel_layout, headingResId, subHeadingResId);

        this.data = epiDataBurial;
    }

    @Override
    protected void onOkClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
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
        this.mContentBinding = (DialogCaseEpidBurialEditLayoutBinding) binding;
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

        mContentBinding.epiDataBurialBurialDateFrom.initializeDateField(getFragmentManager());
        mContentBinding.epiDataBurialBurialDateTo.initializeDateField(getFragmentManager());

        setUpControlListeners();
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
    public boolean isDeleteButtonVisible() {
        return true;
    }

    @Override
    public boolean isRounded() {
        return true;
    }

    @Override
    public ControlButtonType dismissButtonType() {
        return ControlButtonType.LINE_SECONDARY;
    }

    @Override
    public ControlButtonType okButtonType() {
        return ControlButtonType.LINE_PRIMARY;
    }

    @Override
    public ControlButtonType deleteButtonType() {
        return ControlButtonType.LINE_DANGER;
    }

    private void setUpControlListeners() {
        mContentBinding.epiDataBurialBurialAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddressPopup();
            }
        });
    }

    private void openAddressPopup() {
        final Location location = (Location)mContentBinding.epiDataBurialBurialAddress.getValue();
        final LocationDialog locationDialog = new LocationDialog(BaseActivity.getActiveActivity(), location);
        locationDialog.show(null);

        locationDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
            @Override
            public void onOkClick(View v, Object item, View viewRoot) {
                mContentBinding.epiDataBurialBurialAddress.setValue(location);
                locationDialog.dismiss();
            }
        });
    }
}
