package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.report.AggregateReportDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AggregateReportFacadeRetro {

	@GET("aggregatereports/all/{since}")
	Call<List<AggregateReportDto>> pullAllSince(@Path("since") long since);

	@POST("aggregatereports/query")
	Call<List<AggregateReportDto>> pullByUuids(@Body List<String> uuids);

	@POST("aggregatereports/push")
	Call<List<PushResult>> pushAll(@Body List<AggregateReportDto> dtos);

	@GET("aggregatereports/uuids")
	Call<List<String>> pullUuids();

}
