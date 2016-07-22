package de.symeda.sormas.app.rest;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Martin Wahnschaffe on 07.06.2016.
 */
public interface PersonFacadeRetro {

    @GET("persons/{uuid}")
    Call<PersonDto> getByUuid(@Path("uuid") String uuid);

    @GET("persons/all/{since}")
    Call<List<PersonDto>> getAllPersons(@Path("since") long since);

}
