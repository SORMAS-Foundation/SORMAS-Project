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

package de.symeda.sormas.app.core.adapter;

import java.util.Date;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import de.symeda.sormas.app.util.DateFormatHelper;

public class DateBindingAdapters {

	@BindingAdapter("android:text")
	public static void setText(TextView textView, Date dataValue) {
		if (dataValue != null) {
			textView.setText(DateFormatHelper.formatLocalDate(dataValue));
		}
	}
}
