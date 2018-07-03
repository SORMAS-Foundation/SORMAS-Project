package de.symeda.sormas.app.component.dialog;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;

import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.DialogLocationLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.LocationService;

/**
 * Created by Orson on 02/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class LocationDialog extends BaseTeboAlertDialog {

    public static final String TAG = LocationDialog.class.getSimpleName();

    private Location data;
    private FragmentActivity activity;
    private DialogLocationLayoutBinding mContentBinding;

    private RegionLoader regionLoader;
    private DistrictLoader districtLoader;
    private CommunityLoader communityLoader;

    private IEntryItemOnClickListener pickGpsCallback;

    public LocationDialog(final FragmentActivity activity, Location location) {
        this(activity, R.string.heading_location_dialog, -1, null, null, null, location);
    }


    public LocationDialog(final FragmentActivity activity, RegionLoader regionLoader,
                          DistrictLoader districtLoader, CommunityLoader communityLoader, Location location) {
        this(activity, R.string.heading_location_dialog, -1, regionLoader, districtLoader, communityLoader, location);
    }

    public LocationDialog(final FragmentActivity activity, int headingResId, int subHeadingResId,
                          RegionLoader regionLoader, DistrictLoader districtLoader,
                          CommunityLoader communityLoader, Location location) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_location_layout,
                R.layout.dialog_root_two_button_panel_layout, headingResId, subHeadingResId);


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
    protected void initializeData(TaskResultHolder resultHolder, boolean executionComplete) {
        if (!executionComplete) {
            resultHolder.forOther().add(regionLoader == null? RegionLoader.getInstance() : regionLoader);
            resultHolder.forOther().add(districtLoader == null? DistrictLoader.getInstance() : districtLoader);
            resultHolder.forOther().add(communityLoader == null? CommunityLoader.getInstance() : communityLoader);
        } else {
            ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

            if (otherIterator.hasNext())
                this.regionLoader = otherIterator.next();

            if (otherIterator.hasNext())
                this.districtLoader = otherIterator.next();

            if (otherIterator.hasNext())
                this.communityLoader = otherIterator.next();
        }
    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding contentBinding, ViewDataBinding buttonPanelBinding) {
        mContentBinding = (DialogLocationLayoutBinding)contentBinding;

        updateGpsTextView();

        if (mContentBinding.spnState != null) {
            mContentBinding.spnState.initializeSpinner(RegionLoader.getInstance().load(), null, new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {
                    Region selectedValue = (Region) field.getValue();
                    if (selectedValue != null) {
                        mContentBinding.spnLga.setSpinnerData(DataUtils.toItems(DatabaseHelper.getDistrictDao().getByRegion(selectedValue)), mContentBinding.spnLga.getValue());
                    } else {
                        mContentBinding.spnLga.setSpinnerData(null);
                    }
                }
            });
        }

        if (mContentBinding.spnLga != null) {
            mContentBinding.spnLga.initializeSpinner(DistrictLoader.getInstance().load((Region) mContentBinding.spnState.getValue()), null, new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {
                    District selectedValue = (District) field.getValue();
                    if (selectedValue != null) {
                        mContentBinding.spnWard.setSpinnerData(DataUtils.toItems(DatabaseHelper.getCommunityDao().getByDistrict(selectedValue)), mContentBinding.spnWard.getValue());
                       } else {
                        mContentBinding.spnWard.setSpinnerData(null);
                    }
                }
            });
        }

        if (mContentBinding.spnWard != null) {
            mContentBinding.spnWard.initializeSpinner(CommunityLoader.getInstance().load((District) mContentBinding.spnLga.getValue()));
        }
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

        mContentBinding.txtGpsLatLon.setValue(data.getGpsLocation());
    }

    private void setupCallbacks() {
        pickGpsCallback = new IEntryItemOnClickListener() {

            @Override
            public void onClick(View v, Object item) {
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
        };
    }

}
