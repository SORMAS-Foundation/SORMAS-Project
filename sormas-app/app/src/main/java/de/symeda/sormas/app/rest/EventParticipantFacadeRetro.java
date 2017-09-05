package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.task.TaskDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EventParticipantFacadeRetro {

    @GET("eventparticipants/all/{since}")
    Call<List<EventParticipantDto>> pullAllSince(@Path("since") long since);

    @GET("eventparticipants/query")
    Call<List<EventParticipantDto>> pullByUuids(@Query("uuids") List<String> uuids);

    @POST("eventparticipants/push")
    Call<Integer> pushAll(@Body List<EventParticipantDto> dtos);

    @GET("eventparticipants/uuids")
    Call<List<String>> pullUuids();

}
