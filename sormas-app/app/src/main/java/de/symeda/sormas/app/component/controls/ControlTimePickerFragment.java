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

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import de.symeda.sormas.app.R;

public class ControlTimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

	// Constants

	public static final String KEY_TIME = "Time";

	// Attributes

	private Date time = null;

	// Listeners

	private TimePickerDialog.OnTimeSetListener onTimeSetListener;
	private DialogInterface.OnClickListener onClearListener;

	// Overrides

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (outState != null) {
			outState.putSerializable(KEY_TIME, time);
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle arguments = (savedInstanceState != null) ? savedInstanceState : getArguments();

		if (arguments != null && arguments.containsKey(KEY_TIME)) {
			time = (Date) arguments.get(KEY_TIME);
		}
	}

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Calendar calendar = Calendar.getInstance();
		if (time != null) {
			calendar.setTime(time);
		}

		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		final TimePickerDialog timePickerWithClear =
			new TimePickerDialog(getActivity(), R.style.Theme_Tebo_Dialog_DatePicker, this, hour, minute, DateFormat.is24HourFormat(getActivity()));

		timePickerWithClear
			.setButton(DialogInterface.BUTTON_NEUTRAL, getResources().getText(R.string.action_clear), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					timePickerWithClear.cancel();
					onClearListener.onClick(dialog, which);
				}
			});

		return timePickerWithClear;
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		if (onTimeSetListener != null)
			onTimeSetListener.onTimeSet(view, hourOfDay, minute);
	}

	// Getters & setters

	public void setOnClearListener(DialogInterface.OnClickListener onClearListener) {
		this.onClearListener = onClearListener;
	}

	public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener onTimeSetListener) {
		this.onTimeSetListener = onTimeSetListener;
	}
}
