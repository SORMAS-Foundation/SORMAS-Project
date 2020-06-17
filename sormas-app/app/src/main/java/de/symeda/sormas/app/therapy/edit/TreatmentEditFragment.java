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
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.therapy.TreatmentRoute;
import de.symeda.sormas.api.therapy.TreatmentType;
import de.symeda.sormas.api.therapy.TypeOfDrug;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.therapy.Treatment;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.core.FieldHelper;
import de.symeda.sormas.app.databinding.FragmentTreatmentEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

public class TreatmentEditFragment extends BaseEditFragment<FragmentTreatmentEditLayoutBinding, Treatment, Treatment> {

	public static final String TAG = PrescriptionEditFragment.class.getSimpleName();

	private Treatment record;

	// Enum lists

	private List<Item> treatmentTypeList;
	private List<Item> treatmentRouteList;

	// Static methods

	public static TreatmentEditFragment newInstance(Treatment activityRootData) {
		return newInstanceWithFieldCheckers(
			TreatmentEditFragment.class,
			null,
			activityRootData,
			null,
			FieldAccessCheckers.withCheckers(FieldHelper.createSensitiveDataFieldAccessChecker(!activityRootData.isPseudonymized())));
	}
	// Instance methods

	private void setUpFieldVisibilities(FragmentTreatmentEditLayoutBinding contentBinding) {
		if (record.getId() == null || record.getPrescription() == null) {
			contentBinding.treatmentButtonsPanel.setVisibility(GONE);
		}

		contentBinding.treatmentTreatmentType.addValueChangedListener(e -> {
			contentBinding.treatmentTreatmentDetails.setRequired(e.getValue() == TreatmentType.DRUG_INTAKE || e.getValue() == TreatmentType.OTHER);
		});
	}

	private void setUpControlListeners(FragmentTreatmentEditLayoutBinding contentBinding) {
		contentBinding.openPrescription.setOnClickListener(e -> {
			if (getActivityRootData().getPrescription() != null) {
				PrescriptionEditActivity.startActivity(getContext(), getActivityRootData().getPrescription().getUuid(), true);
			}
		});
		contentBinding.treatmentTreatmentType.addValueChangedListener(e -> {
			if (e.getValue() == TreatmentType.DRUG_INTAKE) {
				contentBinding.treatmentTreatmentDetails
					.setCaption(I18nProperties.getPrefixCaption(TreatmentDto.I18N_PREFIX, TreatmentDto.DRUG_INTAKE_DETAILS));
			} else {
				contentBinding.treatmentTreatmentDetails
					.setCaption(I18nProperties.getPrefixCaption(TreatmentDto.I18N_PREFIX, TreatmentDto.TREATMENT_DETAILS));
			}
		});
	}

	// Overrides

	@Override
	public Treatment getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		record = getActivityRootData();

		treatmentTypeList = DataUtils.getEnumItems(TreatmentType.class, true);
		treatmentRouteList = DataUtils.getEnumItems(TreatmentRoute.class, true);
	}

	@Override
	public void onLayoutBinding(FragmentTreatmentEditLayoutBinding contentBinding) {
		setUpControlListeners(contentBinding);

		contentBinding.setData(record);
		contentBinding.setTypeOfDrugClass(TypeOfDrug.class);
	}

	@Override
	public void onAfterLayoutBinding(FragmentTreatmentEditLayoutBinding contentBinding) {
		setFieldVisibilitiesAndAccesses(TreatmentDto.class, contentBinding.mainContent);

		setUpFieldVisibilities(contentBinding);


		// Initialize fields
		contentBinding.treatmentTreatmentType.initializeSpinner(treatmentTypeList);
		contentBinding.treatmentRoute.initializeSpinner(treatmentRouteList);
		contentBinding.treatmentTreatmentDateTime.initializeDateTimeField(getFragmentManager());
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_treatment_edit_layout;
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.heading_treatment_edit);
	}
}
