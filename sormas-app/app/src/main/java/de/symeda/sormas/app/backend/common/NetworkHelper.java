/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

public class NetworkHelper {

	private static final Integer ASSUMED_TRANSFER_TIME_IN_SECONDS = 60;

	private NetworkHelper() {
	}

	public static Integer getNumberOfEntitiesToBePulledInOneBatch(long approximateJsonSizeInBytes, Context context) {
		return Math.toIntExact(getNetworkDownloadSpeedInKbps(context) * ASSUMED_TRANSFER_TIME_IN_SECONDS * 1024 / (approximateJsonSizeInBytes * 8));
	}

	// TODO: 17.12.2021 check with airplane mode how it behaves
	public static long getNetworkDownloadSpeedInKbps(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
		return nc.getLinkDownstreamBandwidthKbps();
	}
}
