package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Stefan Szczesny on 24.10.2016.
 */
public interface TaskFacadeRetro {

    @GET("tasks/all/{since}")
    Call<List<TaskDto>> pullAllSince(@Path("since") long since);

    @POST("tasks/query")
    Call<List<TaskDto>> pullByUuids(@Body List<String> uuids);

    @POST("tasks/push")
    Call<Integer> pushAll(@Body List<TaskDto> dtos);

    @GET("tasks/uuids")
    Call<List<String>> pullUuids();
}
