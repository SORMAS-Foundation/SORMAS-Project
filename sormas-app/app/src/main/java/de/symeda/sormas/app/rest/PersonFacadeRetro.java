package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.person.PersonDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Martin Wahnschaffe on 07.06.2016.
 */
public interface PersonFacadeRetro {

    @GET("persons/all/{since}")
    Call<List<PersonDto>> pullAllSince(@Path("since") long since);

    @POST("persons/push")
    Call<Long> pushAll(@Body List<PersonDto> dtos);

    @GET("persons/uuids")
    Call<List<String>> pullUuids();
}
