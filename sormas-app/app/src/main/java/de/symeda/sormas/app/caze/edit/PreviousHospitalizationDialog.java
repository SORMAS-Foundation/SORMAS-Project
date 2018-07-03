package de.symeda.sormas.app.caze.edit;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;

import java.util.List;

import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.component.dialog.BaseTeboAlertDialog;
import de.symeda.sormas.app.component.dialog.CommunityLoader;
import de.symeda.sormas.app.component.dialog.DistrictLoader;
import de.symeda.sormas.app.component.dialog.FacilityLoader;
import de.symeda.sormas.app.component.dialog.RegionLoader;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.DialogPreviousHospitalizationLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

public class PreviousHospitalizationDialog extends BaseTeboAlertDialog {

    public static final String TAG = PreviousHospitalizationDialog.class.getSimpleName();

    private PreviousHospitalization data;
    private DialogPreviousHospitalizationLayoutBinding mContentBinding;


    public PreviousHospitalizationDialog(final FragmentActivity activity, PreviousHospitalization previousHospitalization) {
        this(activity, R.string.heading_case_hos_prev_hospitalization, -1, previousHospitalization);
    }

    public PreviousHospitalizationDialog(final FragmentActivity activity, int headingResId, int subHeadingResId,
                                         PreviousHospitalization previousHospitalization) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_previous_hospitalization_layout,
                R.layout.dialog_root_three_button_panel_layout, headingResId, subHeadingResId);

        this.data = previousHospitalization;
    }

    @Override
    protected void onOkClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
        /*DialogPreviousHospitalizationLayoutBinding _contentBinding = (DialogPreviousHospitalizationLayoutBinding)contentBinding;

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
        this.mContentBinding = (DialogPreviousHospitalizationLayoutBinding)binding;
    }

    @Override
    protected void setBindingVariable(Context context, ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, data)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }

        if (!binding.setVariable(BR.yesNoUnknownClass, YesNoUnknown.class)) {
            Log.e(TAG, "There is no variable 'yesNoUnknownClass' in layout " + layoutName);
        }
    }

    @Override
    protected void initializeData(TaskResultHolder resultHolder, boolean executionComplete) {

    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding contentBinding, ViewDataBinding buttonPanelBinding) {
        //DialogPreviousHospitalizationLayoutBinding _contentBinding = (DialogPreviousHospitalizationLayoutBinding)contentBinding;

        mContentBinding.dtpDateOfAdmission.setFragmentManager(getFragmentManager());
        mContentBinding.dtpDateOfDischarge.setFragmentManager(getFragmentManager());

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
                        mContentBinding.spnFacility.setSpinnerData(DataUtils.toItems(DatabaseHelper.getFacilityDao().getHealthFacilitiesByDistrict(selectedValue, true)), mContentBinding.spnFacility.getValue());
                    } else {
                        mContentBinding.spnWard.setSpinnerData(null);
                        mContentBinding.spnFacility.setSpinnerData(null);
                    }
                }
            });
        }

        if (mContentBinding.spnWard != null) {
            mContentBinding.spnWard.initializeSpinner(CommunityLoader.getInstance().load((District) mContentBinding.spnLga.getValue()), null, new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {
                    Community selectedValue = (Community) field.getValue();
                    if (selectedValue != null) {
                        mContentBinding.spnFacility.setSpinnerData(DataUtils.toItems(DatabaseHelper.getFacilityDao().getHealthFacilitiesByCommunity(selectedValue, true)));
                    } else if (mContentBinding.spnLga.getValue() != null) {
                        mContentBinding.spnFacility.setSpinnerData(DataUtils.toItems(DatabaseHelper.getFacilityDao().getHealthFacilitiesByDistrict((District) mContentBinding.spnLga.getValue(), true)));
                    } else {
                        mContentBinding.spnFacility.setSpinnerData(null);
                    }
                }
            });
        }

        List<Item> facilities = mContentBinding.spnWard.getValue() != null ? FacilityLoader.getInstance().load((Community) mContentBinding.spnWard.getValue(), true)
                : FacilityLoader.getInstance().load((District) mContentBinding.spnLga.getValue(), true);
        if (mContentBinding.spnFacility != null) {
            mContentBinding.spnFacility.initializeSpinner(facilities, null, new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {

                }
            });
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

    /*public PreviousHospitalizationDialog(final FragmentActivity activity, List<Region> regionList,
                                         List<District> districtList, List<Community> communityList,
                                         List<Facility> facilityList, Case caze) {
        this(activity, R.string.heading_case_hos_prev_hospitalization, -1, regionList, districtList,
                communityList, facilityList, caze);
    }

    public PreviousHospitalizationDialog(final FragmentActivity activity, int headingResId, int subHeadingResId,
                                         List<Region> regionList, List<District> districtList, List<Community> communityList,
                                         List<Facility> facilityList, Case caze) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_previous_hospitalization_layout,
                R.layout.dialog_root_three_button_panel_layout, headingResId, subHeadingResId);

        this.data = caze;
        this.regionList = regionList;
        this.districtList = districtList;
        this.communityList = communityList;
        this.facilityList = facilityList;
    }*/

}