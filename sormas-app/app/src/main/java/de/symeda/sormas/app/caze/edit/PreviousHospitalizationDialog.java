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

import java.util.List;

import android.content.Context;
import android.util.Log;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.FragmentActivity;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.hospitalization.HospitalizationReasonType;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.backend.infrastructure.InfrastructureHelper;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.dialog.FormDialog;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.databinding.DialogPreviousHospitalizationLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.InfrastructureDaoHelper;
import de.symeda.sormas.app.util.InfrastructureFieldsDependencyHandler;

public class PreviousHospitalizationDialog extends FormDialog {

	public static final String TAG = PreviousHospitalizationDialog.class.getSimpleName();

	private PreviousHospitalization data;
	private DialogPreviousHospitalizationLayoutBinding contentBinding;
	private boolean create;

	// Constructor

	PreviousHospitalizationDialog(final FragmentActivity activity, PreviousHospitalization previousHospitalization, boolean create) {
		super(
			activity,
			R.layout.dialog_root_layout,
			R.layout.dialog_previous_hospitalization_layout,
			R.layout.dialog_root_three_button_panel_layout,
			R.string.heading_previous_hospitalization,
			-1,
			UiFieldAccessCheckers.forSensitiveData(previousHospitalization.isPseudonymized()));

		this.data = previousHospitalization;

		if (data.getId() != null) {
			if (data.getRegion() == null) {
				data.setRegion(InfrastructureDaoHelper.unknownRegion);
			}
			if (data.getDistrict() == null) {
				data.setDistrict(InfrastructureDaoHelper.unknownDistrict);
			}
			if (data.getHealthFacility() == null) {
				data.setHealthFacility(InfrastructureDaoHelper.unknownFacility);
			}
		}

		this.create = create;
	}

	// Overrides

	@Override
	protected void setContentBinding(Context context, ViewDataBinding binding, String layoutName) {
		contentBinding = (DialogPreviousHospitalizationLayoutBinding) binding;

		if (!binding.setVariable(BR.data, data)) {
			Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
		}
	}

	@Override
	protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding buttonPanelBinding) {
		contentBinding.casePreviousHospitalizationAdmissionDate.initializeDateField(getFragmentManager());
		contentBinding.casePreviousHospitalizationDischargeDate.initializeDateField(getFragmentManager());
		contentBinding.casePreviousHospitalizationIntensiveCareUnitStart.initializeDateField(getFragmentManager());
		contentBinding.casePreviousHospitalizationIntensiveCareUnitEnd.initializeDateField(getFragmentManager());
		contentBinding.casePreviousHospitalizationIsolationDate.initializeDateField(getFragmentManager());

		if (data.getId() == null) {
			setLiveValidationDisabled(true);
		}

		List<Item> initialRegions = InfrastructureDaoHelper.loadRegionsByServerCountry();
		List<Item> initialDistricts = InfrastructureDaoHelper.loadDistricts(data.getRegion());
		List<Item> initialCommunities = InfrastructureDaoHelper.loadCommunities(data.getDistrict());
		List<Item> initialFacilities = InfrastructureDaoHelper.loadFacilities(data.getDistrict(), data.getCommunity(), FacilityType.HOSPITAL);
		List<Item> hospitalizationReasons = DataUtils.getEnumItems(HospitalizationReasonType.class, true);

		contentBinding.casePreviousHospitalizationHospitalizationReason.initializeSpinner(hospitalizationReasons);

		setFieldVisibilitiesAndAccesses(PreviousHospitalizationDto.class, contentBinding.mainContent);

		if (!isFieldAccessible(PreviousHospitalizationDto.class, PreviousHospitalizationDto.HEALTH_FACILITY)) {
			this.contentBinding.casePreviousHospitalizationRegion.setEnabled(false);
			this.contentBinding.casePreviousHospitalizationDistrict.setEnabled(false);
		}

		InfrastructureDaoHelper.initializeHealthFacilityDetailsFieldVisibility(
			contentBinding.casePreviousHospitalizationHealthFacility,
			contentBinding.casePreviousHospitalizationHealthFacilityDetails);
		InfrastructureFieldsDependencyHandler.withUnknownValues.initializeFacilityFields(
			data,
			contentBinding.casePreviousHospitalizationRegion,
			initialRegions,
			data.getRegion(),
			contentBinding.casePreviousHospitalizationDistrict,
			initialDistricts,
			data.getDistrict(),
			contentBinding.casePreviousHospitalizationCommunity,
			initialCommunities,
			data.getCommunity(),
			null,
			null,
			null,
			null,
			null,
			null,
			contentBinding.casePreviousHospitalizationHealthFacility,
			initialFacilities,
			data.getHealthFacility(),
			contentBinding.casePreviousHospitalizationHealthFacilityDetails,
			false);

		CaseValidator.initializePreviousHospitalizationValidation(contentBinding);
	}

	@Override
	public void onPositiveClick() {
		setLiveValidationDisabled(false);
		try {
			FragmentValidator.validate(getContext(), contentBinding);
		} catch (ValidationException e) {
			NotificationHelper.showDialogNotification(PreviousHospitalizationDialog.this, ERROR, e.getMessage());
			return;
		}

		if (InfrastructureDaoHelper.isUnknownRegion(data.getRegion())) {
			data.setRegion(null);
		}
		if (InfrastructureDaoHelper.isUnknownDistrict(data.getDistrict())) {
			data.setDistrict(null);
		}
		if (InfrastructureDaoHelper.isUnknownFacility(data.getHealthFacility())) {
			data.setHealthFacility(null);
		}

		super.setCloseOnPositiveButtonClick(true);
		super.onPositiveClick();
	}

	@Override
	public boolean isDeleteButtonVisible() {
		return !create;
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
