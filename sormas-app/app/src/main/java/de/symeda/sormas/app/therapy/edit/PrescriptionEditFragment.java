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

package de.symeda.sormas.app.therapy.edit;

import static android.view.View.GONE;

import java.util.List;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.TreatmentRoute;
import de.symeda.sormas.api.therapy.TreatmentType;
import de.symeda.sormas.api.therapy.TypeOfDrug;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.therapy.Prescription;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.core.FieldHelper;
import de.symeda.sormas.app.databinding.FragmentPrescriptionEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

public class PrescriptionEditFragment extends BaseEditFragment<FragmentPrescriptionEditLayoutBinding, Prescription, Prescription> {

	public static final String TAG = PrescriptionEditFragment.class.getSimpleName();

	private Prescription record;

	// Enum lists

	private List<Item> prescriptionTypeList;
	private List<Item> treatmentRouteList;

	// Static methods

	public static PrescriptionEditFragment newInstance(Prescription activityRootData) {
		return newInstanceWithFieldCheckers(
			PrescriptionEditFragment.class,
			null,
			activityRootData,
			null,
			FieldAccessCheckers.withCheckers(FieldHelper.createSensitiveDataFieldAccessChecker(!activityRootData.isPseudonymized())));
	}

	// Instance methods

	private void setUpFieldVisibilities(FragmentPrescriptionEditLayoutBinding contentBinding) {
		if (record.getId() == null || !ConfigProvider.hasUserRight(UserRight.TREATMENT_CREATE)) {
			contentBinding.prescriptionButtonsPanel.setVisibility(GONE);
		}

		contentBinding.prescriptionPrescriptionType.addValueChangedListener(e -> {
			contentBinding.prescriptionPrescriptionDetails
				.setRequired(e.getValue() == TreatmentType.DRUG_INTAKE || e.getValue() == TreatmentType.OTHER);
		});
	}

	private void setUpControlListeners(FragmentPrescriptionEditLayoutBinding contentBinding) {
		contentBinding.createTreatment.setOnClickListener(e -> {
			TreatmentNewActivity.startActivityFromPrescription(getContext(), getActivityRootData().getUuid());
		});
		contentBinding.prescriptionPrescriptionType.addValueChangedListener(e -> {
			if (e.getValue() == TreatmentType.DRUG_INTAKE) {
				contentBinding.prescriptionPrescriptionDetails
					.setCaption(I18nProperties.getPrefixCaption(PrescriptionDto.I18N_PREFIX, PrescriptionDto.DRUG_INTAKE_DETAILS));
			} else {
				contentBinding.prescriptionPrescriptionDetails
					.setCaption(I18nProperties.getPrefixCaption(PrescriptionDto.I18N_PREFIX, PrescriptionDto.PRESCRIPTION_DETAILS));
			}
		});
	}

	// Overrides

	@Override
	public Prescription getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		record = getActivityRootData();

		prescriptionTypeList = DataUtils.getEnumItems(TreatmentType.class, true);
		treatmentRouteList = DataUtils.getEnumItems(TreatmentRoute.class, true);
	}

	@Override
	public void onLayoutBinding(FragmentPrescriptionEditLayoutBinding contentBinding) {
		setUpControlListeners(contentBinding);

		contentBinding.setData(record);
		contentBinding.setTypeOfDrugClass(TypeOfDrug.class);

		PrescriptionValidator.initializeValidation(contentBinding);
	}

	@Override
	public void onAfterLayoutBinding(FragmentPrescriptionEditLayoutBinding contentBinding) {
		setFieldVisibilitiesAndAccesses(PrescriptionDto.class, contentBinding.mainContent);
		setUpFieldVisibilities(contentBinding);

		// Initialize fields
		contentBinding.prescriptionPrescriptionType.initializeSpinner(prescriptionTypeList);
		contentBinding.prescriptionRoute.initializeSpinner(treatmentRouteList);
		contentBinding.prescriptionPrescriptionDate.initializeDateField(getFragmentManager());
		contentBinding.prescriptionPrescriptionStart.initializeDateField(getFragmentManager());
		contentBinding.prescriptionPrescriptionEnd.initializeDateField(getFragmentManager());
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_prescription_edit_layout;
	}

	@Override
	protected String getSubHeadingTitle() {
		return record.getId() != null
			? getResources().getString(R.string.heading_prescription_edit)
			: getResources().getString(R.string.heading_prescription_new);
	}
}
