package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.region.DistrictDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Martin Wahnschaffe on 07.06.2016.
 */
public interface DistrictFacadeRetro {

    @GET("districts/all/{since}")
    Call<List<DistrictDto>> pullAllSince(@Path("since") long since);

    @GET("districts/uuids")
    Call<List<String>> pullUuids();
}
