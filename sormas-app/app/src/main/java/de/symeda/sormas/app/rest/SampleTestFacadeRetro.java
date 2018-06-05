package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Mate Strysewske on 09.02.2017.
 */

public interface SampleTestFacadeRetro {

    @GET("sampletests/all/{since}")
    Call<List<SampleTestDto>> pullAllSince(@Path("since") long since);

    @POST("sampletests/query")
    Call<List<SampleTestDto>> pullByUuids(@Body List<String> uuids);

    @GET("sampletests/uuids")
    Call<List<String>> pullUuids();

}
