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

package de.symeda.sormas.app.rest;

import java.util.Arrays;
import java.util.List;

import android.content.Context;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;

/**
 * Should be thrown when connecting or communicating with the server failed, because:
 * * 401, 403: the user was not properly authorized
 * * 404: the server was not found
 * * 503: the server is currently not available
 * * 600: no network connection
 * * 601: app version is too new
 * 
 * @see ServerCommunicationException
 */
public class ServerConnectionException extends Exception {

	public static final List<Integer> RelatedErrorCodes = Arrays.asList(401, 403, 404, 503, 600, 601);

	private final int customHtmlErrorCode;

	public ServerConnectionException(int customHtmlErrorCode) {
		this.customHtmlErrorCode = customHtmlErrorCode;
	}

	public int getCustomHtmlErrorCode() {
		return customHtmlErrorCode;
	}

	public String getMessage(Context context) {

		switch (getCustomHtmlErrorCode()) {
		case 401:
			return context.getResources().getString(R.string.message_http_401);
		case 403:
			return context.getResources().getString(R.string.message_http_403);
		case 404: {
			String serverUrl = ConfigProvider.getServerRestUrl();
			if (DataHelper.isNullOrEmpty(serverUrl)) {
				return context.getResources().getString(R.string.message_http_404_empty);
			} else {
				return String.format(context.getResources().getString(R.string.message_http_404), serverUrl);
			}
		}
		case 503:
			return context.getResources().getString(R.string.message_http_503);
		case 600:
			return context.getResources().getString(R.string.message_no_connection);
		case 601:
			return context.getResources().getString(R.string.message_version_too_new);
		default:
			throw new IllegalArgumentException("" + getCustomHtmlErrorCode());
		}
	}
}
