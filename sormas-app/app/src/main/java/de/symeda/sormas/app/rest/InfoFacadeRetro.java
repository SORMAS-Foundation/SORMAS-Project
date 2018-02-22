package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.contact.ContactDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Stefan Szczesny on 24.10.2016.
 */
public interface InfoFacadeRetro {

    @GET("info/version")
    Call<String> getVersion();

    @GET("info/appurl")
    Call<String> getAppUrl();
}
