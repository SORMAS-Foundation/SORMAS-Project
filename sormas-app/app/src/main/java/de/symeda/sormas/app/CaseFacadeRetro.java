package de.symeda.sormas.app;

import de.symeda.sormas.api.caze.Case;
import de.symeda.sormas.api.caze.CaseFacade;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Martin Wahnschaffe on 07.06.2016.
 */
public interface CaseFacadeRetro {

    @GET("cases/{uuid}")
    Call<Case> getByUuid(@Path("uuid") String uuid);
}
