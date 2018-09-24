package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.region.RegionDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Martin Wahnschaffe on 07.06.2016.
 */
public interface RegionFacadeRetro {

    @GET("regions/all/{since}")
    Call<List<RegionDto>> pullAllSince(@Path("since") long since);

    @POST("regions/query")
    Call<List<RegionDto>> pullByUuids(@Body List<String> uuids);

    @GET("regions/uuids")
    Call<List<String>> pullUuids();
}
