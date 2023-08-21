package de.symeda.sormas.app.environment.read;

import static android.view.View.GONE;

import android.os.Bundle;
import android.view.View;

import de.symeda.sormas.api.environment.WaterUse;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.environment.Environment;
import de.symeda.sormas.app.databinding.FragmentEnvironmentReadLayoutBinding;

public class EnvironmentReadFragment extends BaseReadFragment<FragmentEnvironmentReadLayoutBinding, Environment, Environment> {

	private Environment record;

	public static EnvironmentReadFragment newInstance(Environment activityRootData) {
		EnvironmentReadFragment environmentReadFragment = newInstanceWithFieldCheckers(
			EnvironmentReadFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.getNoop(),
			UiFieldAccessCheckers.getDefault(activityRootData.isPseudonymized()));

		return environmentReadFragment;
	}

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {
		record = getActivityRootData();
	}

	@Override
	protected void onLayoutBinding(FragmentEnvironmentReadLayoutBinding contentBinding) {
		contentBinding.setData(record);
		contentBinding.environmentWaterUse.setEnumClass(WaterUse.class);
		contentBinding.environmentWaterUse.setEnabled(false);
		contentBinding.environmentOtherWaterUse.setVisibility(Boolean.TRUE.equals(record.getWateruse().get(WaterUse.OTHER)) ? View.VISIBLE : GONE);
	}

	@Override
	public void onAfterLayoutBinding(FragmentEnvironmentReadLayoutBinding contentBinding) {
		super.onAfterLayoutBinding(contentBinding);
		setFieldVisibilitiesAndAccesses(ImmunizationDto.class, contentBinding.mainContent);

	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_environment_read_layout;
	}

	@Override
	public Environment getPrimaryData() {
		return record;
	}

	@Override
	public boolean showEditAction() {
		return ConfigProvider.hasUserRight(UserRight.ENVIRONMENT_EDIT);
	}
}
