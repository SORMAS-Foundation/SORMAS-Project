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

import java.util.List;

import android.content.res.Resources;

import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseEditAuthorization;
import de.symeda.sormas.app.backend.caze.maternalhistory.MaternalHistory;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.core.FieldHelper;
import de.symeda.sormas.app.databinding.FragmentCaseEditMaternalHistoryLayoutBinding;
import de.symeda.sormas.app.util.InfrastructureHelper;

public class CaseEditMaternalHistoryFragment extends BaseEditFragment<FragmentCaseEditMaternalHistoryLayoutBinding, MaternalHistory, Case> {

	public static final String TAG = CaseEditMaternalHistoryFragment.class.getSimpleName();

	private MaternalHistory record;

	// Static methods

	public static CaseEditMaternalHistoryFragment newInstance(Case activityRootData) {
		return newInstanceWithFieldCheckers(
			CaseEditMaternalHistoryFragment.class,
			null,
			activityRootData,
			null,
			FieldAccessCheckers
				.withCheckers(FieldHelper.createSensitiveDataFieldAccessChecker(CaseEditAuthorization.isCaseEditAllowed(activityRootData))));
	}

	// Overrides

	@Override
	protected String getSubHeadingTitle() {
		Resources r = getResources();
		return r.getString(R.string.caption_case_maternal_history);
	}

	@Override
	public MaternalHistory getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		Case caze = getActivityRootData();
		record = caze.getMaternalHistory();
	}

	@Override
	public void onLayoutBinding(final FragmentCaseEditMaternalHistoryLayoutBinding contentBinding) {
		contentBinding.setData(record);
	}

	@Override
	protected void onAfterLayoutBinding(FragmentCaseEditMaternalHistoryLayoutBinding contentBinding) {
		// Initialize ControlDateFields
		contentBinding.maternalHistoryArthralgiaArthritisOnset.initializeDateField(getFragmentManager());
		contentBinding.maternalHistoryConjunctivitisOnset.initializeDateField(getFragmentManager());
		contentBinding.maternalHistoryMaculopapularRashOnset.initializeDateField(getFragmentManager());
		contentBinding.maternalHistoryOtherComplicationsOnset.initializeDateField(getFragmentManager());
		contentBinding.maternalHistoryRubellaOnset.initializeDateField(getFragmentManager());
		contentBinding.maternalHistorySwollenLymphsOnset.initializeDateField(getFragmentManager());
		contentBinding.maternalHistoryRashExposureDate.initializeDateField(getFragmentManager());

		setFieldVisibilitiesAndAccesses(MaternalHistoryDto.class, contentBinding.mainContent);

		List<Item> initialRegions = InfrastructureHelper.loadRegions();
		List<Item> initialDistricts = InfrastructureHelper.loadDistricts(record.getRashExposureRegion());
		List<Item> initialCommunities = InfrastructureHelper.loadCommunities(record.getRashExposureDistrict());
		InfrastructureHelper.initializeRegionFields(
			contentBinding.maternalHistoryRashExposureRegion,
			initialRegions,
			record.getRashExposureRegion(),
			contentBinding.maternalHistoryRashExposureDistrict,
			initialDistricts,
			record.getRashExposureDistrict(),
			contentBinding.maternalHistoryRashExposureCommunity,
			initialCommunities,
			record.getRashExposureCommunity());
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_case_edit_maternal_history_layout;
	}
}
