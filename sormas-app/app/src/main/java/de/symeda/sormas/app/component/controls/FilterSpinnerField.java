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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.VisualStateControlType;

public class FilterSpinnerField extends ControlSpinnerField {

	// Constructors

	public FilterSpinnerField(Context context) {
		super(context);
	}

	public FilterSpinnerField(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FilterSpinnerField(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// Overrides

	@Override
	protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (inflater != null) {
			inflater.inflate(R.layout.filter_spinner_field_layout, this);
		} else {
			throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
		}
	}

	@Override
	protected void onFinishInflate() {
		setLiveValidationDisabled(true);
		super.onFinishInflate();
	}

	@Override
	protected void changeVisualState(VisualState state) {
		if (getUserEditRight() != null && !ConfigProvider.hasUserRight(getUserEditRight())) {
			state = VisualState.DISABLED;
		}

		if (this.visualState == state) {
			return;
		}

		visualState = state;

		int labelColor = getResources().getColor(state.getLabelColor());
		Drawable drawable = getResources().getDrawable(state.getBackground(VisualStateControlType.SPINNER_FILTER));

		if (drawable != null) {
			drawable = drawable.mutate();
		}

		label.setTextColor(labelColor);
		setBackground(drawable);

		input.requestLayout();

		setEnabled(state != VisualState.DISABLED);
	}

	@Override
	public void enableErrorState(String errorMessage) {
		// Don't do anything here
	}

	@Override
	public void disableErrorState() {
		// Don't do anything here
	}
}
