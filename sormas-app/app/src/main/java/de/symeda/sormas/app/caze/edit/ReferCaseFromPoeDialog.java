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

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;

import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.FragmentActivity;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.dialog.AbstractDialog;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.databinding.DialogMoveCaseLayoutBinding;
import de.symeda.sormas.app.util.InfrastructureHelper;

public class ReferCaseFromPoeDialog extends AbstractDialog {

	public static final String TAG = ReferCaseFromPoeDialog.class.getSimpleName();

	protected Case data;
	protected DialogMoveCaseLayoutBinding contentBinding;

	// Constructor

	ReferCaseFromPoeDialog(final FragmentActivity activity, Case caze, int headingResourceId) {
		super(
			activity,
			R.layout.dialog_root_layout,
			R.layout.dialog_move_case_layout,
			R.layout.dialog_root_two_button_panel_layout,
			headingResourceId,
			-1);

		this.data = caze;
	}

	ReferCaseFromPoeDialog(final FragmentActivity activity, Case caze) {
		this(activity, caze, R.string.heading_refer_case_from_poe);
	}

	// Overrides

	@Override
	protected void setContentBinding(Context context, ViewDataBinding binding, String layoutName) {
		this.contentBinding = (DialogMoveCaseLayoutBinding) binding;

		if (!binding.setVariable(BR.data, data)) {
			Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
		}
	}

	@Override
	protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding buttonPanelBinding) {
		InfrastructureHelper
			.initializeHealthFacilityDetailsFieldVisibility(contentBinding.caseDataHealthFacility, contentBinding.caseDataHealthFacilityDetails);

		List<Item> initialRegions = InfrastructureHelper.loadRegions();
		List<Item> initialDistricts = InfrastructureHelper.loadDistricts(data.getRegion());
		List<Item> initialCommunities = InfrastructureHelper.loadCommunities(data.getDistrict());
		List<Item> initialFacilities = InfrastructureHelper.loadFacilities(data.getDistrict(), data.getCommunity());
		InfrastructureHelper.initializeFacilityFields(
			contentBinding.caseDataRegion,
			initialRegions,
			data.getRegion(),
			contentBinding.caseDataDistrict,
			initialDistricts,
			data.getDistrict(),
			contentBinding.caseDataCommunity,
			initialCommunities,
			data.getCommunity(),
			contentBinding.caseDataHealthFacility,
			initialFacilities,
			data.getHealthFacility());
	}

	@Override
	public void onPositiveClick() {
		try {
			FragmentValidator.validate(getContext(), contentBinding);
		} catch (ValidationException e) {
			NotificationHelper.showDialogNotification(ReferCaseFromPoeDialog.this, ERROR, e.getMessage());
			return;
		}

		try {
			data.getHospitalization().setAdmissionDate(new Date());
			DatabaseHelper.getCaseDao().saveAndSnapshot(data);
		} catch (DaoException e) {
			NotificationHelper
				.showDialogNotification(ReferCaseFromPoeDialog.this, ERROR, getContext().getResources().getString(getErrorMessageText()));
			return;
		}

		super.onPositiveClick();
	}

	@Override
	public boolean isPositiveButtonVisible() {
		return true;
	}

	@Override
	public boolean isNegativeButtonVisible() {
		return true;
	}

	@Override
	public boolean isRounded() {
		return true;
	}

	@Override
	public ControlButtonType getNegativeButtonType() {
		return ControlButtonType.LINE_DANGER;
	}

	@Override
	public ControlButtonType getPositiveButtonType() {
		return ControlButtonType.LINE_PRIMARY;
	}

	@Override
	public int getPositiveButtonText() {
		return R.string.action_refer_case_from_poe;
	}

	public int getErrorMessageText() {
		return R.string.error_case_referring_from_poe;
	}
}
