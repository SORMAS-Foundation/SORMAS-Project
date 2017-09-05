package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.task.TaskDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Stefan Szczesny on 24.10.2016.
 */
public interface ContactFacadeRetro {

    @GET("contacts/all/{since}")
    Call<List<ContactDto>> pullAllSince(@Path("since") long since);

    @GET("contacts/query")
    Call<List<ContactDto>> pullByUuids(@Query("uuids") List<String> uuids);

    @POST("contacts/push")
    Call<Integer> pushAll(@Body List<ContactDto> dtos);

    @GET("contacts/uuids")
    Call<List<String>> pullUuids();
}
