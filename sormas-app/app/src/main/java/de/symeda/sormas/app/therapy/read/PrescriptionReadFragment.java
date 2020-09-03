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

import android.os.Bundle;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.TreatmentType;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.therapy.Prescription;
import de.symeda.sormas.app.databinding.FragmentPrescriptionReadLayoutBinding;

public class PrescriptionReadFragment extends BaseReadFragment<FragmentPrescriptionReadLayoutBinding, Prescription, Prescription> {

	private Prescription record;

	// Static methods

	public static PrescriptionReadFragment newInstance(Prescription activityRootData) {
		return newInstance(PrescriptionReadFragment.class, null, activityRootData);
	}

	// Instance methods

	private void setUpControlListeners(FragmentPrescriptionReadLayoutBinding contentBinding) {
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
	protected void prepareFragmentData(Bundle savedInstanceState) {
		record = getActivityRootData();
	}

	@Override
	public void onLayoutBinding(FragmentPrescriptionReadLayoutBinding contentBinding) {
		setUpControlListeners(contentBinding);

		contentBinding.setData(record);
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.heading_prescription);
	}

	@Override
	public Prescription getPrimaryData() {
		return record;
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_prescription_read_layout;
	}
}
