package de.symeda.sormas.app.component.dialog;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;

import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.databinding.DialogLocationLayoutBinding;
import de.symeda.sormas.app.util.InfrastructureHelper;
import de.symeda.sormas.app.util.LocationService;

public class LocationDialog extends BaseTeboAlertDialog {

    public static final String TAG = LocationDialog.class.getSimpleName();

    private FragmentActivity activity;

    private Location data;
    private List<Item> initialRegions;
    private List<Item> initialDistricts;
    private List<Item> initialCommunities;

    private DialogLocationLayoutBinding mContentBinding;
    private IEntryItemOnClickListener pickGpsCallback;

    public LocationDialog(final FragmentActivity activity, Location location) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_location_layout,
                R.layout.dialog_root_two_button_panel_layout,R.string.heading_location_dialog, -1);

        this.activity = activity;
        this.data = location;

        setupCallbacks();
    }

    @Override
    protected void onOkClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
        DialogLocationLayoutBinding _contentBinding = (DialogLocationLayoutBinding)contentBinding;

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
        this.mContentBinding = (DialogLocationLayoutBinding)binding;
    }

    @Override
    protected void setBindingVariable(Context context, ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, data)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }

        if (!binding.setVariable(BR.pickGpsCallback, pickGpsCallback)) {
            Log.e(TAG, "There is no variable 'pickGpsCallback' in layout " + layoutName);
        }
    }

    @Override
    protected void prepareDialogData() {
        initialRegions = InfrastructureHelper.loadRegions();
        initialDistricts = InfrastructureHelper.loadDistricts(data.getRegion());
        initialCommunities = InfrastructureHelper.loadCommunities(data.getDistrict());
    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding contentBinding, ViewDataBinding buttonPanelBinding) {
        mContentBinding = (DialogLocationLayoutBinding)contentBinding;

        updateGpsTextView();

        InfrastructureHelper.initializeRegionFields(mContentBinding.locationRegion, initialRegions,
                mContentBinding.locationDistrict, initialDistricts,
                mContentBinding.locationCommunity, initialCommunities);
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
    public boolean isRounded() {
        return true;
    }

    @Override
    public ControlButtonType dismissButtonType() {
        return ControlButtonType.LINE_DANGER;
    }

    @Override
    public ControlButtonType okButtonType() {
        return ControlButtonType.LINE_PRIMARY;
    }

    private void updateGpsTextView() {
        if (mContentBinding == null)
            return;

        mContentBinding.locationLatLon.setValue(data.getGpsLocation());
    }

    private void setupCallbacks() {
        pickGpsCallback = new IEntryItemOnClickListener() {

            @Override
            public void onClick(View v, Object item) {

                final ConfirmationDialog confirmationDialog = new ConfirmationDialog(getActivity(),
                        R.string.heading_confirmation_dialog,
                        R.string.heading_sub_confirmation_notification_dialog_pick_gps);

                confirmationDialog.setOnPositiveClickListener(new de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object confirmationItem, View viewRoot) {
                        confirmationDialog.dismiss();

                        android.location.Location phoneLocation = LocationService.instance().getLocation(activity);
                        if (phoneLocation != null) {
                            data.setLatitude(phoneLocation.getLatitude());
                            data.setLongitude(phoneLocation.getLongitude());
                            data.setLatLonAccuracy(phoneLocation.getAccuracy());
                        } else {
                            data.setLatitude(null);
                            data.setLongitude(null);
                            data.setLatLonAccuracy(null);
                        }
                        updateGpsTextView();
                    }
                });

                confirmationDialog.show(null);
            }
        };
    }

}
