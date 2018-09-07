package de.symeda.sormas.app.caze.edit;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BR;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.epidata.EpiDataGathering;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.dialog.AbstractDialog;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.databinding.DialogCaseEpidGatheringEditLayoutBinding;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

public class EpiDataGatheringDialog extends AbstractDialog {

    public static final String TAG = EpiDataGatheringDialog.class.getSimpleName();

    private EpiDataGathering data;
    private DialogCaseEpidGatheringEditLayoutBinding contentBinding;

    EpiDataGatheringDialog(final FragmentActivity activity, EpiDataGathering epiDataGathering) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_case_epid_gathering_edit_layout,
                R.layout.dialog_root_three_button_panel_layout,  R.string.heading_sub_case_epid_social_events, -1);

        this.data = epiDataGathering;
    }

    @Override
    protected void setBindingVariable(Context context, ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, data)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }
    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, final ViewDataBinding contentBinding, ViewDataBinding buttonPanelBinding) {
        this.contentBinding = (DialogCaseEpidGatheringEditLayoutBinding) contentBinding;
        this.contentBinding.epiDataGatheringGatheringDate.initializeDateField(getFragmentManager());

        setUpControlListeners();

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
                    NotificationHelper.showDialogNotification(EpiDataGatheringDialog.this, ERROR, e.getMessage());
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

    private void setUpControlListeners() {
        contentBinding.epiDataGatheringGatheringAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddressPopup();
            }
        });
    }

    private void openAddressPopup() {
        final Location location = (Location) contentBinding.epiDataGatheringGatheringAddress.getValue();
        final LocationDialog locationDialog = new LocationDialog(BaseActivity.getActiveActivity(), location);
        locationDialog.show();

        locationDialog.setPositiveCallback(new de.symeda.sormas.app.util.Callback() {
            @Override
            public void call() {
                contentBinding.epiDataGatheringGatheringAddress.setValue(location);
                locationDialog.dismiss();
            }
        });
    }

}
