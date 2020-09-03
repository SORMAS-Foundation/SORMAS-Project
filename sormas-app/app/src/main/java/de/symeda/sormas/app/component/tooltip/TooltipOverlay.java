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

package de.symeda.sormas.app.component.tooltip;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import de.symeda.sormas.app.R;

public class TooltipOverlay extends androidx.appcompat.widget.AppCompatImageView {

	private int mMargins;

	public TooltipOverlay(Context context) {
		this(context, null);
	}

	public TooltipOverlay(Context context, AttributeSet attrs) {
		this(context, attrs, R.style.ToolTipOverlayDefaultStyle);
	}

	public TooltipOverlay(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, R.style.ToolTipLayoutDefaultStyle);
	}

	private void init(final Context context, final int defStyleResId) {
		TooltipOverlayDrawable drawable = new TooltipOverlayDrawable(context, defStyleResId);
		setImageDrawable(drawable);

		final TypedArray array = context.getTheme().obtainStyledAttributes(defStyleResId, R.styleable.TooltipOverlay);
		mMargins = array.getDimensionPixelSize(R.styleable.TooltipOverlay_android_layout_margin, 0);
		array.recycle();
	}

	public TooltipOverlay(Context context, AttributeSet attrs, int defStyleAttr, int defStyleResId) {
		super(context, attrs, defStyleAttr);
		init(context, defStyleResId);
	}

	public int getLayoutMargins() {
		return mMargins;
	}
}
