package de.symeda.sormas.app.rest;

import java.util.List;

import de.symeda.sormas.api.sample.SampleTestDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Mate Strysewske on 09.02.2017.
 */

public interface SampleTestFacadeRetro {

    @GET("sampletests/all/{since}")
    Call<List<SampleTestDto>> getAll(@Path("since") long since);

    @GET("sampletests/uuids")
    Call<List<String>> getUuids();

}
