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

import java.util.Hashtable;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

public final class Typefaces {

	private static final String TAG = "Typefaces";
	private static final Hashtable<String, Typeface> FONT_CACHE = new Hashtable<>();

	private Typefaces() {
	}

	public static Typeface get(Context c, String assetPath) {
		synchronized (FONT_CACHE) {
			if (!FONT_CACHE.containsKey(assetPath)) {
				try {
					Typeface t = Typeface.createFromAsset(c.getAssets(), assetPath);
					FONT_CACHE.put(assetPath, t);
				} catch (Exception e) {
					Log.e(TAG, "Could not get typeface '" + assetPath + "' because " + e.getMessage());
					return null;
				}
			}
			return FONT_CACHE.get(assetPath);
		}
	}
}
