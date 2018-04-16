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
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.TeboButtonType;
import de.symeda.sormas.app.component.TeboSpinner;
import de.symeda.sormas.app.component.VisualState;
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

    private Double latitude;
    private Double longitude;
    private Float latLonAccuracy;

    private IRegionLoader regionLoader;
    private IDistrictLoader districtLoader;
    private ICommunityLoader communityLoader;

    private List<Region> regionList;
    private List<District> districtList;
    private List<Community> communityList;

    private IEntryItemOnClickListener pickGpsCallback;

    public LocationDialog(final FragmentActivity activity, Location location) {
        this(activity, R.string.heading_location_dialog, -1, null, null, null, location);
    }


    public LocationDialog(final FragmentActivity activity, IRegionLoader regionLoader,
                          IDistrictLoader districtLoader, ICommunityLoader communityLoader, Location location) {
        this(activity, R.string.heading_location_dialog, -1, regionLoader, districtLoader, communityLoader, location);
    }

    public LocationDialog(final FragmentActivity activity, int headingResId, int subHeadingResId,
                          IRegionLoader regionLoader, IDistrictLoader districtLoader,
                          ICommunityLoader communityLoader, Location location) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_location_layout,
                R.layout.dialog_root_two_button_panel_layout, headingResId, subHeadingResId);


        this.activity = activity;
        this.data = location;

        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.latLonAccuracy = location.getLatLonAccuracy();

        setupCallbacks();
        updateGpsTextView();
    }

    @Override
    protected void onOkClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
        DialogLocationLayoutBinding _contentBinding = (DialogLocationLayoutBinding)contentBinding;

        data.setLatitude(latitude);
        data.setLongitude(longitude);
        data.setLatLonAccuracy(latLonAccuracy);

        callback.call(null);
    }

    @Override
    protected void onDismissClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
        callback.call(null);
    }

    @Override
    protected void onDeleteClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
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

        mContentBinding.spnState.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return data.getRegion();
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                /*Community comm = DatabaseHelper.getCommunityDao().queryForAll().get(0);
                return DataUtils.toItems(DatabaseHelper.getFacilityDao()
                        .getHealthFacilitiesByCommunity(comm, false));*/


                //return DataUtils.toItems(communityList);

                List<Item> regions = regionLoader.load();


                return (regions.size() > 0) ? DataUtils.addEmptyItem(regions) : regions;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }
        });

        mContentBinding.spnLga.initialize(mContentBinding.spnState, new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return data.getDistrict();
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                List<Item> districts = districtLoader.load((Region)parentValue);

                return (districts.size() > 0) ? DataUtils.addEmptyItem(districts) : districts;

            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }
        });

        mContentBinding.spnWard.initialize(mContentBinding.spnLga, new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return data.getCommunity();
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                List<Item> communities = communityLoader.load((District)parentValue);

                return (communities.size() > 0) ? DataUtils.addEmptyItem(communities) : communities;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }
        });
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
    public TeboButtonType dismissButtonType() {
        return TeboButtonType.BTN_LINE_DANGER;
    }

    @Override
    public TeboButtonType okButtonType() {
        return TeboButtonType.BTN_LINE_PRIMARY;
    }

    private void updateGpsTextView() {
        if (mContentBinding == null)
            return;

        if (latitude == null || longitude == null) {
            mContentBinding.txtGpsLatLon.setValue(""); //getContext().getString(R.string.label_pick_gps)
        }
        else {
            if (latLonAccuracy != null) {
                mContentBinding.txtGpsLatLon.setValue(android.location.Location.convert(latitude, android.location.Location.FORMAT_DEGREES)
                        + ", " + android.location.Location.convert(longitude, android.location.Location.FORMAT_DEGREES)
                        + " +-" + Math.round(latLonAccuracy) + "m");
            } else {
                mContentBinding.txtGpsLatLon.setValue(android.location.Location.convert(latitude, android.location.Location.FORMAT_DEGREES)
                        + ", " + android.location.Location.convert(longitude, android.location.Location.FORMAT_DEGREES));
            }
        }
    }

    private void setupCallbacks() {
        pickGpsCallback = new IEntryItemOnClickListener() {

            @Override
            public void onClick(View v, Object item) {
                android.location.Location phoneLocation = LocationService.instance().getLocation(activity);
                if (phoneLocation != null) {
                    latitude = phoneLocation.getLatitude();
                    longitude = phoneLocation.getLongitude();
                    latLonAccuracy = phoneLocation.getAccuracy();
                } else {
                    latitude = null;
                    longitude = null;
                    latLonAccuracy = null;
                }
                updateGpsTextView();
            }
        };
    }

}
