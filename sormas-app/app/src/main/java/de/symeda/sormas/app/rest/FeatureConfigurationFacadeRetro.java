package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.feature.FeatureConfigurationDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FeatureConfigurationFacadeRetro {

	@GET("featureconfigurations/all/{since}")
	Call<List<FeatureConfigurationDto>> pullAllSince(@Path("since") long since);

	@POST("featureconfigurations/query")
	Call<List<FeatureConfigurationDto>> pullByUuids(@Body List<String> uuids);

	@GET("featureconfigurations/uuids")
	Call<List<String>> pullUuids();

	@GET("userroles/deleted/{since}")
	Call<List<String>> pullDeletedUuidsSince(@Path("since") long since);

}
