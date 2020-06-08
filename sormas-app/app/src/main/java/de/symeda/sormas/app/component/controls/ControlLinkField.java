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
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import de.symeda.sormas.app.R;

public class ControlLinkField extends ControlTextReadField {

	// Constructors

	public ControlLinkField(Context context) {
		super(context);
	}

	public ControlLinkField(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ControlLinkField(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// Overrides

	@Override
	protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (inflater != null) {
			inflater.inflate(R.layout.control_link_layout, this);
		} else {
			throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		textView.setImeOptions(getImeOptions());
	}

	@Override
	public void setOnClickListener(OnClickListener listener) {
		textView.setOnClickListener(listener);
	}
}
