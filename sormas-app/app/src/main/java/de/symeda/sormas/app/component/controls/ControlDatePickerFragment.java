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

package de.symeda.sormas.app.component.controls;

import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import de.symeda.sormas.app.R;

public class ControlDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

	// Constants

	public static final String KEY_DATE = "Date";

	// Attributes

	private Date date = null;

	// Listeners

	private DatePickerDialog.OnDateSetListener onDateSetListener;
	private DialogInterface.OnClickListener onClearListener;

	// Overrides

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (outState != null) {
			outState.putSerializable(KEY_DATE, date);
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle arguments = (savedInstanceState != null) ? savedInstanceState : getArguments();

		if (arguments != null && arguments.containsKey(KEY_DATE)) {
			date = (Date) arguments.get(KEY_DATE);
		}
	}

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Calendar calendar = Calendar.getInstance();
		if (date != null) {
			calendar.setTime(date);
		}

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

		final DatePickerDialog datePickerWithClear =
			new DatePickerDialog(getActivity(), R.style.Theme_Tebo_Dialog_DatePicker, this, year, month, dayOfMonth);

		datePickerWithClear
			.setButton(DialogInterface.BUTTON_NEUTRAL, getResources().getText(R.string.action_clear), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					datePickerWithClear.cancel();
					onClearListener.onClick(dialog, which);
				}
			});

		return datePickerWithClear;
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
		if (onDateSetListener != null) {
			onDateSetListener.onDateSet(view, year, month, dayOfMonth);
		}
	}

	// Getters & setters

	public void setOnClearListener(DialogInterface.OnClickListener onClearListener) {
		this.onClearListener = onClearListener;
	}

	public void setOnDateSetListener(DatePickerDialog.OnDateSetListener onDateSetListener) {
		this.onDateSetListener = onDateSetListener;
	}
}
