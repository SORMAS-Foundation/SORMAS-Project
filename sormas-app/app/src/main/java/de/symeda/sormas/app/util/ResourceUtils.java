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

/**
 * Created by Orson on 01/12/2017.
 */

public class ResourceUtils {

	public static String getString(Context context, int resourceId) {
		return context.getResources().getString(resourceId);
	}

	public static int getColor(Context context, int resourceId) {
		return context.getResources().getColor(resourceId);
	}

	public static Drawable getDrawable(Context context, int resourceId) {
		return context.getResources().getDrawable(resourceId);
	}

	public static float getDimension(Context context, int resourceId) {
		return context.getResources().getDimension(resourceId);
	}

	public static String[] getStringArray(Context context, int resourceId) {
		return context.getResources().getStringArray(resourceId);
	}

	public static int[] getIntArray(Context context, int resourceId) {
		return context.getResources().getIntArray(resourceId);
	}

	public static boolean getBoolean(Context context, int resourceId) {
		return context.getResources().getBoolean(resourceId);
	}

	public static int getIdentifier(Context context, String name, String defType, String defPackage) {
		return context.getResources().getIdentifier(name, defType, defPackage);
	}

	public static float getFraction(Context context, int resourceId, int base, int pbase) {
		return context.getResources().getFraction(resourceId, base, pbase);
	}

	public static int getInteger(Context context, int resourceId) {
		return context.getResources().getInteger(resourceId);
	}

	public static Configuration getConfiguration(Context context) {
		return context.getResources().getConfiguration();
	}
}
