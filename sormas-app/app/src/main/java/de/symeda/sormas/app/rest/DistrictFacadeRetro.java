package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.region.DistrictDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Martin Wahnschaffe on 07.06.2016.
 */
public interface DistrictFacadeRetro {

    @GET("districts/all/{since}")
    Call<List<DistrictDto>> pullAllSince(@Path("since") long since);

    @POST("districts/query")
    Call<List<DistrictDto>> pullByUuids(@Body List<String> uuids);

    @GET("districts/uuids")
    Call<List<String>> pullUuids();
}
