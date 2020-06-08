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

package de.symeda.sormas.app.pathogentest.read;

import android.os.Bundle;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.databinding.FragmentPathogenTestReadLayoutBinding;

public class PathogenTestReadFragment extends BaseReadFragment<FragmentPathogenTestReadLayoutBinding, PathogenTest, PathogenTest> {

	private PathogenTest record;

	public static PathogenTestReadFragment newInstance(PathogenTest activityRootData) {
		return newInstance(PathogenTestReadFragment.class, null, activityRootData);
	}

	// Overrides

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {
		record = getActivityRootData();
	}

	@Override
	public void onLayoutBinding(FragmentPathogenTestReadLayoutBinding contentBinding) {
		contentBinding.setData(record);
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.heading_pathogen_test_read);
	}

	@Override
	public PathogenTest getPrimaryData() {
		return record;
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_pathogen_test_read_layout;
	}
}
