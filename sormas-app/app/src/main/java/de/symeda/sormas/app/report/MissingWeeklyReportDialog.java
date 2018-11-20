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
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.dialog.AbstractDialog;
import de.symeda.sormas.app.core.Callback;

public class MissingWeeklyReportDialog extends AbstractDialog {

    public static final String TAG = MissingWeeklyReportDialog.class.getSimpleName();

    // Constructor

    public MissingWeeklyReportDialog(final FragmentActivity activity) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_missing_weekly_report_layout,
                R.layout.dialog_root_two_button_panel_layout,
                R.string.heading_missing_weekly_report_dialog, R.string.alert_missing_report);
    }

    // Overrides

    @Override
    protected void setContentBinding(Context context, ViewDataBinding binding, String layoutName) {
        // Nothing to bind
    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding buttonPanelBinding) {
        // Nothing to initialize
    }

    @Override
    public boolean isHeadingCentered() {
        return true;
    }

    @Override
    public boolean isRounded() {
        return true;
    }

    @Override
    public ControlButtonType getNegativeButtonType() {
        return ControlButtonType.LINE_DANGER;
    }

    @Override
    public int getPositiveButtonText() {
        return R.string.action_open_reports;
    }

}
