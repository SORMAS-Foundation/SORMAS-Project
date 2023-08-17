package de.symeda.sormas.app.backend.environment.environmentsample;

import java.util.List;

import de.symeda.sormas.api.PostResponse;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.environment.Environment;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class EnvironmentSampleDtoHelper extends AdoDtoHelper<EnvironmentSample, EnvironmentSampleDto> {
    @Override
    protected Class<EnvironmentSample> getAdoClass() {
        return EnvironmentSample.class;
    }

    @Override
    protected Class<EnvironmentSampleDto> getDtoClass() {
        return EnvironmentSampleDto.class;
    }

    @Override
    protected Call<List<EnvironmentSampleDto>> pullAllSince(long since, Integer size, String lastSynchronizedUuid) throws NoConnectionException {
        return RetroProvider.getEnvironmentSampleFacade().pullAllSince(since, size, lastSynchronizedUuid);
    }

    @Override
    protected Call<List<EnvironmentSampleDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
        return RetroProvider.getEnvironmentSampleFacade().pullByUuids(uuids);
    }

    @Override
    protected Call<List<PostResponse>> pushAll(List<EnvironmentSampleDto> environmentSampleDtos) throws NoConnectionException {
        return RetroProvider.getEnvironmentSampleFacade().pushAll(environmentSampleDtos);
    }

    @Override
    protected void fillInnerFromDto(EnvironmentSample environmentSample, EnvironmentSampleDto dto) {

    }

    @Override
    protected void fillInnerFromAdo(EnvironmentSampleDto dto, EnvironmentSample environmentSample) {

    }

    @Override
    protected long getApproximateJsonSizeInBytes() {
        return EnvironmentSampleDto.APPROXIMATE_JSON_SIZE_IN_BYTES;
    }
}
