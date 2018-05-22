package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.report.WeeklyReportEntryDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Mate Strysewske on 12.09.2017.
 */
public interface WeeklyReportEntryFacadeRetro {

    @GET("weeklyreportentries/all/{since}")
    Call<List<WeeklyReportEntryDto>> pullAllSince(@Path("since") long since);

    @POST("weeklyreportentries/query")
    Call<List<WeeklyReportEntryDto>> pullByUuids(@Body List<String> uuids);

    @POST("weeklyreportentries/push")
    Call<Integer> pushAll(@Body List<WeeklyReportEntryDto> dtos);

    @GET("weeklyreportentries/uuids")
    Call<List<String>> pullUuids();

}
