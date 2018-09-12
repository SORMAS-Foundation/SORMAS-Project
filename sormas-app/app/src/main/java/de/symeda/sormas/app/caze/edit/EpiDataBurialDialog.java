package de.symeda.sormas.app.caze.edit;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.epidata.EpiDataBurial;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.dialog.AbstractDialog;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.databinding.DialogCaseEpidBurialEditLayoutBinding;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

public class EpiDataBurialDialog extends AbstractDialog {

    public static final String TAG = EpiDataBurialDialog.class.getSimpleName();

    private EpiDataBurial data;
    private DialogCaseEpidBurialEditLayoutBinding contentBinding;

    // Constructor

    EpiDataBurialDialog(final FragmentActivity activity, EpiDataBurial epiDataBurial) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_case_epid_burial_edit_layout,
                R.layout.dialog_root_three_button_panel_layout,
                R.string.heading_burial, -1);

        this.data = epiDataBurial;
    }

    // Instance methods

    private void setUpControlListeners() {
        contentBinding.epiDataBurialBurialAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddressPopup();
            }
        });
    }

    private void openAddressPopup() {
        final Location location = (Location) contentBinding.epiDataBurialBurialAddress.getValue();
        final Location locationClone = (Location) location.clone();
        final LocationDialog locationDialog = new LocationDialog(BaseActivity.getActiveActivity(), locationClone);
        locationDialog.show();

        locationDialog.setPositiveCallback(new de.symeda.sormas.app.util.Callback() {
            @Override
            public void call() {
                contentBinding.epiDataBurialBurialAddress.setValue(locationClone);
                data.setBurialAddress(locationClone);
                locationDialog.dismiss();
            }
        });
    }

    // Overrides

    @Override
    protected void setContentBinding(Context context, ViewDataBinding binding, String layoutName) {
        this.contentBinding = (DialogCaseEpidBurialEditLayoutBinding) binding;

        if (!binding.setVariable(BR.data, data)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }
    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding buttonPanelBinding) {
        this.contentBinding.epiDataBurialBurialDateFrom.initializeDateField(getFragmentManager());
        this.contentBinding.epiDataBurialBurialDateTo.initializeDateField(getFragmentManager());

        CaseValidator.initializeEpiDataBurialValidation(getContext(), contentBinding);

        setUpControlListeners();

        if (data.getId() == null) {
            setLiveValidationDisabled(true);
        }
    }

    @Override
    public void onPositiveClick() {
        setLiveValidationDisabled(false);
        try {
            FragmentValidator.validate(getContext(), contentBinding);
        } catch (ValidationException e) {
            NotificationHelper.showDialogNotification(EpiDataBurialDialog.this, ERROR, e.getMessage());
            return;
        }

        super.onPositiveClick();
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
