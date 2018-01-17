package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.outbreak.OutbreakDto;
import de.symeda.sormas.api.region.CommunityDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Martin Wahnschaffe on 07.06.2016.
 */
public interface OutbreakFacadeRetro {

    @GET("outbreaks/all/{since}")
    Call<List<OutbreakDto>> pullAllSince(@Path("since") long since);

    @GET("outbreaks/uuids")
    Call<List<String>> pullUuids();
}
