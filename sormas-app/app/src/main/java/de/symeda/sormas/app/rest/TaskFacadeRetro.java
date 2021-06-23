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

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.task.TaskDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Stefan Szczesny on 24.10.2016.
 */
public interface TaskFacadeRetro {

	@GET("tasks/all/{since}")
	Call<List<TaskDto>> pullAllSince(@Path("since") long since);

	@POST("tasks/query")
	Call<List<TaskDto>> pullByUuids(@Body List<String> uuids);

	@POST("tasks/push")
	Call<List<PushResult>> pushAll(@Body List<TaskDto> dtos);

	@GET("tasks/uuids")
	Call<List<String>> pullUuids();

	@GET("tasks/archived/{since}")
	Call<List<String>> pullArchivedUuidsSince(@Path("since") long since);
}
