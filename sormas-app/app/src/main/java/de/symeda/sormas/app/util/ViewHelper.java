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

package de.symeda.sormas.app.util;

import java.util.ArrayList;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.app.R;

/**
 * Created by Orson on 30/12/2017.
 */

public class ViewHelper {

	@NonNull
	public static ArrayList<View> getViewsByTag(@NonNull ViewGroup root, String tag) {
		ArrayList<View> views = new ArrayList<View>();
		final int childCount = root.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = root.getChildAt(i);
			if (child instanceof ViewGroup) {
				views.addAll(getViewsByTag((ViewGroup) child, tag));
			}

			if (tag == null || tag.isEmpty()) {
				views.add(child);
				continue;
			}

			final Object tagObj = child.getTag();
			if (tagObj != null && tagObj.equals(tag)) {
				views.add(child);
			}

		}
		return views;
	}

	public static void formatInaccessibleTextView(@NonNull TextView textField) {
		textField.setText(I18nProperties.getCaption(Captions.inaccessibleValue));
		textField.setTextColor(ResourceUtils.getColor(textField.getContext(),R.color.disabled));
		textField.setTypeface(null, Typeface.ITALIC);
	}

	public static void removeInaccessibleTextViewFormat(@NonNull TextView textField) {
		textField.setTextColor(ResourceUtils.getColor(textField.getContext(), R.color.listActivityRowPrimaryText));
		textField.setTypeface(null, Typeface.NORMAL);
	}
}
