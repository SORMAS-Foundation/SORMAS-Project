package de.symeda.sormas.app.caze.edit;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;

import java.util.List;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.dialog.AbstractDialog;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.databinding.DialogPreviousHospitalizationLayoutBinding;
import de.symeda.sormas.app.util.InfrastructureHelper;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

public class PreviousHospitalizationDialog extends AbstractDialog {

    public static final String TAG = PreviousHospitalizationDialog.class.getSimpleName();

    private PreviousHospitalization data;

    PreviousHospitalizationDialog(final FragmentActivity activity, PreviousHospitalization previousHospitalization) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_previous_hospitalization_layout,
                R.layout.dialog_root_three_button_panel_layout, R.string.heading_case_hos_prev_hospitalization, -1);

        this.data = previousHospitalization;
    }

    @Override
    protected void setBindingVariable(Context context, ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, data)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }
    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, final ViewDataBinding contentBinding, ViewDataBinding buttonPanelBinding) {
        DialogPreviousHospitalizationLayoutBinding _contentBinding = (DialogPreviousHospitalizationLayoutBinding) contentBinding;

        _contentBinding.casePreviousHospitalizationAdmissionDate.initializeDateField(getFragmentManager());
        _contentBinding.casePreviousHospitalizationDischargeDate.initializeDateField(getFragmentManager());

        if (data.getId() == null) {
            setLiveValidationDisabled(true);
        }

        List<Item> initialRegions = InfrastructureHelper.loadRegions();
        List<Item> initialDistricts = InfrastructureHelper.loadDistricts(data.getRegion());
        List<Item> initialCommunities = InfrastructureHelper.loadCommunities(data.getDistrict());
        List<Item> initialFacilities = InfrastructureHelper.loadFacilities(data.getDistrict(), data.getCommunity());

        InfrastructureHelper.initializeHealthFacilityDetailsFieldVisibility(
                _contentBinding.casePreviousHospitalizationHealthFacility, _contentBinding.casePreviousHospitalizationHealthFacilityDetails);
        InfrastructureHelper.initializeFacilityFields(_contentBinding.casePreviousHospitalizationRegion, initialRegions,
                _contentBinding.casePreviousHospitalizationDistrict, initialDistricts,
                _contentBinding.casePreviousHospitalizationCommunity, initialCommunities,
                _contentBinding.casePreviousHospitalizationHealthFacility, initialFacilities);

        setPositiveCallback(new de.symeda.sormas.app.util.Callback() {
            @Override
            public void call() {
                setLiveValidationDisabled(false);

                try {
                    FragmentValidator.validate(getContext(), contentBinding);
                } catch (ValidationException e) {
                    NotificationHelper.showDialogNotification(PreviousHospitalizationDialog.this, ERROR, e.getMessage());
                }
            }
        });
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
    public ControlButtonType getNegativeButtonType() {
        return ControlButtonType.LINE_SECONDARY;
    }

    @Override
    public ControlButtonType getPositiveButtonType() {
        return ControlButtonType.LINE_PRIMARY;
    }

    @Override
    public ControlButtonType getDeleteButtonType() {
        return ControlButtonType.LINE_DANGER;
    }

}