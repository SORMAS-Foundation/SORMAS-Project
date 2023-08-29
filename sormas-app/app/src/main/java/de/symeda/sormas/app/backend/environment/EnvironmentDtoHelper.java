package de.symeda.sormas.app.backend.environment;

import java.util.List;

import de.symeda.sormas.api.PostResponse;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class EnvironmentDtoHelper extends AdoDtoHelper<Environment, EnvironmentDto> {

	private LocationDtoHelper locationHelper;

	public EnvironmentDtoHelper() {
		this.locationHelper = new LocationDtoHelper();
	}

	@Override
	protected Class<Environment> getAdoClass() {
		return Environment.class;
	}

	@Override
	protected Class<EnvironmentDto> getDtoClass() {
		return EnvironmentDto.class;
	}

	@Override
	protected Call<List<EnvironmentDto>> pullAllSince(long since, Integer size, String lastSynchronizedUuid) throws NoConnectionException {
		return RetroProvider.getEnvironmentFacade().pullAllSince(since, size, lastSynchronizedUuid);
	}

	@Override
	protected Call<List<EnvironmentDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getEnvironmentFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PostResponse>> pushAll(List<EnvironmentDto> environmentDtos) throws NoConnectionException {
		return RetroProvider.getEnvironmentFacade().pushAll(environmentDtos);
	}

	@Override
	protected void fillInnerFromDto(Environment target, EnvironmentDto source) {

		target.setReportDate(source.getReportDate());
		target.setReportingUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getReportingUser()));
		target.setEnvironmentName(source.getEnvironmentName());
		target.setDescription(source.getDescription());
		target.setExternalId(source.getExternalId());
		target.setResponsibleUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getResponsibleUser()));
		target.setInvestigationStatus(source.getInvestigationStatus());
		target.setEnvironmentMedia(source.getEnvironmentMedia());
		target.setWaterType(source.getWaterType());
		target.setOtherWaterType(source.getOtherWaterType());
		target.setInfrastructureDetails(source.getInfrastructureDetails());
		target.setOtherInfrastructureDetails(source.getOtherInfrastructureDetails());
		target.setWateruse(source.getWaterUse());
		target.setOtherWaterUse(source.getOtherWaterUse());
		target.setLocation(locationHelper.fillOrCreateFromDto(target.getLocation(), source.getLocation()));
	}

	@Override
	protected void fillInnerFromAdo(EnvironmentDto target, Environment source) {

		target.setReportDate(source.getReportDate());
		if (source.getReportingUser() != null) {
			User user = DatabaseHelper.getUserDao().queryForId(source.getReportingUser().getId());
			target.setReportingUser(UserDtoHelper.toReferenceDto(user));
		} else {
			target.setReportingUser(null);
		}
		target.setEnvironmentName(source.getEnvironmentName());
		target.setDescription(source.getDescription());
		target.setExternalId(source.getExternalId());
		if (source.getResponsibleUser() != null) {
			User user = DatabaseHelper.getUserDao().queryForId(source.getResponsibleUser().getId());
			target.setResponsibleUser(UserDtoHelper.toReferenceDto(user));
		} else {
			target.setResponsibleUser(null);
		}
		target.setInvestigationStatus(source.getInvestigationStatus());
		target.setEnvironmentMedia(source.getEnvironmentMedia());
		target.setWaterType(source.getWaterType());
		target.setOtherWaterType(source.getOtherWaterType());
		target.setInfrastructureDetails(source.getInfrastructureDetails());
		target.setOtherInfrastructureDetails(source.getOtherInfrastructureDetails());
		target.setWaterUse(source.getWateruse());
		target.setOtherWaterUse(source.getOtherWaterUse());
		if (source.getLocation() != null) {
			Location location = DatabaseHelper.getLocationDao().queryForId(source.getLocation().getId());
			target.setLocation(locationHelper.adoToDto(location));
		} else {
			target.setLocation(null);
		}
	}

	@Override
	protected long getApproximateJsonSizeInBytes() {
		return EnvironmentDto.APPROXIMATE_JSON_SIZE_IN_BYTES;
	}

	public static EnvironmentReferenceDto toReferenceDto(Environment environment) {
		if (environment == null) {
			return null;
		}

		return new EnvironmentReferenceDto(environment.getUuid(), environment.getEnvironmentName());
	}
}
