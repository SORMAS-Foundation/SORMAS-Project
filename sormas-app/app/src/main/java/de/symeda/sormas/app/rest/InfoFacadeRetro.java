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

import de.symeda.sormas.api.utils.CompatibilityCheckResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Stefan Szczesny on 24.10.2016.
 */
public interface InfoFacadeRetro {

	@GET("info/version")
	Call<String> getVersion();

	@GET("info/appurl")
	Call<String> getAppUrl(@Query("appVersion") String appVersion);

	@GET("info/checkcompatibility")
	Call<CompatibilityCheckResponse> isCompatibleToApi(@Query("appVersion") String appVersion);

	@GET("info/locale")
	Call<String> getLocale();
}
