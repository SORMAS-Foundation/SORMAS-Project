package de.symeda.sormas.app.backend.environment.environmentsample;

import java.util.List;

import de.symeda.sormas.api.PostResponse;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.environment.EnvironmentDtoHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class EnvironmentSampleDtoHelper extends AdoDtoHelper<EnvironmentSample, EnvironmentSampleDto> {

	private EnvironmentDtoHelper environmentHelper;
	private LocationDtoHelper locationHelper;

	public EnvironmentSampleDtoHelper() {
		environmentHelper = new EnvironmentDtoHelper();
		locationHelper = new LocationDtoHelper();
	}

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
	protected void fillInnerFromDto(EnvironmentSample target, EnvironmentSampleDto source) {
		target.setEnvironment(DatabaseHelper.getEnvironmentDao().getByReferenceDto(source.getEnvironment()));
		target.setReportDate(source.getReportDate());
		target.setReportingUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getReportingUser()));
		target.setSampleDateTime(source.getSampleDateTime());
		target.setSampleMaterial(source.getSampleMaterial());
		target.setOtherSampleMaterial(source.getOtherSampleMaterial());
		target.setSampleVolume(source.getSampleVolume());
		target.setFieldSampleId(source.getFieldSampleId());
		target.setTurbidity(source.getTurbidity());
		target.setPhValue(source.getPhValue());
		target.setSampleTemperature(source.getSampleTemperature());
		target.setChlorineResiduals(source.getChlorineResiduals());
		target.setLaboratory(DatabaseHelper.getFacilityDao().getByReferenceDto(source.getLaboratory()));
		target.setLaboratoryDetails(source.getLaboratoryDetails());
		target.setRequestedPathogenTests(source.getRequestedPathogenTests());
		target.setOtherRequestedPathogenTests(source.getOtherRequestedPathogenTests());
		target.setWeatherConditions(source.getWeatherConditions());
		target.setHeavyRain(source.getHeavyRain());
		target.setDispatched(source.isDispatched());
		target.setDispatchDate(source.getDispatchDate());
		target.setDispatchDetails(source.getDispatchDetails());
		target.setReceived(source.isReceived());
		target.setReceivalDate(source.getReceivalDate());
		target.setLabSampleId(source.getLabSampleId());
		target.setSpecimenCondition(source.getSpecimenCondition());
		target.setLocation(locationHelper.fillOrCreateFromDto(target.getLocation(), source.getLocation()));
		target.setGeneralComment(source.getGeneralComment());
	}

	@Override
	protected void fillInnerFromAdo(EnvironmentSampleDto target, EnvironmentSample source) {
		target.setEnvironment(EnvironmentDtoHelper.toReferenceDto(DatabaseHelper.getEnvironmentDao().queryForId(source.getEnvironment().getId())));
		target.setReportDate(source.getReportDate());
		target.setReportingUser(UserDtoHelper.toReferenceDto(DatabaseHelper.getUserDao().queryForId(source.getReportingUser().getId())));
		target.setSampleDateTime(source.getSampleDateTime());
		target.setSampleMaterial(source.getSampleMaterial());
		target.setOtherSampleMaterial(source.getOtherSampleMaterial());
		target.setSampleVolume(source.getSampleVolume());
		target.setFieldSampleId(source.getFieldSampleId());
		target.setTurbidity(source.getTurbidity());
		target.setPhValue(source.getPhValue());
		target.setSampleTemperature(source.getSampleTemperature());
		target.setChlorineResiduals(source.getChlorineResiduals());
		Facility lab = DatabaseHelper.getFacilityDao().queryForId(source.getLaboratory().getId());
		target.setLaboratory(lab != null ? FacilityDtoHelper.toReferenceDto(lab) : null);
		target.setLaboratoryDetails(source.getLaboratoryDetails());
		target.setRequestedPathogenTests(source.getRequestedPathogenTests());
		target.setOtherRequestedPathogenTests(source.getOtherRequestedPathogenTests());
		target.setWeatherConditions(source.getWeatherConditions());
		target.setHeavyRain(source.getHeavyRain());
		target.setDispatched(source.isDispatched());
		target.setDispatchDate(source.getDispatchDate());
		target.setDispatchDetails(source.getDispatchDetails());
		target.setReceived(source.isReceived());
		target.setReceivalDate(source.getReceivalDate());
		target.setLabSampleId(source.getLabSampleId());
		target.setSpecimenCondition(source.getSpecimenCondition());
		target.setLocation(locationHelper.adoToDto(DatabaseHelper.getLocationDao().queryForId(source.getLocation().getId())));
		target.setGeneralComment(source.getGeneralComment());
	}

	@Override
	protected long getApproximateJsonSizeInBytes() {
		return EnvironmentSampleDto.APPROXIMATE_JSON_SIZE_IN_BYTES;
	}
}
