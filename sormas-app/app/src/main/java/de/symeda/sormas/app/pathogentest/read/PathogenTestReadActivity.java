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

import android.content.Context;
import android.view.Menu;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.pathogentest.edit.PathogenTestEditActivity;

public class PathogenTestReadActivity extends BaseReadActivity<PathogenTest> {

	public static final String TAG = PathogenTestReadActivity.class.getSimpleName();

	public static void startActivity(Context context, String rootUuid) {
		BaseReadActivity.startActivity(context, PathogenTestReadActivity.class, buildBundle(rootUuid));
	}

	@Override
	protected PathogenTest queryRootEntity(String recordUuid) {
		PathogenTest _pathogenTest = DatabaseHelper.getSampleTestDao().queryUuid(recordUuid);
		return _pathogenTest;
	}

	@Override
	public Enum getPageStatus() {
		return null;
	}

	@Override
	protected BaseReadFragment buildReadFragment(PageMenuItem menuItem, PathogenTest activityRootData) {
		return PathogenTestReadFragment.newInstance(activityRootData);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getEditMenu().setTitle(R.string.action_edit_pathogen_test);
		getEditMenu().setVisible(ConfigProvider.hasUserRight(UserRight.PATHOGEN_TEST_EDIT));

		return true;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_pathogen_test_read;
	}

	@Override
	public void goToEditView() {
		PathogenTestEditActivity.startActivity(getContext(), getRootUuid());
	}
}
