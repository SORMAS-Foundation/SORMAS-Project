package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.infrastructure.country.CountryDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CountryFacadeRetro {

	@GET("countries/all/{since}")
	Call<List<CountryDto>> pullAllSince(@Path("since") long since);

	@POST("countries/query")
	Call<List<CountryDto>> pullByUuids(@Body List<String> uuids);

	@GET("countries/uuids")
	Call<List<String>> pullUuids();
}
