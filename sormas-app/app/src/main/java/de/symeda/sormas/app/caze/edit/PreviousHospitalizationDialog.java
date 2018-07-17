package de.symeda.sormas.app.caze.edit;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;

import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.dialog.BaseTeboAlertDialog;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.databinding.DialogPreviousHospitalizationLayoutBinding;
import de.symeda.sormas.app.util.InfrastructureHelper;

public class PreviousHospitalizationDialog extends BaseTeboAlertDialog {

    public static final String TAG = PreviousHospitalizationDialog.class.getSimpleName();

    private PreviousHospitalization data;
    private DialogPreviousHospitalizationLayoutBinding contentBinding;

    private List<Item> initialRegions;
    private List<Item> initialDistricts;
    private List<Item> initialCommunities;
    private List<Item> initialFacilities;

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
        this.contentBinding = (DialogPreviousHospitalizationLayoutBinding) binding;
    }

    @Override
    protected void setBindingVariable(Context context, ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, data)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }
    }

    @Override
    protected void prepareDialogData() {
        initialRegions = InfrastructureHelper.loadRegions();
        initialDistricts = InfrastructureHelper.loadDistricts(data.getRegion());
        initialCommunities = InfrastructureHelper.loadCommunities(data.getDistrict());
        initialFacilities = InfrastructureHelper.loadFacilities(data.getDistrict(), data.getCommunity());
    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding contentBinding, ViewDataBinding buttonPanelBinding) {
        //DialogPreviousHospitalizationLayoutBinding _contentBinding = (DialogPreviousHospitalizationLayoutBinding)contentBinding;

        this.contentBinding.casePreviousHospitalizationAdmissionDate.initializeDateField(getFragmentManager());
        this.contentBinding.casePreviousHospitalizationDischargeDate.initializeDateField(getFragmentManager());

        InfrastructureHelper.initializeHealthFacilityDetailsFieldVisibility(
                this.contentBinding.casePreviousHospitalizationHealthFacility, this.contentBinding.casePreviousHospitalizationHealthFacilityDetails);

        InfrastructureHelper.initializeFacilityFields(this.contentBinding.casePreviousHospitalizationRegion, initialRegions,
                this.contentBinding.casePreviousHospitalizationDistrict, initialDistricts,
                this.contentBinding.casePreviousHospitalizationCommunity, initialCommunities,
                this.contentBinding.casePreviousHospitalizationHealthFacility, initialFacilities);
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
}