package de.symeda.sormas.app.epidata;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.epidata.EpiDataFragmentHelper.getDiseaseOfCaseOrContact;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;

import de.symeda.sormas.api.activityascase.ActivityAsCaseType;
import de.symeda.sormas.api.event.MeansOfTransport;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureRole;
import de.symeda.sormas.api.exposure.GatheringType;
import de.symeda.sormas.api.exposure.HabitationType;
import de.symeda.sormas.api.exposure.WorkEnvironment;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.facility.FacilityTypeGroup;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BR;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.activityascase.ActivityAsCase;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.dialog.FormDialog;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.databinding.DialogActivityAsCaseEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

public class ActivityAsCaseDialog extends FormDialog {

	private final ActivityAsCase data;
	private DialogActivityAsCaseEditLayoutBinding contentBinding;
	private final boolean create;

	ActivityAsCaseDialog(final FragmentActivity activity, ActivityAsCase activityAsCase, PseudonymizableAdo activityRootData, boolean create) {
		super(
			activity,
			R.layout.dialog_root_layout,
			R.layout.dialog_activity_as_case_edit_layout,
			R.layout.dialog_root_three_button_panel_layout,
			R.string.heading_activityAsCase,
			-1,
			false,
			UiFieldAccessCheckers.forSensitiveData(activityAsCase.isPseudonymized()),
			FieldVisibilityCheckers.withDisease(getDiseaseOfCaseOrContact(activityRootData)));

		this.data = activityAsCase;
		this.create = create;
	}

	private void openAddressPopup() {
		final Location location = (Location) contentBinding.activityAsCaseLocation.getValue();
		final Location locationClone = (Location) location.clone();
		final LocationDialog locationDialog = new LocationDialog(BaseActivity.getActiveActivity(), locationClone, fieldAccessCheckers);
		locationDialog.show();
		locationDialog.setFacilityFieldsVisible(data.getTypeOfPlace() == TypeOfPlace.FACILITY, true);

		locationDialog.setPositiveCallback(() -> {
			contentBinding.activityAsCaseLocation.setValue(locationClone);
			data.setLocation(locationClone);
			if (FacilityTypeGroup.WORKING_PLACE != locationDialog.getContentBinding().facilityTypeGroup.getValue()) {
				contentBinding.activityAsCaseWorkEnvironment.setValue(null);
				contentBinding.activityAsCaseWorkEnvironment.setVisibility(View.GONE);
			} else {
				contentBinding.activityAsCaseWorkEnvironment.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	protected void setContentBinding(Context context, ViewDataBinding binding, String layoutName) {
		contentBinding = (DialogActivityAsCaseEditLayoutBinding) binding;
		binding.setVariable(BR.data, data);
	}

	@Override
	protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding buttonPanelBinding) {
		contentBinding.activityAsCaseStartDate.initializeDateField(getFragmentManager());
		contentBinding.activityAsCaseEndDate.initializeDateField(getFragmentManager());

		if (data.getId() == null) {
			setLiveValidationDisabled(true);
		}

		contentBinding.activityAsCaseActivityAsCaseType.initializeSpinner(DataUtils.getEnumItems(ActivityAsCaseType.class, true));
		contentBinding.activityAsCaseGatheringType.initializeSpinner(DataUtils.getEnumItems(GatheringType.class, true));
		contentBinding.activityAsCaseHabitationType.initializeSpinner(DataUtils.getEnumItems(HabitationType.class, true));
		contentBinding.activityAsCaseTypeOfPlace.initializeSpinner(DataUtils.getEnumItems(TypeOfPlace.class, true));
		contentBinding.activityAsCaseMeansOfTransport.initializeSpinner(DataUtils.getEnumItems(MeansOfTransport.class, true));
		contentBinding.activityAsCaseRole.initializeSpinner(DataUtils.getEnumItems(ExposureRole.class, true));
		contentBinding.activityAsCaseWorkEnvironment.initializeSpinner(DataUtils.getEnumItems(WorkEnvironment.class, true));

		contentBinding.activityAsCaseActivityAsCaseType.addValueChangedListener(e -> {
		});
		contentBinding.activityAsCaseMeansOfTransport.addValueChangedListener(e -> {
			contentBinding.activityAsCaseConnectionNumber.setCaption(
				e.getValue() == MeansOfTransport.PLANE
					? I18nProperties.getCaption(Captions.exposureFlightNumber)
					: I18nProperties.getPrefixCaption(ExposureDto.I18N_PREFIX, ExposureDto.CONNECTION_NUMBER));
		});

		contentBinding.activityAsCaseLocation.setOnClickListener(v -> openAddressPopup());

		setFieldVisibilitiesAndAccesses(ExposureDto.class, (ViewGroup) getRootView());

		contentBinding.activityAsCaseTypeOfPlace.addValueChangedListener(e -> {
			if (e.getValue() != TypeOfPlace.FACILITY) {
				contentBinding.activityAsCaseWorkEnvironment.setValue(null);
				contentBinding.activityAsCaseWorkEnvironment.setVisibility(View.GONE);
			} else {
				FacilityType facilityType = data.getLocation().getFacilityType();
				contentBinding.activityAsCaseWorkEnvironment.setVisibility(
					facilityType == null || FacilityTypeGroup.WORKING_PLACE != facilityType.getFacilityTypeGroup() ? View.GONE : View.VISIBLE);
			}
		});
	}

	@Override
	protected void onPositiveClick() {
		setLiveValidationDisabled(false);
		try {
			FragmentValidator.validate(getContext(), contentBinding);
		} catch (ValidationException e) {
			NotificationHelper.showDialogNotification(de.symeda.sormas.app.epidata.ActivityAsCaseDialog.this, ERROR, e.getMessage());
			return;
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
