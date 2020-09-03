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

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.Item;

public class ControlSpinnerAdapter extends ArrayAdapter<Item> {

	// Spinner fields

	private ControlSpinnerField spinner;
	private List<Item> spinnerData;
	private boolean excludeEmptyItem;

	// Resources

	private LayoutInflater inflater;
	private int layoutResourceId;
	private int dropdownResourceId;
	private int textViewResourceId;

	// Constructor

	ControlSpinnerAdapter(
		@NonNull Context context,
		ControlSpinnerField spinner,
		@NonNull List<Item> objects,
		int layoutResourceId,
		int dropdownResourceId,
		int textViewResourceId,
		boolean excludeEmptyItem) {
		super(context, layoutResourceId, textViewResourceId, objects);

		this.spinner = spinner;
		this.spinnerData = objects;
		this.layoutResourceId = layoutResourceId;
		this.dropdownResourceId = dropdownResourceId;
		this.textViewResourceId = textViewResourceId;
		this.inflater = LayoutInflater.from(context);
		this.excludeEmptyItem = excludeEmptyItem;
	}

	// Overrides

	@Override
	public int getCount() {
		return spinnerData.size();
	}

	@Override
	public Item getItem(int position) {
		return spinnerData.get(position);
	}

	@Override
	@NonNull
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		final View view;
		final TextView textView;

		if (convertView == null) {
			view = inflater.inflate(this.layoutResourceId, parent, false);
		} else {
			view = convertView;
		}

		final Item item = getItem(position);
		textView = (TextView) view.findViewById(textViewResourceId);

		if (item != null) {
			textView.setText(item.toString());
		}

		if (textView != null && (StringUtils.isEmpty(textView.getText()))) {
			if (spinner.getHint() != null) {
				textView.setHint(spinner.getHint());
			} else {
				textView.setHint(getContext().getResources().getString(R.string.hint_select_entry));
			}
			textView.setTextColor(getContext().getResources().getColor(R.color.hintText));
		}

		return view;
	}

	@Override
	public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
		final View view;
		final TextView textView;

		if (spinnerData.size() <= 0) {
			return null;
		}

		if (convertView == null) {
			view = inflater.inflate(this.dropdownResourceId, parent, false);
		} else {
			view = convertView;
		}

		final Item item = getItem(position);
		textView = (TextView) view.findViewById(textViewResourceId);

		if (item != null) {
			textView.setText(item.toString());
		}

		if (textView != null) {
			if (StringUtils.isEmpty(textView.getText())) {
				textView.setText(getContext().getResources().getString(R.string.hint_clear));
			}

			if (position == 0 && !excludeEmptyItem) {
				textView.setTypeface(null, Typeface.BOLD_ITALIC);
			} else {
				textView.setTypeface(null, Typeface.NORMAL);
			}

			int selectedPosition = spinner.getPositionOf(spinner.getSelectedItem());
			if (selectedPosition >= 0) {
				if (position == selectedPosition) {
					textView.setTextColor(getContext().getResources().getColor(R.color.spinnerDropdownItemTextActive));
					view.setBackgroundColor(getContext().getResources().getColor(R.color.spinnerDropdownItemBackgroundActive));
				} else {
					textView.setTextColor(getContext().getResources().getColor(R.color.controlTextColor));
					view.setBackground(getContext().getResources().getDrawable(R.drawable.background_spinner_item));
				}
			}
		}

		return view;
	}
}
