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

package de.symeda.sormas.app.report;

import java.util.List;

import android.content.Context;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseReportActivity;
import de.symeda.sormas.app.BaseReportFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.menu.PageMenuItem;

public class ReportActivity extends BaseReportActivity {

	private final static String TAG = ReportActivity.class.getSimpleName();

	public EpiWeek epiWeek = null;

	public static void startActivity(Context context) {
		BaseActivity.startActivity(context, ReportActivity.class, buildBundle(0));
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		List<PageMenuItem> menuItems = PageMenuItem.fromEnum(ReportSection.values(), getContext());
		if (!(ConfigProvider.getUser().hasUserRole(UserRole.SURVEILLANCE_OFFICER))) {
			menuItems.set(ReportSection.INFORMANT_REPORTS.ordinal(), null);
			setPageMenuVisibility(false);
		}

		return menuItems;
	}

	@Override
	public void onResume() { // public, so the ReportFragment can call it to reload data
		super.onResume();
	}

	@Override
	public BaseReportFragment buildReportFragment(PageMenuItem menuItem) {
		ReportSection section = ReportSection.fromOrdinal(menuItem.getPosition());
		BaseReportFragment fragment;
		switch (section) {
		case MY_REPORTS:
			fragment = ReportFragment.newInstance();
			break;
		case INFORMANT_REPORTS:
			fragment = ReportOverviewFragment.newInstance();
			break;
		default:
			throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
		}

		return fragment;
	}

	@Override
	public Enum getPageStatus() {
		return null;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.main_menu_reports;
	}

	@Override
	protected boolean showTitleBar() {
		return true;
	}

	public EpiWeek getEpiWeek() {
		return epiWeek;
	}

	public void setEpiWeek(EpiWeek epiWeek) {
		this.epiWeek = epiWeek;
	}
}
