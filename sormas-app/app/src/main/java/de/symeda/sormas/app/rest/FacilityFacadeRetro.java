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

    @GET("facilities/all/{since}")
    Call<List<FacilityDto>> pullAllSince(@Path("since") long since);

}
