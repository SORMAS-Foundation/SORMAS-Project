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

package de.symeda.sormas.app.visit.read;

import java.util.List;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.symptoms.SymptomsReadFragment;
import de.symeda.sormas.app.util.Bundler;
import de.symeda.sormas.app.visit.VisitSection;
import de.symeda.sormas.app.visit.edit.VisitEditActivity;

public class VisitReadActivity extends BaseReadActivity<Visit> {

	public static final String TAG = VisitReadActivity.class.getSimpleName();

	private String contactUuid = null;

	public static void startActivity(Context context, String rootUuid, String contactUuid, VisitSection section) {
		BaseReadActivity.startActivity(context, VisitReadActivity.class, buildBundle(rootUuid, contactUuid, section));
	}

	public static Bundler buildBundle(String rootUuid, String contactUuid, VisitSection section) {
		return buildBundle(rootUuid, section).setContactUuid(contactUuid);
	}

	@Override
	protected void onCreateInner(Bundle savedInstanceState) {
		super.onCreateInner(savedInstanceState);
		contactUuid = new Bundler(savedInstanceState).getContactUuid();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		new Bundler(outState).setContactUuid(contactUuid);
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		return PageMenuItem.fromEnum(VisitSection.values(), getContext());
	}

	@Override
	protected Visit queryRootEntity(String recordUuid) {
		return DatabaseHelper.getVisitDao().queryUuid(recordUuid);
	}

	@Override
	public VisitStatus getPageStatus() {
		return getStoredRootEntity() == null ? null : getStoredRootEntity().getVisitStatus();
	}

	@Override
	protected BaseReadFragment buildReadFragment(PageMenuItem menuItem, Visit activityRootData) {
		VisitSection section = VisitSection.fromOrdinal(menuItem.getPosition());
		BaseReadFragment fragment;
		switch (section) {
		case VISIT_INFO:
			fragment = VisitReadFragment.newInstance(activityRootData);
			break;
		case SYMPTOMS:
			fragment = SymptomsReadFragment.newInstance(activityRootData);
			break;
		default:
			throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
		}
		return fragment;
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_contact_visit;
	}

	@Override
	public void goToEditView() {
		VisitSection section = VisitSection.fromOrdinal(getActivePage().getPosition());
		VisitEditActivity.startActivity(getContext(), getRootUuid(), contactUuid, section);
	}
}
