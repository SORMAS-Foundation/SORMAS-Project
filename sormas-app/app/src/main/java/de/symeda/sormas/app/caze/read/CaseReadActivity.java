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

import java.util.List;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseEditAuthorization;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.caze.CaseSection;
import de.symeda.sormas.app.caze.edit.CaseEditActivity;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.epidata.EpidemiologicalDataReadFragment;
import de.symeda.sormas.app.person.read.PersonReadFragment;
import de.symeda.sormas.app.symptoms.SymptomsReadFragment;
import de.symeda.sormas.app.util.Bundler;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;

public class CaseReadActivity extends BaseReadActivity<Case> {

	public static final String TAG = CaseReadActivity.class.getSimpleName();

	public static void startActivity(Context context, String rootUuid, boolean finishInsteadOfUpNav) {
		BaseActivity.startActivity(context, CaseReadActivity.class, buildBundle(rootUuid, finishInsteadOfUpNav));
	}

	public static Bundler buildBundle(String rootUuid, boolean finishInsteadOfUpNav) {
		return BaseReadActivity.buildBundle(rootUuid, finishInsteadOfUpNav);
	}

	@Override
	public CaseClassification getPageStatus() {
		return getStoredRootEntity() == null ? null : getStoredRootEntity().getCaseClassification();
	}

	@Override
	protected Case queryRootEntity(String recordUuid) {
		return DatabaseHelper.getCaseDao().queryUuidWithEmbedded(recordUuid);
	}

	@Override
	public List<PageMenuItem> getPageMenuData() {
		List<PageMenuItem> menuItems = PageMenuItem.fromEnum(CaseSection.values(), getContext());
		Case caze = getStoredRootEntity();
		// Sections must be removed in reverse order
		if (!ConfigProvider.hasUserRight(UserRight.CLINICAL_COURSE_VIEW)
			|| (caze != null && caze.isUnreferredPortHealthCase())
			|| (caze != null && caze.getClinicalCourse() == null)) {
			menuItems.set(CaseSection.CLINICAL_VISITS.ordinal(), null);
			menuItems.set(CaseSection.HEALTH_CONDITIONS.ordinal(), null);
		}
		if (!ConfigProvider.hasUserRight(UserRight.THERAPY_VIEW)
			|| (caze != null && caze.isUnreferredPortHealthCase())
			|| (caze != null && caze.getTherapy() == null)) {
			menuItems.set(CaseSection.TREATMENTS.ordinal(), null);
			menuItems.set(CaseSection.PRESCRIPTIONS.ordinal(), null);
		}
		if (caze != null && caze.isUnreferredPortHealthCase()) {
			menuItems.set(CaseSection.SAMPLES.ordinal(), null);
		}
		if (!ConfigProvider.hasUserRight(UserRight.CONTACT_VIEW)
			|| (caze != null && caze.isUnreferredPortHealthCase())
			|| (caze != null && !DiseaseConfigurationCache.getInstance().hasFollowUp(caze.getDisease()))) {
			menuItems.set(CaseSection.CONTACTS.ordinal(), null);
		}
		if (caze != null && caze.getDisease() == Disease.CONGENITAL_RUBELLA) {
			menuItems.set(CaseSection.EPIDEMIOLOGICAL_DATA.ordinal(), null);
		}
		if (caze != null && (caze.getCaseOrigin() != CaseOrigin.POINT_OF_ENTRY || !ConfigProvider.hasUserRight(UserRight.PORT_HEALTH_INFO_VIEW))) {
			menuItems.set(CaseSection.PORT_HEALTH_INFO.ordinal(), null);
		}
		if (caze != null && (caze.isUnreferredPortHealthCase() || UserRole.isPortHealthUser(ConfigProvider.getUser().getUserRoles()))) {
			menuItems.set(CaseSection.HOSPITALIZATION.ordinal(), null);
		}
		if (caze != null && caze.getDisease() != Disease.CONGENITAL_RUBELLA) {
			menuItems.set(CaseSection.MATERNAL_HISTORY.ordinal(), null);
		}

		return menuItems;
	}

	@Override
	protected BaseReadFragment buildReadFragment(PageMenuItem menuItem, Case activityRootData) {
		CaseSection section = CaseSection.fromOrdinal(menuItem.getPosition());
		BaseReadFragment fragment;
		switch (section) {
		case CASE_INFO:
			fragment = CaseReadFragment.newInstance(activityRootData);
			break;
		case PERSON_INFO:
			fragment = PersonReadFragment.newInstance(activityRootData);
			break;
		case MATERNAL_HISTORY:
			fragment = CaseReadMaternalHistoryFragment.newInstance(activityRootData);
			break;
		case HOSPITALIZATION:
			fragment = CaseReadHospitalizationFragment.newInstance(activityRootData);
			break;
		case PORT_HEALTH_INFO:
			fragment = CaseReadPortHealthInfoFragment.newInstance(activityRootData);
			break;
		case SYMPTOMS:
			fragment = SymptomsReadFragment.newInstance(activityRootData);
			break;
		case EPIDEMIOLOGICAL_DATA:
			fragment = EpidemiologicalDataReadFragment.newInstance(activityRootData);
			break;
		case CONTACTS:
			fragment = CaseReadContactListFragment.newInstance(activityRootData);
			break;
		case SAMPLES:
			fragment = CaseReadSampleListFragment.newInstance(activityRootData);
			break;
		case PRESCRIPTIONS:
			fragment = CaseReadPrescriptionListFragment.newInstance(activityRootData);
			break;
		case TREATMENTS:
			fragment = CaseReadTreatmentListFragment.newInstance(activityRootData);
			break;
		case HEALTH_CONDITIONS:
			fragment = CaseReadHealthConditionsFragment.newInstance(activityRootData);
			break;
		case CLINICAL_VISITS:
			fragment = CaseReadClinicalVisitListFragment.newInstance(activityRootData);
			break;
		case TASKS:
			fragment = CaseReadTaskListFragment.newInstance(activityRootData);
			break;
		default:
			throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
		}

		return fragment;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);

		getEditMenu().setTitle(R.string.action_edit_case);

		return result;
	}

	@Override
	protected void processActionbarMenu() {
		super.processActionbarMenu();
		final Case selectedCase = DatabaseHelper.getCaseDao().queryUuidBasic(getRootUuid());
		final MenuItem editMenu = getEditMenu();

		if (editMenu != null) {
			if (CaseEditAuthorization.isCaseEditAllowed(selectedCase)
				|| (getActiveFragment() != null && getActiveFragment() instanceof CaseReadContactListFragment)) {
				editMenu.setVisible(true);
			} else {
				editMenu.setVisible(false);
			}
		}
	}

	@Override
	protected int getActivityTitle() {
		return R.string.heading_case_read;
	}

	@Override
	public void goToEditView() {
		CaseSection section = CaseSection.fromOrdinal(getActivePage().getPosition());

		CaseEditActivity.startActivity(CaseReadActivity.this, getRootUuid(), section);
	}
}
