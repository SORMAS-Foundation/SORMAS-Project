package de.symeda.sormas.app.rest;

import de.symeda.sormas.api.utils.CompatibilityCheckResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Stefan Szczesny on 24.10.2016.
 */
public interface InfoFacadeRetro {

    @GET("info/version")
    Call<String> getVersion();

    @GET("info/appurl")
    Call<String> getAppUrl(@Query("appVersion") String appVersion);

    @GET("info/checkcompatibility")
    Call<CompatibilityCheckResponse> isCompatibleToApi(@Query("appVersion") String appVersion);
}
