package de.symeda.sormas.app.backend.report;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.report.AggregateReportDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.app.backend.infrastructure.PointOfEntryDtoHelper;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.DistrictDtoHelper;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class AggregateReportDtoHelper extends AdoDtoHelper<AggregateReport, AggregateReportDto> {

	@Override
	protected Class<AggregateReport> getAdoClass() {
		return AggregateReport.class;
	}

	@Override
	protected Class<AggregateReportDto> getDtoClass() {
		return AggregateReportDto.class;
	}

	@Override
	protected Call<List<AggregateReportDto>> pullAllSince(long since) throws NoConnectionException {
		return RetroProvider.getAggregateReportFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<AggregateReportDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getAggregateReportFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<AggregateReportDto> aggregateReports) throws NoConnectionException {
		return RetroProvider.getAggregateReportFacade().pushAll(aggregateReports);
	}

	@Override
	public void fillInnerFromDto(AggregateReport target, AggregateReportDto source) {
		target.setReportingUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getReportingUser()));
		target.setDisease(source.getDisease());
		target.setRegion(DatabaseHelper.getRegionDao().getByReferenceDto(source.getRegion()));
		target.setDistrict(DatabaseHelper.getDistrictDao().getByReferenceDto(source.getDistrict()));
		target.setHealthFacility(DatabaseHelper.getFacilityDao().getByReferenceDto(source.getHealthFacility()));
		target.setPointOfEntry(DatabaseHelper.getPointOfEntryDao().getByReferenceDto(source.getPointOfEntry()));
		target.setYear(source.getYear());
		target.setEpiWeek(source.getEpiWeek());
		target.setNewCases(source.getNewCases());
		target.setLabConfirmations(source.getLabConfirmations());
		target.setDeaths(source.getDeaths());
	}

	@Override
	public void fillInnerFromAdo(AggregateReportDto target, AggregateReport source) {
		if (source.getReportingUser() != null) {
			User reportingUser = DatabaseHelper.getUserDao().queryForId(source.getReportingUser().getId());
			target.setReportingUser(UserDtoHelper.toReferenceDto(reportingUser));
		} else {
			target.setReportingUser(null);
		}

		if (source.getDistrict() != null) {
			Region region = DatabaseHelper.getRegionDao().queryForId(source.getRegion().getId());
			target.setRegion(RegionDtoHelper.toReferenceDto(region));
		} else {
			target.setRegion(null);
		}

		if (source.getDistrict() != null) {
			District district = DatabaseHelper.getDistrictDao().queryForId(source.getDistrict().getId());
			target.setDistrict(DistrictDtoHelper.toReferenceDto(district));
		} else {
			target.setDistrict(null);
		}

		if (source.getHealthFacility() != null) {
			Facility facility = DatabaseHelper.getFacilityDao().queryForId(source.getHealthFacility().getId());
			target.setHealthFacility(FacilityDtoHelper.toReferenceDto(facility));
		} else {
			target.setHealthFacility(null);
		}

		if (source.getPointOfEntry() != null) {
			PointOfEntry pointOfEntry = DatabaseHelper.getPointOfEntryDao().queryForId(source.getPointOfEntry().getId());
			target.setPointOfEntry(PointOfEntryDtoHelper.toReferenceDto(pointOfEntry));
		} else {
			target.setPointOfEntry(null);
		}

		target.setDisease(source.getDisease());
		target.setYear(source.getYear());
		target.setEpiWeek(source.getEpiWeek());
		target.setNewCases(source.getNewCases());
		target.setLabConfirmations(source.getLabConfirmations());
		target.setDeaths(source.getDeaths());
	}
}
