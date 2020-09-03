package de.symeda.sormas.app.rest;

import de.symeda.sormas.api.infrastructure.InfrastructureChangeDatesDto;
import de.symeda.sormas.api.infrastructure.InfrastructureSyncDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface InfrastructureFacadeRetro {

	@POST("infrastructure/sync")
	Call<InfrastructureSyncDto> pullInfrastructureSyncData(@Body InfrastructureChangeDatesDto changeDates);

}
