package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.person.PersonDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Martin Wahnschaffe on 07.06.2016.
 */
public interface PersonFacadeRetro {

    @GET("persons/all/{since}")
    Call<List<PersonDto>> pullAllSince(@Path("since") long since);

    @POST("persons/query")
    Call<List<PersonDto>> pullByUuids(@Body List<String> uuids);

    @POST("persons/push")
    Call<Integer> pushAll(@Body List<PersonDto> dtos);

    @GET("persons/uuids")
    Call<List<String>> pullUuids();
}
