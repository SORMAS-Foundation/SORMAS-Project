package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.sample.SampleDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Mate Strysewske on 06.02.2017.
 */

public interface SampleFacadeRetro {

    @GET("samples/all/{user}/{since}")
    Call<List<SampleDto>> getAll(@Path("user") String userUuid, @Path("since") long since);

    @POST("samples/push")
    Call<Long> postAll(@Body List<SampleDto> dtos);

}
