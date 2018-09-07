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
import de.symeda.sormas.app.databinding.DialogLocationLayoutBinding;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.InfrastructureHelper;
import de.symeda.sormas.app.util.LocationService;

public class LocationDialog extends AbstractDialog {

    public static final String TAG = LocationDialog.class.getSimpleName();

    private Location data;
    private DialogLocationLayoutBinding contentBinding;

    // Constructor

    public LocationDialog(final FragmentActivity activity, Location location) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_location_layout,
                R.layout.dialog_root_two_button_panel_layout, R.string.heading_location_dialog, -1);

        this.data = location;
    }

    // Instance methods

    private void updateGpsTextView() {
        if (contentBinding == null) {
            return;
        }

        contentBinding.locationLatLon.setValue(data.getGpsLocation());
    }

    // Overrides

    @Override
    protected void setContentBinding(Context context, ViewDataBinding binding, String layoutName) {
        this.contentBinding = (DialogLocationLayoutBinding) binding;

        if (!binding.setVariable(BR.data, data)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }
    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding buttonPanelBinding) {
        updateGpsTextView();

        List<Item> initialRegions = InfrastructureHelper.loadRegions();
        List<Item> initialDistricts = InfrastructureHelper.loadDistricts(data.getRegion());
        List<Item> initialCommunities = InfrastructureHelper.loadCommunities(data.getDistrict());
        InfrastructureHelper.initializeRegionFields(this.contentBinding.locationRegion, initialRegions,
                this.contentBinding.locationDistrict, initialDistricts,
                this.contentBinding.locationCommunity, initialCommunities);

        // "Pick GPS Coordinates" confirmation dialog
        this.contentBinding.pickGpsCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ConfirmationDialog confirmationDialog = new ConfirmationDialog(getActivity(),
                        R.string.heading_confirmation_dialog,
                        R.string.heading_sub_confirmation_notification_dialog_pick_gps,
                        R.string.yes,
                        R.string.no);

                confirmationDialog.setPositiveCallback(new Callback() {
                    @Override
                    public void call() {
                        confirmationDialog.dismiss();

                        android.location.Location phoneLocation = LocationService.instance().getLocation(getActivity());
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

                confirmationDialog.show();
            }
        });
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
    public ControlButtonType getPositiveButtonType() {
        return ControlButtonType.LINE_PRIMARY;
    }

}
