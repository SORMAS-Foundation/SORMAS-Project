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

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.maternalhistory.MaternalHistory;
import de.symeda.sormas.app.databinding.FragmentCaseReadMaternalHistoryLayoutBinding;

public class CaseReadMaternalHistoryFragment extends BaseReadFragment<FragmentCaseReadMaternalHistoryLayoutBinding, MaternalHistory, Case> {

	public static final String TAG = CaseReadMaternalHistoryFragment.class.getSimpleName();

	private MaternalHistory record;

	// Static methods

	public static CaseReadMaternalHistoryFragment newInstance(Case activityRootData) {
		return newInstance(CaseReadMaternalHistoryFragment.class, null, activityRootData);
	}

	// Overrides

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {
		Case caze = getActivityRootData();
		record = caze.getMaternalHistory();
	}

	@Override
	public void onLayoutBinding(FragmentCaseReadMaternalHistoryLayoutBinding contentBinding) {
		contentBinding.setData(record);
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_case_maternal_history);
	}

	@Override
	public MaternalHistory getPrimaryData() {
		return record;
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_case_read_maternal_history_layout;
	}
}
