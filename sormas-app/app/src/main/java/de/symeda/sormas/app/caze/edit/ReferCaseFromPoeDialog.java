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

package de.symeda.sormas.app.caze.edit;

import androidx.fragment.app.FragmentActivity;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;

public class ReferCaseFromPoeDialog extends MoveCaseDialog {

    public static final String TAG = ReferCaseFromPoeDialog.class.getSimpleName();

    ReferCaseFromPoeDialog(final FragmentActivity activity, Case caze) {
        super(activity, caze, true, R.string.heading_refer_case_from_poe);
    }

    @Override
    public int getPositiveButtonText() {
        return R.string.action_refer_case_from_poe;
    }

    @Override
    public int getErrorMessageText() {
        return R.string.error_case_referring_from_poe;
    }

}