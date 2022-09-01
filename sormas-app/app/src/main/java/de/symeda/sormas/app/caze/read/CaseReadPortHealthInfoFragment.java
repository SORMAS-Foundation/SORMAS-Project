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

package de.symeda.sormas.app.caze.read;

import android.os.Bundle;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.porthealthinfo.PortHealthInfo;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.databinding.FragmentCaseReadPortHealthInfoLayoutBinding;

public class CaseReadPortHealthInfoFragment extends BaseReadFragment<FragmentCaseReadPortHealthInfoLayoutBinding, PortHealthInfo, Case> {

	public static final String TAG = CaseReadPortHealthInfoFragment.class.getSimpleName();

	private PortHealthInfo record;
	private Case caze;

	// Static methods

	public static CaseReadPortHealthInfoFragment newInstance(Case activityRootData) {
		return newInstance(CaseReadPortHealthInfoFragment.class, null, activityRootData);
	}

	// Overrides

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {
		caze = getActivityRootData();
		record = caze.getPortHealthInfo();
	}

	@Override
	public void onLayoutBinding(FragmentCaseReadPortHealthInfoLayoutBinding contentBinding) {
		contentBinding.setData(record);
		contentBinding.setPointOfEntry(caze.getPointOfEntry());
		contentBinding.setPointOfEntryDetails(caze.getPointOfEntryDetails());
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_case_port_health_info);
	}

	@Override
	public PortHealthInfo getPrimaryData() {
		return record;
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_case_read_port_health_info_layout;
	}

	@Override
	public boolean showEditAction() {
		return ConfigProvider.hasUserRight(UserRight.CASE_EDIT) && ConfigProvider.hasUserRight(UserRight.PORT_HEALTH_INFO_EDIT);
	}
}
