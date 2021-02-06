package de.symeda.sormas.app.epidata;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;
import static de.symeda.sormas.app.epidata.EpiDataFragmentHelper.getDiseaseOfCaseOrContact;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;

import de.symeda.sormas.api.epidata.AnimalCondition;
import de.symeda.sormas.api.event.MeansOfTransport;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.exposure.AnimalContactType;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureRole;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.exposure.GatheringType;
import de.symeda.sormas.api.exposure.HabitationType;
import de.symeda.sormas.api.exposure.TypeOfAnimal;
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
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.exposure.Exposure;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.dialog.FormDialog;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.databinding.DialogExposureEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

public class ExposureDialog extends FormDialog {

	private final Exposure data;
	private DialogExposureEditLayoutBinding contentBinding;
	private final boolean create;

	ExposureDialog(final FragmentActivity activity, Exposure exposure, PseudonymizableAdo activityRootData, boolean create) {
		super(
			activity,
			R.layout.dialog_root_layout,
			R.layout.dialog_exposure_edit_layout,
			R.layout.dialog_root_three_button_panel_layout,
			R.string.heading_exposure,
			-1,
			false,
			UiFieldAccessCheckers.forSensitiveData(exposure.isPseudonymized()),
			FieldVisibilityCheckers.withDisease(getDiseaseOfCaseOrContact(activityRootData)));

		this.data = exposure;
		this.create = create;
	}

	private void setUpHeadingVisibilities(ExposureType exposureType) {
		if (exposureType == ExposureType.ANIMAL_CONTACT) {
			contentBinding.headingAnimalContactDetails.setVisibility(View.VISIBLE);
			contentBinding.headingBurialDetails.setVisibility(View.GONE);
		} else if (exposureType == ExposureType.BURIAL) {
			contentBinding.headingBurialDetails.setVisibility(View.VISIBLE);
			contentBinding.headingAnimalContactDetails.setVisibility(View.GONE);
		} else {
			contentBinding.headingAnimalContactDetails.setVisibility(View.GONE);
			contentBinding.headingBurialDetails.setVisibility(View.GONE);
		}
	}

	private void openAddressPopup() {
		final Location location = (Location) contentBinding.exposureLocation.getValue();
		final Location locationClone = (Location) location.clone();
		final LocationDialog locationDialog = new LocationDialog(BaseActivity.getActiveActivity(), locationClone, fieldAccessCheckers);
		locationDialog.show();
		locationDialog.setFacilityFieldsVisible(data.getTypeOfPlace() == TypeOfPlace.FACILITY, true);

		locationDialog.setPositiveCallback(() -> {
			contentBinding.exposureLocation.setValue(locationClone);
			data.setLocation(locationClone);
			if (FacilityTypeGroup.WORKING_PLACE != locationDialog.getContentBinding().facilityTypeGroup.getValue()) {
				contentBinding.exposureWorkEnvironment.setValue(null);
				contentBinding.exposureWorkEnvironment.setVisibility(View.GONE);
			} else {
				contentBinding.exposureWorkEnvironment.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	protected void setContentBinding(Context context, ViewDataBinding binding, String layoutName) {
		contentBinding = (DialogExposureEditLayoutBinding) binding;
		binding.setVariable(BR.data, data);
	}

	@Override
	protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding buttonPanelBinding) {
		contentBinding.exposureStartDate.initializeDateField(getFragmentManager());
		contentBinding.exposureEndDate.initializeDateField(getFragmentManager());

		if (data.getId() == null) {
			setLiveValidationDisabled(true);
		}

		contentBinding.exposureExposureType.initializeSpinner(DataUtils.getEnumItems(ExposureType.class, true));
		contentBinding.exposureGatheringType.initializeSpinner(DataUtils.getEnumItems(GatheringType.class, true));
		contentBinding.exposureHabitationType.initializeSpinner(DataUtils.getEnumItems(HabitationType.class, true));
		contentBinding.exposureTypeOfAnimal.initializeSpinner(DataUtils.getEnumItems(TypeOfAnimal.class, true));
		contentBinding.exposureAnimalCondition.initializeSpinner(DataUtils.getEnumItems(AnimalCondition.class, true));
		contentBinding.exposureAnimalContactType.initializeSpinner(DataUtils.getEnumItems(AnimalContactType.class, true));
		contentBinding.exposureTypeOfPlace.initializeSpinner(DataUtils.getEnumItems(TypeOfPlace.class, true));
		contentBinding.exposureMeansOfTransport.initializeSpinner(DataUtils.getEnumItems(MeansOfTransport.class, true));
		contentBinding.exposureExposureRole.initializeSpinner(DataUtils.getEnumItems(ExposureRole.class, true));
		contentBinding.exposureWorkEnvironment.initializeSpinner(DataUtils.getEnumItems(WorkEnvironment.class, true));

		setUpHeadingVisibilities(data.getExposureType());
		contentBinding.exposureExposureType.addValueChangedListener(e -> {
			setUpHeadingVisibilities((ExposureType) e.getValue());
		});
		contentBinding.exposureMeansOfTransport.addValueChangedListener(e -> {
			contentBinding.exposureConnectionNumber.setCaption(
				e.getValue() == MeansOfTransport.PLANE
					? I18nProperties.getCaption(Captions.exposureFlightNumber)
					: I18nProperties.getPrefixCaption(ExposureDto.I18N_PREFIX, ExposureDto.CONNECTION_NUMBER));
		});

		contentBinding.exposureLocation.setOnClickListener(v -> openAddressPopup());

		setFieldVisibilitiesAndAccesses(ExposureDto.class, (ViewGroup) getRootView());

		contentBinding.exposureTypeOfPlace.addValueChangedListener(e -> {
			if (e.getValue() != TypeOfPlace.FACILITY) {
				contentBinding.exposureWorkEnvironment.setValue(null);
				contentBinding.exposureWorkEnvironment.setVisibility(View.GONE);
			} else {
				FacilityType facilityType = data.getLocation().getFacilityType();
				contentBinding.exposureWorkEnvironment.setVisibility(
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
			NotificationHelper.showDialogNotification(ExposureDialog.this, ERROR, e.getMessage());
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
