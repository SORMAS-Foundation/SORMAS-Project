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

import static android.util.Log.INFO;
import static android.util.Log.VERBOSE;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

final class Utils {

	private Utils() {
	}

	@Nullable
	static Activity getActivity(@Nullable Context cont) {
		if (cont == null) {
			return null;
		} else if (cont instanceof Activity) {
			return (Activity) cont;
		} else if (cont instanceof ContextWrapper) {
			return getActivity(((ContextWrapper) cont).getBaseContext());
		}
		return null;
	}

	static void log(final String tag, final int level, final String format, Object... args) {
		if (Tooltip.dbg) {
			switch (level) {
			case Log.DEBUG:
				Log.d(tag, String.format(format, args));
				break;
			case Log.ERROR:
				Log.e(tag, String.format(format, args));
				break;
			case INFO:
				Log.i(tag, String.format(format, args));
				break;
			case Log.WARN:
				Log.w(tag, String.format(format, args));
				break;
			default:
			case VERBOSE:
				Log.v(tag, String.format(format, args));
				break;
			}
		}
	}

	static boolean equals(@Nullable Object a, @Nullable Object b) {
		return (a == null) ? (b == null) : a.equals(b);
	}

	static boolean rectContainsRectWithTolerance(@NonNull final Rect parentRect, @NonNull final Rect childRect, final int t) {
		return parentRect.contains(childRect.left + t, childRect.top + t, childRect.right - t, childRect.bottom - t);
	}
}
