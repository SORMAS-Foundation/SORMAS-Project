package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.event.EventDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EventFacadeRetro {

    @GET("events/all/{user}/{since}")
    Call<List<EventDto>> getAll(@Path("user") String userUuid, @Path("since") long since);

    @POST("events/push")
    Call<Long> postAll(@Body List<EventDto> dtos);
}
