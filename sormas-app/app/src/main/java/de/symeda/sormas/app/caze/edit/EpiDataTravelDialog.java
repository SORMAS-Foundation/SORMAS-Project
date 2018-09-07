package de.symeda.sormas.app.caze.edit;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;

import java.util.List;

import de.symeda.sormas.api.epidata.TravelType;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.epidata.EpiDataTravel;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.dialog.AbstractDialog;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.databinding.DialogCaseEpidTravelEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.component.validation.FragmentValidator;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

public class EpiDataTravelDialog extends AbstractDialog {

    public static final String TAG = EpiDataTravelDialog.class.getSimpleName();

    private EpiDataTravel data;

    EpiDataTravelDialog(final FragmentActivity activity, EpiDataTravel epiDataTravel) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_case_epid_travel_edit_layout,
                R.layout.dialog_root_three_button_panel_layout,  R.string.heading_sub_case_epid_travels, -1);

        this.data = epiDataTravel;
    }

    @Override
    protected void setBindingVariable(Context context, ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, data)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }
    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, final ViewDataBinding contentBinding, ViewDataBinding buttonPanelBinding) {
        ((DialogCaseEpidTravelEditLayoutBinding) contentBinding).epiDataTravelTravelType.initializeSpinner(DataUtils.getEnumItems(TravelType.class, true));
        ((DialogCaseEpidTravelEditLayoutBinding) contentBinding).epiDataTravelTravelDateFrom.initializeDateField(getFragmentManager());
        ((DialogCaseEpidTravelEditLayoutBinding) contentBinding).epiDataTravelTravelDateTo.initializeDateField(getFragmentManager());

        CaseValidator.initializeEpiDataTravelValidation(getContext(), (DialogCaseEpidTravelEditLayoutBinding) contentBinding);

        if (data.getId() == null) {
            setLiveValidationDisabled(true);
        }

        setPositiveCallback(new de.symeda.sormas.app.util.Callback() {
            @Override
            public void call() {
                setLiveValidationDisabled(false);
                try {
                    FragmentValidator.validate(getContext(), contentBinding);
                } catch (ValidationException e) {
                    NotificationHelper.showDialogNotification(EpiDataTravelDialog.this, ERROR, e.getMessage());
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