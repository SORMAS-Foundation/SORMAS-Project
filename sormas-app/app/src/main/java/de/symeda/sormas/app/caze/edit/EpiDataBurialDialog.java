/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.caze.edit;

import android.content.Context;
import android.util.Log;

import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.FragmentActivity;

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
        contentBinding.epiDataBurialBurialAddress.setOnClickListener(v -> openAddressPopup());
    }

    private void openAddressPopup() {
        final Location location = (Location) contentBinding.epiDataBurialBurialAddress.getValue();
        final Location locationClone = (Location) location.clone();
        final LocationDialog locationDialog = new LocationDialog(BaseActivity.getActiveActivity(), locationClone, null);
        locationDialog.show();

        locationDialog.setPositiveCallback(() -> {
            contentBinding.epiDataBurialBurialAddress.setValue(locationClone);
            data.setBurialAddress(locationClone);
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

        CaseValidator.initializeEpiDataBurialValidation(contentBinding);

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
