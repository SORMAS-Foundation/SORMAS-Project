/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.caze.edit;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

import android.content.Context;
import android.util.Log;

import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.FragmentActivity;

import de.symeda.sormas.api.epidata.TravelType;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.epidata.EpiDataTravel;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.dialog.AbstractDialog;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.databinding.DialogCaseEpidTravelEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

public class EpiDataTravelDialog extends AbstractDialog {

	public static final String TAG = EpiDataTravelDialog.class.getSimpleName();

	private EpiDataTravel data;
	private DialogCaseEpidTravelEditLayoutBinding contentBinding;

	// Constructor

	EpiDataTravelDialog(final FragmentActivity activity, EpiDataTravel epiDataTravel) {
		super(
			activity,
			R.layout.dialog_root_layout,
			R.layout.dialog_case_epid_travel_edit_layout,
			R.layout.dialog_root_three_button_panel_layout,
			R.string.heading_travel,
			-1);

		this.data = epiDataTravel;
	}

	// Overrides

	@Override
	protected void setContentBinding(Context context, ViewDataBinding binding, String layoutName) {
		this.contentBinding = (DialogCaseEpidTravelEditLayoutBinding) binding;

		if (!binding.setVariable(BR.data, data)) {
			Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
		}
	}

	@Override
	protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding buttonPanelBinding) {
		contentBinding.epiDataTravelTravelType.initializeSpinner(DataUtils.getEnumItems(TravelType.class, true));
		contentBinding.epiDataTravelTravelDateFrom.initializeDateField(getFragmentManager());
		contentBinding.epiDataTravelTravelDateTo.initializeDateField(getFragmentManager());

		CaseValidator.initializeEpiDataTravelValidation(contentBinding);

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
			NotificationHelper.showDialogNotification(EpiDataTravelDialog.this, ERROR, e.getMessage());
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
