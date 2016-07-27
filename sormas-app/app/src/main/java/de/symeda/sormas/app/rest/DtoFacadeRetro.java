package de.symeda.sormas.app.rest;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */

import java.util.List;

import de.symeda.sormas.api.DataTransferObject;
import retrofit2.Call;

public interface DtoFacadeRetro<DTO extends DataTransferObject> {

    Call<List<DTO>> getAll(long since);
}
