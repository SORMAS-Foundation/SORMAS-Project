package de.symeda.sormas.app.vaccinationinfo.read;

import android.os.Bundle;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.vaccinationinfo.VaccinationInfo;
import de.symeda.sormas.app.databinding.FragmentVaccinationInfoReadLayoutBinding;

public class VaccinationReadFragment extends BaseReadFragment<FragmentVaccinationInfoReadLayoutBinding, VaccinationInfo, VaccinationInfo> {

	private VaccinationInfo record;

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {
		record = getActivityRootData();
	}

	@Override
	public void onLayoutBinding(FragmentVaccinationInfoReadLayoutBinding contentBinding) {
		contentBinding.setData(record);
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_vaccination_info_read_layout;
	}

	@Override
	public VaccinationInfo getPrimaryData() {
		return record;
	}
}
