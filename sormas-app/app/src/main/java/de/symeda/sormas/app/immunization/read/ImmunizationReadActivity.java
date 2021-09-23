/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.immunization.read;

import android.content.Context;
import android.view.MenuItem;

import java.util.List;

import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.immunization.Immunization;
import de.symeda.sormas.app.backend.immunization.ImmunizationEditAuthorization;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.immunization.ImmunizationSection;
import de.symeda.sormas.app.immunization.edit.ImmunizationEditActivity;
import de.symeda.sormas.app.person.read.PersonReadFragment;
import de.symeda.sormas.app.util.Bundler;

public class ImmunizationReadActivity extends BaseReadActivity<Immunization> {

    public static void startActivity(Context context, String rootUuid, boolean finishInsteadOfUpNav) {
        BaseActivity.startActivity(context, ImmunizationReadActivity.class, buildBundle(rootUuid, finishInsteadOfUpNav));
    }

    public static void startActivity(Context context, String rootUuid) {
        BaseReadActivity.startActivity(context, ImmunizationReadActivity.class, buildBundle(rootUuid));
    }

    public static Bundler buildBundle(String rootUuid) {
        return BaseReadActivity.buildBundle(rootUuid);
    }

    @Override
    protected Immunization queryRootEntity(String recordUuid) {
        return DatabaseHelper.getImmunizationDao().queryUuid(recordUuid);
    }

    @Override
    public void goToEditView() {
        final ImmunizationSection section = ImmunizationSection.fromOrdinal(getActivePage().getPosition());
        ImmunizationEditActivity.startActivity(getContext(), getRootUuid(), section);
    }

    @Override
    protected BaseReadFragment buildReadFragment(PageMenuItem menuItem, Immunization activityRootData) {
        final ImmunizationSection section = ImmunizationSection.fromOrdinal(menuItem.getPosition());
        BaseReadFragment fragment;
        switch (section) {
            case IMMUNIZATION_INFO:
                fragment = ImmunizationReadFragment.newInstance(activityRootData);
                break;
            case PERSON_INFO:
                fragment = PersonReadFragment.newInstance(activityRootData);
                break;
            case VACCINATIONS:
                fragment = ImmunizationReadVaccinationListFragment.newInstance(activityRootData);
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
	public List<PageMenuItem> getPageMenuData() {
		final Immunization storedRootEntity = this.getStoredRootEntity();
		if (storedRootEntity != null) {
			MeansOfImmunization meansOfImmunization = storedRootEntity.getMeansOfImmunization();
			if (meansOfImmunization == MeansOfImmunization.VACCINATION || meansOfImmunization == MeansOfImmunization.VACCINATION_RECOVERY) {
				return PageMenuItem.fromEnum(ImmunizationSection.values(), getContext());
			} else {
				return PageMenuItem.fromEnum(getContext(), ImmunizationSection.IMMUNIZATION_INFO, ImmunizationSection.PERSON_INFO);
			}
		} else {
			return PageMenuItem.fromEnum(ImmunizationSection.values(), getContext());
		}
	}

    @Override
    protected int getActivityTitle() {
        return R.string.heading_immunization_read;
    }

    @Override
    protected void processActionbarMenu() {
        super.processActionbarMenu();
        final Immunization selectedImmunization = DatabaseHelper.getImmunizationDao().queryUuid(getRootUuid());
        final MenuItem editMenu = getEditMenu();

        if (editMenu != null) {
            if (ImmunizationEditAuthorization.isImmunizationEditAllowed(selectedImmunization)) {
                editMenu.setVisible(true);
            } else {
                editMenu.setVisible(false);
            }
        }
    }
}
