/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.environmentsample.read;

import java.util.List;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.environment.environmentsample.EnvironmentSample;
import de.symeda.sormas.app.backend.environment.environmentsample.EnvironmentSampleEditAuthorization;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.environmentsample.EnvironmentSampleSection;
import de.symeda.sormas.app.sample.ShipmentStatus;
import de.symeda.sormas.app.util.Bundler;

public class EnvironmentSampleReadActivity extends BaseReadActivity<EnvironmentSample> {

	public static void startActivity(Context context, String rootUuid) {
		BaseReadActivity.startActivity(context, EnvironmentSampleReadActivity.class, buildBundle(rootUuid));
	}

	public static Bundler buildBundle(String rootUuid) {
		return BaseReadActivity.buildBundle(rootUuid, 0);
	}

	@Override
	protected EnvironmentSample queryRootEntity(String recordUuid) {
		return DatabaseHelper.getEnvironmentSampleDao().queryUuid(recordUuid);
	}

	@Override
	public ShipmentStatus getPageStatus() {
		EnvironmentSample sample = getStoredRootEntity();
		return sample != null
			? sample.isReceived() ? ShipmentStatus.RECEIVED : sample.isDispatched() ? ShipmentStatus.SHIPPED : ShipmentStatus.NOT_SHIPPED
			: null;
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		return PageMenuItem.fromEnum(EnvironmentSampleSection.values(), getContext());
	}

	@Override
	protected BaseReadFragment buildReadFragment(PageMenuItem menuItem, EnvironmentSample activityRootData) {
		EnvironmentSampleSection section = EnvironmentSampleSection.fromOrdinal(menuItem.getPosition());
		BaseReadFragment fragment;
		switch (section) {
		case ENVIRONMENT_SAMPLE_INFO:
			fragment = EnvironmentSampleReadFragment.newInstance(activityRootData);
			break;
		default:
			throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
		}
		return fragment;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		getEditMenu().setTitle(R.string.action_edit_environment_sample);
		return result;
	}

	@Override
	protected void processActionbarMenu() {

		super.processActionbarMenu();
		final MenuItem editMenu = getEditMenu();
		final ReferenceDto referenceDto = new EnvironmentSampleReferenceDto(getRootUuid());
		final EnvironmentSample selectedSample = DatabaseHelper.getEnvironmentSampleDao().getByReferenceDto(referenceDto);

		if (editMenu != null) {
			if (ConfigProvider.hasUserRight(UserRight.ENVIRONMENT_SAMPLE_EDIT)
				&& EnvironmentSampleEditAuthorization.isEnvironmentSampleEditAllowed(selectedSample)) {
				editMenu.setVisible(true);
			} else {
				editMenu.setVisible(false);
			}
		}
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_environment_sample_read;
	}

	@Override
	public void goToEditView() {
		EnvironmentSampleSection section = EnvironmentSampleSection.fromOrdinal(getActivePage().getPosition());
		//EnvironmentSampleEditActivity.startActivity(getContext(), getRootUuid(), section);
	}
}
