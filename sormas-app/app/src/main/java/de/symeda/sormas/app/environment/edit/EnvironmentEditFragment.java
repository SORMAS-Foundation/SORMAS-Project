package de.symeda.sormas.app.environment.edit;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

import android.view.View;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.environment.EnvironmentInfrastructureDetails;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.environment.WaterType;
import de.symeda.sormas.api.environment.WaterUse;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.environment.Environment;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.databinding.FragmentEnvironmentEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

public class EnvironmentEditFragment extends BaseEditFragment<FragmentEnvironmentEditLayoutBinding, Environment, Environment> {

	private Environment record;

	private List<Item> environmentMediaList;

	private List<Item> initialResponsibleUserList;

	private List<Item> investigationStatusList;

	private List<Item> environmentWaterType;

	private List<Item> environmentInfrastructureDetails;

	private List<Item> environmentWaterUse;

	public static EnvironmentEditFragment newInstance(Environment activityRootData) {
		return newInstance(EnvironmentEditFragment.class, null, activityRootData);
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_environment_edit_layout;
	}

	@Override
	public Environment getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		record = getActivityRootData();
		environmentMediaList = DataUtils.getEnumItems(EnvironmentMedia.class, false);

		initialResponsibleUserList = updateResponsibleUserList(record.getLocation().getDistrict());

		investigationStatusList = DataUtils.getEnumItems(InvestigationStatus.class, false);

		environmentWaterType = DataUtils.getEnumItems(WaterType.class, true);

		environmentInfrastructureDetails = DataUtils.getEnumItems(EnvironmentInfrastructureDetails.class, true);

		environmentWaterUse = DataUtils.getEnumItems(WaterUse.class, false);
	}

	private List<Item> updateResponsibleUserList(District district) {
		return DataUtils.toItems(
			DatabaseHelper.getUserDao()
				.getUsersWithJurisdictionLevel(JurisdictionLevel.DISTRICT, null, district, Arrays.asList(UserRight.ENVIRONMENT_EDIT)));

	}

	@Override
	protected void onLayoutBinding(FragmentEnvironmentEditLayoutBinding contentBinding) {
		setUpControlListeners(contentBinding);
		contentBinding.setData(record);

		EnvironmentValidator.initializeLocationValidations(contentBinding.environmentLocation, () -> record.getLocation());

		contentBinding.environmentEnvironmentMedia.initializeSpinner(environmentMediaList);
		contentBinding.environmentReportDate.initializeDateField(getFragmentManager());
		contentBinding.environmentInvestigationStatus.initializeSpinner(investigationStatusList);
		contentBinding.environmentResponsibleUser.initializeSpinner(initialResponsibleUserList);
		contentBinding.environmentWaterType.initializeSpinner(environmentWaterType);
		contentBinding.environmentInfrastructureDetails.initializeSpinner(environmentInfrastructureDetails);
		contentBinding.environmentWaterUse.setEnumClass(WaterUse.class);

	}

	private void setUpControlListeners(final FragmentEnvironmentEditLayoutBinding contentBinding) {
		contentBinding.environmentLocation.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				openAddressPopup(contentBinding);
			}
		});
	}

	private void openAddressPopup(final FragmentEnvironmentEditLayoutBinding contentBinding) {
		final Location location = record.getLocation();
		final Location locationClone = (Location) location.clone();
		final LocationDialog locationDialog = new LocationDialog(BaseActivity.getActiveActivity(), locationClone, false, null);
		locationDialog.show();
		locationDialog.getContentBinding().locationRegion.setRequired(true);
		locationDialog.getContentBinding().locationDistrict.setRequired(true);
		locationDialog.getContentBinding().locationLatitude.setRequired(true);
		locationDialog.getContentBinding().locationLongitude.setRequired(true);
		locationDialog.setFacilityFieldsVisible(true, true);

		locationDialog.setPositiveCallback(() -> {
			try {
				FragmentValidator.validate(getContext(), locationDialog.getContentBinding());
				contentBinding.environmentLocation.setValue(locationClone);
				record.setLocation(locationClone);
				contentBinding.environmentResponsibleUser.initializeSpinner(updateResponsibleUserList(record.getLocation().getDistrict()));

				locationDialog.dismiss();
			} catch (ValidationException e) {
				NotificationHelper.showDialogNotification(locationDialog, ERROR, e.getMessage());
			}
		});
	}

	@Override
	public void onAfterLayoutBinding(FragmentEnvironmentEditLayoutBinding contentBinding) {
		if (!record.getWateruse().containsKey(WaterUse.OTHER) || Boolean.FALSE.equals(record.getWateruse().get(WaterUse.OTHER))) {
			contentBinding.environmentOtherWaterUse.setVisibility(View.GONE);
		}

		contentBinding.environmentWaterUse.addValueChangedListener(view -> {
			Map<WaterUse, Boolean> waterUseBooleanMap = (Map<WaterUse, Boolean>) contentBinding.environmentWaterUse.getValue();
			if (waterUseBooleanMap.containsKey(WaterUse.OTHER) && Boolean.TRUE.equals(waterUseBooleanMap.get(WaterUse.OTHER))) {
				contentBinding.environmentOtherWaterUse.setVisibility(View.VISIBLE);
			} else {
				contentBinding.environmentOtherWaterUse.setVisibility(View.GONE);
				contentBinding.environmentOtherWaterUse.setValue(null);
			}
		});

		contentBinding.environmentResponsibleUser.setPseudonymized(record.isPseudonymized());
	}
}
