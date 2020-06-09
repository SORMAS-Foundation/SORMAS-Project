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

import android.content.Context;

import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.therapy.Treatment;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.therapy.edit.TreatmentEditActivity;

public class TreatmentReadActivity extends BaseReadActivity<Treatment> {

	public static void startActivity(Context context, String rootUuid) {
		BaseReadActivity.startActivity(context, TreatmentReadActivity.class, buildBundle(rootUuid));
	}

	@Override
	protected Treatment queryRootEntity(String recordUuid) {
		return DatabaseHelper.getTreatmentDao().queryUuid(recordUuid);
	}

	@Override
	protected BaseReadFragment buildReadFragment(PageMenuItem menuItem, Treatment activityRootData) {
		return TreatmentReadFragment.newInstance(activityRootData);
	}

	@Override
	public void goToEditView() {
		TreatmentEditActivity.startActivity(this, getRootUuid());
	}

	@Override
	public Enum getPageStatus() {
		return null;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_treatment;
	}
}
