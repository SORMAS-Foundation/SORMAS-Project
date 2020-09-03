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

package de.symeda.sormas.app.clinicalcourse.read;

import java.util.List;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.clinicalcourse.ClinicalVisit;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.clinicalcourse.ClinicalVisitSection;
import de.symeda.sormas.app.clinicalcourse.edit.ClinicalVisitEditActivity;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.symptoms.SymptomsReadFragment;
import de.symeda.sormas.app.util.Bundler;

public class ClinicalVisitReadActivity extends BaseReadActivity<ClinicalVisit> {

	public static final String TAG = ClinicalVisitReadActivity.class.getSimpleName();

	private String caseUuid;

	public static void startActivity(Context context, String rootUuid, String caseUuid, ClinicalVisitSection section) {
		BaseReadActivity.startActivity(context, ClinicalVisitReadActivity.class, buildBundle(rootUuid, section).setCaseUuid(caseUuid));
	}

	@Override
	protected ClinicalVisit queryRootEntity(String recordUuid) {
		return DatabaseHelper.getClinicalVisitDao().queryUuid(recordUuid);
	}

	@Override
	public void onCreateInner(@Nullable Bundle savedInstanceState) {
		super.onCreateInner(savedInstanceState);
		caseUuid = new Bundler(savedInstanceState).getCaseUuid();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		new Bundler(outState).setCaseUuid(caseUuid);
	}

	@Override
	public Enum getPageStatus() {
		return null;
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		return PageMenuItem.fromEnum(ClinicalVisitSection.values(), getContext());
	}

	@Override
	protected BaseReadFragment buildReadFragment(PageMenuItem menuItem, ClinicalVisit activityRootData) {
		ClinicalVisitSection section = ClinicalVisitSection.fromOrdinal(menuItem.getPosition());
		BaseReadFragment fragment;
		switch (section) {
		case VISIT_INFO:
			fragment = ClinicalVisitReadFragment.newInstance(activityRootData);
			break;
		case CLINICAL_MEASUREMENTS:
			fragment = ClinicalMeasurementsReadFragment.newInstance(activityRootData.getSymptoms());
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
		return R.string.heading_clinical_visit;
	}

	@Override
	public void goToEditView() {
		ClinicalVisitSection section = ClinicalVisitSection.fromOrdinal(getActivePage().getPosition());
		ClinicalVisitEditActivity.startActivity(getContext(), getRootUuid(), caseUuid, section);
	}
}
