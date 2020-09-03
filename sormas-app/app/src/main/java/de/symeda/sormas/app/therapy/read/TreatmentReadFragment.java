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

package de.symeda.sormas.app.therapy.read;

import static android.view.View.GONE;

import android.os.Bundle;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.therapy.TreatmentType;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.therapy.Treatment;
import de.symeda.sormas.app.databinding.FragmentTreatmentReadLayoutBinding;

public class TreatmentReadFragment extends BaseReadFragment<FragmentTreatmentReadLayoutBinding, Treatment, Treatment> {

	private Treatment record;

	// Static methods

	public static TreatmentReadFragment newInstance(Treatment activityRootData) {
		return newInstance(TreatmentReadFragment.class, null, activityRootData);
	}

	// Instance methods

	private void setUpFieldVisibilities(FragmentTreatmentReadLayoutBinding contentBinding) {
		if (record.getPrescription() == null) {
			contentBinding.treatmentButtonsPanel.setVisibility(GONE);
		}
	}

	private void setUpControlListeners(FragmentTreatmentReadLayoutBinding contentBinding) {
		contentBinding.openPrescription.setOnClickListener(e -> {
			if (getActivityRootData().getPrescription() != null) {
				PrescriptionReadActivity.startActivity(getContext(), getActivityRootData().getPrescription().getUuid(), true);
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
	protected void prepareFragmentData(Bundle savedInstanceState) {
		record = getActivityRootData();
	}

	@Override
	public void onLayoutBinding(FragmentTreatmentReadLayoutBinding contentBinding) {
		setUpControlListeners(contentBinding);

		contentBinding.setData(record);
	}

	@Override
	public void onAfterLayoutBinding(FragmentTreatmentReadLayoutBinding contentBinding) {
		setUpFieldVisibilities(contentBinding);
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.heading_treatment);
	}

	@Override
	public Treatment getPrimaryData() {
		return record;
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_treatment_read_layout;
	}
}
