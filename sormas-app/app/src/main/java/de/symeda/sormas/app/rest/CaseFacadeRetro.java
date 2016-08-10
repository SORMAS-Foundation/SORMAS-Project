package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Martin Wahnschaffe on 07.06.2016.
 */
public interface CaseFacadeRetro {

    @GET("cases/all/{user}/{since}")
    Call<List<CaseDataDto>> getAll(@Path("user") String userUuid, @Path("since") long since);
}
