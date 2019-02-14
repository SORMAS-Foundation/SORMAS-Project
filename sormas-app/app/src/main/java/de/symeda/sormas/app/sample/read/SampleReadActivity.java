/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.sample.read;

import android.content.Context;
import android.view.Menu;

import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.sample.ShipmentStatus;
import de.symeda.sormas.app.sample.edit.SampleEditActivity;

public class SampleReadActivity extends BaseReadActivity<Sample> {

    public static void startActivity(Context context, String rootUuid) {
        BaseReadActivity.startActivity(context, SampleReadActivity.class, buildBundle(rootUuid));
    }

    @Override
    protected Sample queryRootEntity(String recordUuid) {
        return DatabaseHelper.getSampleDao().queryUuid(recordUuid);
    }

    @Override
    public ShipmentStatus getPageStatus() {
        Sample sample = getStoredRootEntity();
        if (sample != null) {
            ShipmentStatus shipmentStatus = sample.getReferredToUuid() != null ?
                    ShipmentStatus.REFERRED_OTHER_LAB : sample.isReceived() ?
                    ShipmentStatus.RECEIVED : sample.isShipped() ? ShipmentStatus.SHIPPED :
                    ShipmentStatus.NOT_SHIPPED;
            return shipmentStatus;
        } else {
            return null;
        }
    }

    @Override
    protected BaseReadFragment buildReadFragment(PageMenuItem menuItem, Sample activityRootData) {
        return SampleReadFragment.newInstance(activityRootData);
    }

    @Override
    public PageMenuItem getActivePage() {
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getEditMenu().setTitle(R.string.action_edit_sample);
        return result;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_sample_read;
    }

    @Override
    public void goToEditView() {
        SampleEditActivity.startActivity(getContext(), getRootUuid());
    }
}
