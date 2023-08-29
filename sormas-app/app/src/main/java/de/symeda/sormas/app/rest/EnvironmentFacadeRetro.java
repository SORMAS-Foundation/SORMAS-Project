package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.PostResponse;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.event.EventDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EnvironmentFacadeRetro {

	@GET("environments/all/{since}")
	Call<List<EnvironmentDto>> pullAllSince(@Path("since") long since);

	@GET("environments/all/{since}/{size}/{lastSynchronizedUuid}")
	Call<List<EnvironmentDto>> pullAllSince(@Path("since") long since, @Path("size") int size, @Path("lastSynchronizedUuid") String lastSynchronizedUuid);

	@POST("environments/query")
	Call<List<EnvironmentDto>> pullByUuids(@Body List<String> uuids);

	@POST("environments/push")
	Call<List<PostResponse>> pushAll(@Body List<EnvironmentDto> dtos);

	@GET("environments/uuids")
	Call<List<String>> pullUuids();

	@GET("environments/obsolete/{since}")
	Call<List<String>> pullObsoleteUuidsSince(@Path("since") long since);
}
