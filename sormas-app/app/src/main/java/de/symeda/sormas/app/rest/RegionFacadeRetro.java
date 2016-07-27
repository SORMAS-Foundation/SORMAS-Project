package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.region.RegionDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Martin Wahnschaffe on 07.06.2016.
 */
public interface RegionFacadeRetro {

    @GET("regions/all/{since}")
    Call<List<RegionDto>> getAll(@Path("since") long since);

}
