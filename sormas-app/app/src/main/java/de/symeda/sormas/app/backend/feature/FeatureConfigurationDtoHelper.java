package de.symeda.sormas.app.backend.feature;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.feature.FeatureConfigurationDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class FeatureConfigurationDtoHelper extends AdoDtoHelper<FeatureConfiguration, FeatureConfigurationDto> {

	@Override
	protected Class<FeatureConfiguration> getAdoClass() {
		return FeatureConfiguration.class;
	}

	@Override
	protected Class<FeatureConfigurationDto> getDtoClass() {
		return FeatureConfigurationDto.class;
	}

	@Override
	protected Call<List<FeatureConfigurationDto>> pullAllSince(long since, Integer size, String lastSynchronizedUuid) throws NoConnectionException {
		return RetroProvider.getFeatureConfigurationFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<FeatureConfigurationDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getFeatureConfigurationFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<FeatureConfigurationDto> dtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is read-only");
	}

	@Override
	public void fillInnerFromDto(FeatureConfiguration target, FeatureConfigurationDto source) {
		target.setDisease(source.getDisease());
		target.setFeatureType(source.getFeatureType());
		target.setEndDate(source.getEndDate());
		target.setEnabled(source.isEnabled());
		target.setPropertiesMap(source.getProperties());
	}

	@Override
	public void fillInnerFromAdo(FeatureConfigurationDto target, FeatureConfiguration source) {
		target.setDisease(source.getDisease());
		target.setFeatureType(source.getFeatureType());
		target.setEndDate(source.getEndDate());
		target.setEnabled(source.isEnabled());
		target.setProperties(source.getPropertiesMap());
	}

	@Override
	protected long getApproximateJsonSizeInBytes() {
		return 0;
	}
}
