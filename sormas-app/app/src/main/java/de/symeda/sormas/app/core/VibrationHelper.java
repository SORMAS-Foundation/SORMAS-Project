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

package de.symeda.sormas.app.core;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

public class VibrationHelper {

	private static VibrationHelper sSoleInstance;
	private static Context mContext;
	private static long[] inputFieldErrorPattern = {
		0,
		300,
		100,
		500 };;

	private VibrationHelper(Context context) {
		this.mContext = context;
	}

	public static VibrationHelper getInstance(Context context) {
		if (sSoleInstance == null) { //if there is no instance available... create new one
			sSoleInstance = new VibrationHelper(context);
		}

		return sSoleInstance;
	}

	public static void onInputFieldError() {
		Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

		if (v.hasVibrator()) {
			v.vibrate(inputFieldErrorPattern, -1);
		} else {
			Log.v("Can Vibrate", "NO");
		}
	}
}
