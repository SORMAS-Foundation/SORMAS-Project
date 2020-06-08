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

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;

import de.symeda.sormas.app.R;

public enum ControlSwitchState {

	NORMAL(R.color.transparent),
	PRESSED(R.color.transparent),
	CHECKED(R.color.lighterBlue),
	DISABLED(R.color.disabled);

	private static final int DRAWABLE_SHAPE = GradientDrawable.RECTANGLE;
	private static final int STROKE_COLOR = R.color.lighterBlue;
	private static final int BUTTON_DIVIDER_WIDTH = 2;
	private static final int LAST_BG_INSET_LEFT = -2;
	private static final int LAST_BG_INSET_TOP = -2;
	private static final int LAST_BG_INSET_BOTTOM = -2;
	private static final int LAST_BG_INSET_RIGHT = 0;

	private int backgroundColor;
	private int backgroundColorError;

	ControlSwitchState(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Drawable getDrawable(boolean lastButton, boolean hasError, Resources resources) {
		GradientDrawable drawable = new GradientDrawable();
		drawable.setShape(DRAWABLE_SHAPE);
		drawable.setColor(resources.getColor(backgroundColor));

		if (lastButton) {
			return drawable;
		}

		drawable.setStroke(BUTTON_DIVIDER_WIDTH, getStrokeButtonDivider(hasError, resources));

		LayerDrawable layerDrawable = new LayerDrawable(
			new Drawable[] {
				drawable });
		layerDrawable.setLayerInset(0, LAST_BG_INSET_LEFT, LAST_BG_INSET_TOP, LAST_BG_INSET_RIGHT, LAST_BG_INSET_BOTTOM);

		return layerDrawable;
	}

	private ColorStateList getStrokeButtonDivider(boolean hasError, Resources resources) {
		int[][] states = new int[][] {
			new int[] {
				-android.R.attr.state_checked,
				-android.R.attr.state_enabled,
				-android.R.attr.state_checkable,
				-android.R.attr.state_focused },
			new int[] {
				-android.R.attr.state_enabled },
			new int[] {
				android.R.attr.state_enabled }, };

		int[] thumbColors = new int[] {
			resources.getColor(R.color.disabled),
			resources.getColor(R.color.disabled),
			hasError ? resources.getColor(R.color.watchOut) : resources.getColor(STROKE_COLOR) };

		return new ColorStateList(states, thumbColors);
	}
}
