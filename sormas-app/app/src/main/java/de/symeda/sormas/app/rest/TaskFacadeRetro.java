package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.task.TaskDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Stefan Szczesny on 24.10.2016.
 */
public interface TaskFacadeRetro {

    @GET("tasks/all/{since}")
    Call<List<TaskDto>> getAll(@Path("since") long since);

    @POST("tasks/push")
    Call<Long> postAll(@Body List<TaskDto> dtos);
}
