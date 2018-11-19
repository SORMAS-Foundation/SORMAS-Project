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

package de.symeda.sormas.app.report;

import android.content.Context;

import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseReportActivity;
import de.symeda.sormas.app.BaseReportFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.caze.read.CaseReadActivity;

public class ReportActivity extends BaseReportActivity {

    private final static String TAG = ReportActivity.class.getSimpleName();

    public static void startActivity(Context context) {
        BaseActivity.startActivity(context, ReportActivity.class, buildBundle(0));
    }

    @Override
    public BaseReportFragment buildReportFragment() {
        return ReportFragment.newInstance();
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
}
