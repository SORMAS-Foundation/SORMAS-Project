package de.symeda.sormas.app.vaccinationinfo.edit;

import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.vaccinationinfo.VaccinationInfo;
import de.symeda.sormas.app.contact.edit.ContactEditFragment;
import de.symeda.sormas.app.databinding.FragmentVaccinationInfoEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

public class VaccinationInfoEditFragment extends BaseEditFragment<FragmentVaccinationInfoEditLayoutBinding, VaccinationInfo, VaccinationInfo> {

	private VaccinationInfo record;

	public static void setUpLayoutBinding(
		ContactEditFragment contactEditFragment,
		VaccinationInfo vaccinationInfo,
		FragmentVaccinationInfoEditLayoutBinding vaccinationInfoEditLayout) {
		vaccinationInfoEditLayout.setData(vaccinationInfo);
		vaccinationInfoEditLayout.setVaccinationClass(Vaccination.class);

		vaccinationInfoEditLayout.vaccinationInfoVaccinationInfoSource.initializeSpinner(DataUtils.getEnumItems(VaccinationInfoSource.class, true));
		vaccinationInfoEditLayout.vaccinationInfoVaccineName.initializeSpinner(DataUtils.getEnumItems(Vaccine.class, true));
		vaccinationInfoEditLayout.vaccinationInfoVaccineManufacturer.initializeSpinner(DataUtils.getEnumItems(VaccineManufacturer.class, true));
		vaccinationInfoEditLayout.vaccinationInfoFirstVaccinationDate.initializeDateField(contactEditFragment.getChildFragmentManager());
		vaccinationInfoEditLayout.vaccinationInfoLastVaccinationDate.initializeDateField(contactEditFragment.getChildFragmentManager());

//		vaccinationInfoEditLayout.vaccinationInfoVaccineName.addValueChangedListener(new ValueChangeListener() {
//
//			private Vaccine currentVaccine = vaccinationInfo.getVaccineName();
//
//			@Override
//			public void onChange(ControlPropertyField e) {
//				Vaccine vaccine = (Vaccine) e.getValue();
//
//				if (currentVaccine != vaccine) {
//					vaccinationInfoEditLayout.vaccinationInfoVaccineManufacturer.setValue(vaccine != null ? vaccine.getManufacturer() : null);
//					currentVaccine = vaccine;
//				}
//			}
//		});
//
//		ValidationHelper.initIntegerValidator(
//			vaccinationInfoEditLayout.vaccinationInfoVaccinationDoses,
//			I18nProperties.getValidationError(Validations.vaccineDosesFormat),
//			1,
//			10);
//
//		if (contactEditFragment.isVisibleAllowed(VaccinationInfoDto.class, vaccinationInfoEditLayout.vaccinationInfoVaccineName)) {
//			contactEditFragment.setVisibleWhen(vaccinationInfoEditLayout.vaccinationInfoVaccineName, vaccinationInfoEditLayout.vaccinationInfoVaccination, Vaccination.VACCINATED);
//		}
//		if (contactEditFragment.isVisibleAllowed(VaccinationInfoDto.class, vaccinationInfoEditLayout.vaccinationInfoVaccineManufacturer)) {
//			contactEditFragment.setVisibleWhen(vaccinationInfoEditLayout.vaccinationInfoVaccineManufacturer, vaccinationInfoEditLayout.vaccinationInfoVaccination, Vaccination.VACCINATED);
//		}
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_vaccination_info_edit_layout;
	}

	@Override
	public VaccinationInfo getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		record = getActivityRootData();
	}

	@Override
	protected void onLayoutBinding(FragmentVaccinationInfoEditLayoutBinding contentBinding) {
		contentBinding.setData(record);
		contentBinding.setVaccinationClass(Vaccination.class);
	}
}
