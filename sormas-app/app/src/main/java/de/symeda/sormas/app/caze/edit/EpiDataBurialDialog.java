package de.symeda.sormas.app.caze.edit;

import android.app.AlertDialog;
import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;

import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.epidata.EpiDataBurial;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.TeboButtonType;
import de.symeda.sormas.app.component.dialog.BaseTeboAlertDialog;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.DialogEpidBurialsLayoutBinding;

/**
 * Created by Orson on 19/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class EpiDataBurialDialog extends BaseTeboAlertDialog {

    public static final String TAG = EpiDataBurialDialog.class.getSimpleName();

    private EpiDataBurial data;
    private IEntryItemOnClickListener onAddressLinkClickedCallback;
    private DialogEpidBurialsLayoutBinding mContentBinding;


    public EpiDataBurialDialog(final FragmentActivity activity, EpiDataBurial epiDataBurial) {
        this(activity, R.string.heading_sub_case_epid_burial_visited, -1, epiDataBurial);
    }

    public EpiDataBurialDialog(final FragmentActivity activity, int headingResId, int subHeadingResId, EpiDataBurial epiDataBurial) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_epid_burials_layout,
                R.layout.dialog_root_three_button_panel_layout, headingResId, subHeadingResId);

        this.data = epiDataBurial;

        setupCallback();
    }

    @Override
    protected void onOkClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
        /*DialogEpiDataTravelLayoutBinding _contentBinding = (DialogEpiDataTravelLayoutBinding)contentBinding;

        _contentBinding.spnState.enableErrorState("Hello");*/
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
        this.mContentBinding = (DialogEpidBurialsLayoutBinding)binding;
    }

    @Override
    protected void setBindingVariable(Context context, ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, data)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }

        if (!binding.setVariable(BR.yesNoUnknownClass, YesNoUnknown.class)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }

        if (!binding.setVariable(BR.addressLinkCallback, onAddressLinkClickedCallback)) {
            Log.e(TAG, "There is no variable 'addressLinkCallback' in layout " + layoutName);
        }
    }

    @Override
    protected void initializeData(TaskResultHolder resultHolder, boolean executionComplete) {

    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding contentBinding, ViewDataBinding buttonPanelBinding) {
        //mContentBinding = (DialogEpidBurialsLayoutBinding)contentBinding;

        mContentBinding.dtpBurialFromDate.initialize(getFragmentManager());
        mContentBinding.dtpBurialToDate.initialize(getFragmentManager());
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
    public TeboButtonType dismissButtonType() {
        return TeboButtonType.BTN_LINE_SECONDARY;
    }

    @Override
    public TeboButtonType okButtonType() {
        return TeboButtonType.BTN_LINE_PRIMARY;
    }

    @Override
    public TeboButtonType deleteButtonType() {
        return TeboButtonType.BTN_LINE_DANGER;
    }

    private void setupCallback() {
        onAddressLinkClickedCallback = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final Location location = data.getBurialAddress();// MemoryDatabaseHelper.LOCATION.getLocations(1).get(0);
                final LocationDialog locationDialog = new LocationDialog(getActivity(), location);
                locationDialog.show(new Callback.IAction<AlertDialog>() {
                    @Override
                    public void call(AlertDialog result) {

                    }
                });



                locationDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        mContentBinding.txtBurialAddress.setValue(location);
                        /*getContentBinding().txtAddress.setValue(location.toString());
                        locationDialog.dismiss();*/
                        //data.setBurialAddress(location);
                        locationDialog.dismiss();
                    }
                });
            }
        };
    }

}
