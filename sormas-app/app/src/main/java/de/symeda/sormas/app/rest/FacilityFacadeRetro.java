package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.facility.FacilityDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Martin Wahnschaffe on 07.06.2016.
 */
public interface FacilityFacadeRetro {

    @GET("facilities/region/{regionUuid}/{since}")
    Call<List<FacilityDto>> pullAllByRegionSince(@Path("regionUuid") String regionUUid, @Path("since") long since);

    @GET("facilities/no-region/{since}")
    Call<List<FacilityDto>> pullAllWithoutRegionSince(@Path("since") long since);

}
