package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.task.TaskDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EventFacadeRetro {

    @GET("events/all/{since}")
    Call<List<EventDto>> pullAllSince(@Path("since") long since);

    @POST("events/query")
    Call<List<EventDto>> pullByUuids(@Body List<String> uuids);

    @POST("events/push")
    Call<Integer> pushAll(@Body List<EventDto> dtos);

    @GET("events/uuids")
    Call<List<String>> pullUuids();
}
