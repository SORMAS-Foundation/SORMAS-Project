package de.symeda.sormas.app.environment.edit;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

import java.util.Arrays;
import java.util.List;

import android.view.View;

import de.symeda.sormas.api.environment.EnvironmentMedia;
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
import de.symeda.sormas.app.databinding.FragmentEnvironmentNewLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

public class EnvironmentNewFragment extends BaseEditFragment<FragmentEnvironmentNewLayoutBinding, Environment, Environment> {

	private Environment record;

	private List<Item> environmentMediaList;

	private List<Item> initialResponsibleUserList;

	public static EnvironmentNewFragment newInstance(Environment activityRootData) {
		return newInstance(EnvironmentNewFragment.class, EnvironmentNewActivity.buildBundle().get(), activityRootData);
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_environment_new_layout;
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
	}

	private List<Item> updateResponsibleUserList(District district) {
		return DataUtils.toItems(
			DatabaseHelper.getUserDao()
				.getUsersWithJurisdictionLevel(JurisdictionLevel.DISTRICT, null, district, Arrays.asList(UserRight.ENVIRONMENT_EDIT)));

	}

	@Override
	protected void onLayoutBinding(FragmentEnvironmentNewLayoutBinding contentBinding) {
		setUpControlListeners(contentBinding);
		contentBinding.setData(record);

		EnvironmentValidator.initializeLocationValidations(contentBinding.environmentLocation, () -> record.getLocation());

		contentBinding.environmentEnvironmentMedia.initializeSpinner(environmentMediaList);

		contentBinding.environmentReportDate.initializeDateField(getFragmentManager());

		contentBinding.environmentResponsibleUser.initializeSpinner(initialResponsibleUserList);
	}

	private void setUpControlListeners(final FragmentEnvironmentNewLayoutBinding contentBinding) {
		contentBinding.environmentLocation.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				openAddressPopup(contentBinding);
			}
		});
	}

	private void openAddressPopup(final FragmentEnvironmentNewLayoutBinding contentBinding) {
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
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.heading_environment_new);
	}
}
