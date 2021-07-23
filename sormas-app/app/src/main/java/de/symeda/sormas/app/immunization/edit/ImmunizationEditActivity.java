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

package de.symeda.sormas.app.immunization.edit;

import android.content.Context;

import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.backend.immunization.Immunization;
import de.symeda.sormas.app.component.menu.PageMenuItem;

public class ImmunizationEditActivity extends BaseEditActivity<Immunization> {

    public static void startActivity(Context context, String rootUuid) {
        BaseEditActivity.startActivity(context, ImmunizationEditActivity.class, buildBundle(rootUuid));
    }

    @Override
    protected Immunization queryRootEntity(String recordUuid) {
		return null;
    }

    @Override
    protected Immunization buildRootEntity() {
        return null;
    }

    @Override
    protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Immunization activityRootData) {
        return null;
    }

    @Override
    public void saveData() {

    }

    @Override
    public Enum getPageStatus() {
        return null;
    }

    @Override
    protected int getActivityTitle() {
        return 0;
    }
}
