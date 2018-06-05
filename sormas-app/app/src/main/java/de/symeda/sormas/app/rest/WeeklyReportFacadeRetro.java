package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.report.WeeklyReportDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Mate Strysewske on 12.09.2017.
 */
public interface WeeklyReportFacadeRetro {

    @GET("weeklyreports/all/{since}")
    Call<List<WeeklyReportDto>> pullAllSince(@Path("since") long since);

    @POST("weeklyreports/query")
    Call<List<WeeklyReportDto>> pullByUuids(@Body List<String> uuids);

    @POST("weeklyreports/push")
    Call<Integer> pushAll(@Body List<WeeklyReportDto> dtos);

    @GET("weeklyreports/uuids")
    Call<List<String>> pullUuids();

}
