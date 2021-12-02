/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.immunization.vaccination;

import java.util.List;

import android.content.Context;
import android.view.Menu;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.vaccination.Vaccination;
import de.symeda.sormas.app.component.menu.PageMenuItem;

public class VaccinationReadActivity extends BaseReadActivity<Vaccination> {

	public static void startActivity(Context context, String rootUuid) {
		BaseReadActivity.startActivity(context, VaccinationReadActivity.class, buildBundle(rootUuid));
	}

	public static void startActivity(Context context, String rootUuid, boolean finishInsteadOfUpNav) {
		BaseReadActivity.startActivity(context, VaccinationReadActivity.class, buildBundle(rootUuid, finishInsteadOfUpNav));
	}

	@Override
	protected Vaccination queryRootEntity(String recordUuid) {
		return DatabaseHelper.getVaccinationDao().queryUuid(recordUuid);
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		List<PageMenuItem> menuItems = PageMenuItem.fromEnum(VaccinationSection.values(), getContext());
		if (!ConfigProvider.hasUserRight(UserRight.IMMUNIZATION_VIEW)
			|| DatabaseHelper.getFeatureConfigurationDao().isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {
			menuItems.set(VaccinationSection.HEALTH_CONDITIONS.ordinal(), null);
		}
		return menuItems;
	}

	@Override
	protected BaseReadFragment buildReadFragment(PageMenuItem menuItem, Vaccination activityRootData) {
		final VaccinationSection section = VaccinationSection.fromOrdinal(menuItem.getPosition());
		BaseReadFragment fragment;
		switch (section) {
		case VACCINATION_INFO:
			fragment = VaccinationReadFragment.newInstance(activityRootData);
			break;
		case HEALTH_CONDITIONS:
			fragment = VaccinationReadHealthConditionsFragment.newInstance(activityRootData);
			break;
		default:
			throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
		}

		return fragment;
	}

	@Override
	public void goToEditView() {
		VaccinationEditActivity.startActivity(this, getRootUuid());
	}

	@Override
	public Enum getPageStatus() {
		return null;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_vaccination;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		getEditMenu().setTitle(R.string.action_edit_vaccination);
		return result;
	}

}
