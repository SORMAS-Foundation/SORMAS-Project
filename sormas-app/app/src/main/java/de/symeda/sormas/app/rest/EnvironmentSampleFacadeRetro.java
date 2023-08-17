package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.PostResponse;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EnvironmentSampleFacadeRetro {
    @GET("environmentsamples/uuids")
    Call<List<String>> pullUuids();

    @GET("environmentsamples/all/{since}/{size}/{lastSynchronizedUuid}")
    Call<List<EnvironmentSampleDto>> pullAllSince(@Path("since") long since, @Path("size") int size, @Path("lastSynchronizedUuid") String lastSynchronizedUuid);

    @POST("environmentsamples/query")
    Call<List<EnvironmentSampleDto>> pullByUuids(@Body List<String> uuids);

    @POST("environmentsamples/push")
    Call<List<PostResponse>> pushAll(@Body List<EnvironmentSampleDto> dtos);


    @GET("environmentsamples/obsolete/{since}")
    Call<List<String>> pullObsoleteUuidsSince(@Path("since") long since);
}
