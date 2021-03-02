package de.symeda.sormas.app.backend.activityascase;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.activityascase.ActivityAsCaseDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

public class ActivityAsCaseDtoHelper extends AdoDtoHelper<ActivityAsCase, ActivityAsCaseDto> {

	private final LocationDtoHelper locationDtoHelper;

	public ActivityAsCaseDtoHelper() {
		locationDtoHelper = new LocationDtoHelper();
	}

	@Override
	protected Class<ActivityAsCase> getAdoClass() {
		return ActivityAsCase.class;
	}

	@Override
	protected Class<ActivityAsCaseDto> getDtoClass() {
		return ActivityAsCaseDto.class;
	}

	@Override
	protected Call<List<ActivityAsCaseDto>> pullAllSince(long since) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<ActivityAsCaseDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<ActivityAsCaseDto> exposureDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected void fillInnerFromDto(ActivityAsCase target, ActivityAsCaseDto source) {

		// Info: Epi Data is set by calling method

		target.setReportingUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getReportingUser()));
		target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());
		target.setDescription(source.getDescription());
		target.setActivityAsCaseType(source.getActivityAsCaseType());
		target.setActivityAsCaseTypeDetails(source.getActivityAsCaseTypeDetails());
		target.setLocation(locationDtoHelper.fillOrCreateFromDto(target.getLocation(), source.getLocation()));
		target.setRole(source.getRole());

		target.setTypeOfPlace(source.getTypeOfPlace());
		target.setTypeOfPlaceDetails(source.getTypeOfPlaceDetails());
		target.setMeansOfTransport(source.getMeansOfTransport());
		target.setMeansOfTransportDetails(source.getMeansOfTransportDetails());
		target.setConnectionNumber(source.getConnectionNumber());
		target.setSeatNumber(source.getSeatNumber());
		target.setWorkEnvironment(source.getWorkEnvironment());

		target.setGatheringDetails(source.getGatheringDetails());
		target.setGatheringType(source.getGatheringType());
		target.setHabitationDetails(source.getHabitationDetails());
		target.setHabitationType(source.getHabitationType());
	}

	@Override
	protected void fillInnerFromAdo(ActivityAsCaseDto target, ActivityAsCase source) {

		if (source.getLocation() != null) {
			Location location = DatabaseHelper.getLocationDao().queryForId(source.getLocation().getId());
			target.setLocation(locationDtoHelper.adoToDto(location));
		} else {
			target.setLocation(null);
		}

		if (source.getReportingUser() != null) {
			User reportingUser = DatabaseHelper.getUserDao().queryForId(source.getReportingUser().getId());
			target.setReportingUser(UserDtoHelper.toReferenceDto(reportingUser));
		} else {
			target.setReportingUser(null);
		}

		target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());
		target.setDescription(source.getDescription());
		target.setActivityAsCaseType(source.getActivityAsCaseType());
		target.setActivityAsCaseTypeDetails(source.getActivityAsCaseTypeDetails());
		target.setRole(source.getRole());

		target.setTypeOfPlace(source.getTypeOfPlace());
		target.setTypeOfPlaceDetails(source.getTypeOfPlaceDetails());
		target.setMeansOfTransport(source.getMeansOfTransport());
		target.setMeansOfTransportDetails(source.getMeansOfTransportDetails());
		target.setConnectionNumber(source.getConnectionNumber());
		target.setSeatNumber(source.getSeatNumber());
		target.setWorkEnvironment(source.getWorkEnvironment());

		target.setGatheringDetails(source.getGatheringDetails());
		target.setGatheringType(source.getGatheringType());
		target.setHabitationDetails(source.getHabitationDetails());
		target.setHabitationType(source.getHabitationType());
	}
}
