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

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;

import androidx.annotation.BoolRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

/**
 * Created by Orson on 01/12/2017.
 */

public class ResourceUtils {

	public static String getString(@NonNull Context context, @StringRes int resourceId) {
		return context.getResources().getString(resourceId);
	}

	@ColorInt
	public static int getColor(@NonNull Context context, @ColorRes int resourceId) {
		return ContextCompat.getColor(context, resourceId);
	}

	@Nullable
	public static Drawable getDrawable(@NonNull Context context, @DrawableRes int resourceId) {
		return ContextCompat.getDrawable(context, resourceId);
	}

	public static float getDimension(@NonNull Context context, @DimenRes int resourceId) {
		return context.getResources().getDimension(resourceId);
	}

	public static String[] getStringArray(@NonNull Context context, int resourceId) {
		return context.getResources().getStringArray(resourceId);
	}

	public static int[] getIntArray(@NonNull Context context, int resourceId) {
		return context.getResources().getIntArray(resourceId);
	}

	public static boolean getBoolean(@NonNull Context context, @BoolRes int resourceId) {
		return context.getResources().getBoolean(resourceId);
	}

	public static int getIdentifier(@NonNull Context context, String name, String defType, String defPackage) {
		return context.getResources().getIdentifier(name, defType, defPackage);
	}

	public static float getFraction(@NonNull Context context, int resourceId, int base, int pbase) {
		return context.getResources().getFraction(resourceId, base, pbase);
	}

	public static int getInteger(@NonNull Context context, int resourceId) {
		return context.getResources().getInteger(resourceId);
	}

	public static Configuration getConfiguration(@NonNull Context context) {
		return context.getResources().getConfiguration();
	}
}
