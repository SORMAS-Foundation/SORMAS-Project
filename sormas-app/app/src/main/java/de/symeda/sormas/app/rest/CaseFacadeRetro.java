package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.task.TaskDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Martin Wahnschaffe on 07.06.2016.
 */
public interface CaseFacadeRetro {

    @GET("cases/all/{since}")
    Call<List<CaseDataDto>> pullAllSince(@Path("since") long since);

    @POST("cases/query")
    Call<List<CaseDataDto>> pullByUuids(@Body List<String> uuids);

    @POST("cases/push")
    Call<Integer> pushAll(@Body List<CaseDataDto> dtos);

    @GET("cases/uuids")
    Call<List<String>> pullUuids();
}
