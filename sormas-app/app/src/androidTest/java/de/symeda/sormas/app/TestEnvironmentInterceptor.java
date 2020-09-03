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

package de.symeda.sormas.app;

import java.net.URI;

import de.symeda.sormas.api.utils.InfoProvider;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Mate Strysewske on 13.06.2017.
 */
public class TestEnvironmentInterceptor implements Interceptor {

	@Override
	public Response intercept(Chain chain) {
		Response response = null;
		String responseString;
		final URI uri = chain.request().url().uri();

		// API version
		if (uri.getPath().endsWith("version")) {
			responseString = "\"" + InfoProvider.get().getVersion() + "\"";
		} else if (uri.getPath().contains("persons/all")) {
			responseString = "[]";
		} else if (uri.getPath().contains("persons/push")) {
			responseString = "0";
		} else if (uri.getPath().contains("cases/all")) {
			responseString = "[]";
		} else if (uri.getPath().contains("cases/push")) {
			responseString = "0";
		} else if (uri.getPath().contains("contacts/all")) {
			responseString = "[]";
		} else if (uri.getPath().contains("contacts/push")) {
			responseString = "0";
		} else if (uri.getPath().contains("events/all")) {
			responseString = "[]";
		} else if (uri.getPath().contains("events/push")) {
			responseString = "0";
		} else if (uri.getPath().contains("eventparticipants/all")) {
			responseString = "[]";
		} else if (uri.getPath().contains("eventparticipants/push")) {
			responseString = "0";
		} else if (uri.getPath().contains("visits/all")) {
			responseString = "[]";
		} else if (uri.getPath().contains("visits/push")) {
			responseString = "0";
		} else if (uri.getPath().contains("samples/all")) {
			responseString = "[]";
		} else if (uri.getPath().contains("samples/push")) {
			responseString = "0";
		} else if (uri.getPath().contains("sampletests/all")) {
			responseString = "[]";
		} else if (uri.getPath().contains("sampletests/push")) {
			responseString = "0";
		} else if (uri.getPath().contains("tasks/all")) {
			responseString = "[]";
		} else if (uri.getPath().contains("tasks/push")) {
			responseString = "0";
		} else if (uri.getPath().contains("regions/all")) {
			responseString = "[]";
		} else if (uri.getPath().contains("districts/all")) {
			responseString = "[]";
		} else if (uri.getPath().contains("communities/all")) {
			responseString = "[]";
		} else if (uri.getPath().contains("facilities/all")) {
			responseString = "[]";
		} else if (uri.getPath().contains("users/all")) {
			responseString = "[]";
		} else {
			responseString = "";
		}

		response = new Response.Builder().code(200)
			.message("OK")
			.request(chain.request())
			.protocol(Protocol.HTTP_1_1)
			.body(ResponseBody.create(MediaType.parse("application/json"), responseString))
			.addHeader("content-type", "application/json")
			.build();

		return response;
	}
}
