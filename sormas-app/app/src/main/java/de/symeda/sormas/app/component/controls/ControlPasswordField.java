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

import com.google.android.material.textfield.TextInputLayout;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import de.symeda.sormas.app.R;

public class ControlPasswordField extends ControlTextEditField {

	// Constructors

	public ControlPasswordField(Context context) {
		super(context);
	}

	public ControlPasswordField(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ControlPasswordField(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// Overrides

	@Override
	protected void initialize(Context context, AttributeSet attrs, int defStyle) {
		super.initialize(context, attrs, defStyle);

		setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
	}

	@Override
	protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (inflater != null) {
			if (isSlim()) {
				inflater.inflate(R.layout.control_password_slim_layout, this);
			} else {
				inflater.inflate(R.layout.control_password_layout, this);
			}
		} else {
			throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		TextInputLayout inputLayout = (TextInputLayout) this.findViewById(R.id.text_input_layout);
		inputLayout.setPasswordVisibilityToggleEnabled(true);
	}
}
