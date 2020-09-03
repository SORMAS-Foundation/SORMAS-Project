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

package de.symeda.sormas.app.caze.edit;

import android.content.res.Resources;

import de.symeda.sormas.api.caze.porthealthinfo.ConveyanceType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.porthealthinfo.PortHealthInfo;
import de.symeda.sormas.app.databinding.FragmentCaseEditPortHealthInfoLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

public class CaseEditPortHealthInfoFragment extends BaseEditFragment<FragmentCaseEditPortHealthInfoLayoutBinding, PortHealthInfo, Case> {

	public static final String TAG = CaseEditPortHealthInfoFragment.class.getSimpleName();

	private PortHealthInfo record;
	private Case caze;

	// Static methods

	public static CaseEditPortHealthInfoFragment newInstance(Case activityRootData) {
		return newInstance(CaseEditPortHealthInfoFragment.class, null, activityRootData);
	}

	// Overrides

	@Override
	protected String getSubHeadingTitle() {
		Resources r = getResources();
		return r.getString(R.string.caption_case_port_health_info);
	}

	@Override
	public PortHealthInfo getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		caze = getActivityRootData();
		record = caze.getPortHealthInfo();
	}

	@Override
	public void onLayoutBinding(final FragmentCaseEditPortHealthInfoLayoutBinding contentBinding) {
		contentBinding.setData(record);
		contentBinding.setPointOfEntry(caze.getPointOfEntry());
		contentBinding.setPointOfEntryDetails(caze.getPointOfEntryDetails());

		CaseValidator.initializePortHealthInfoValidation(contentBinding, caze);
	}

	@Override
	protected void onAfterLayoutBinding(FragmentCaseEditPortHealthInfoLayoutBinding contentBinding) {
		// Initialize ControlDateFields
		contentBinding.portHealthInfoArrivalDateTime.initializeDateTimeField(getFragmentManager());
		contentBinding.portHealthInfoDepartureDateTime.initializeDateTimeField(getFragmentManager());

		// Initialize ControlSpinnerFields
		contentBinding.portHealthInfoConveyanceType.initializeSpinner(DataUtils.getEnumItems(ConveyanceType.class));
		contentBinding.portHealthInfoNumberOfTransitStops.initializeSpinner(DataUtils.toItems(DataHelper.buildIntegerList(0, 5)));
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_case_edit_port_health_info_layout;
	}
}
