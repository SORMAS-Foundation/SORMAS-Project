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

package de.symeda.sormas.app.util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import de.symeda.sormas.app.R;

/**
 * Created by Orson on 05/11/2017.
 */
public class ChangeLabelColorOnEditTextFocus implements View.OnFocusChangeListener {

    private Context context;
    private TextView label;

    public ChangeLabelColorOnEditTextFocus(Context context, TextView label) {
        this.context = context;
        this.label = label;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int colorOnFocus = this.context.getResources().getColor(R.color.labelFocus);
        int colorDefault = this.context.getResources().getColor(R.color.controlLabelColor);

        if (v.hasFocus()) {
            this.label.setTextColor(colorOnFocus);
        } else {
            this.label.setTextColor(colorDefault);
        }
    }
}
