/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.report;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.report.WeeklyReportDto;
import de.symeda.sormas.api.report.WeeklyReportEntryDto;
import de.symeda.sormas.api.report.WeeklyReportReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.CommunityDtoHelper;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.DistrictDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class WeeklyReportDtoHelper extends AdoDtoHelper<WeeklyReport, WeeklyReportDto> {

	private WeeklyReportEntryDtoHelper entryDtoHelper;

	public WeeklyReportDtoHelper() {
		entryDtoHelper = new WeeklyReportEntryDtoHelper();
	}

	@Override
	protected Class<WeeklyReport> getAdoClass() {
		return WeeklyReport.class;
	}

	@Override
	protected Class<WeeklyReportDto> getDtoClass() {
		return WeeklyReportDto.class;
	}

	@Override
	protected Call<List<WeeklyReportDto>> pullAllSince(long since) throws NoConnectionException {
		return RetroProvider.getWeeklyReportFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<WeeklyReportDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getWeeklyReportFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<WeeklyReportDto> weeklyReportDtos) throws NoConnectionException {
		return RetroProvider.getWeeklyReportFacade().pushAll(weeklyReportDtos);
	}

	@Override
	public WeeklyReportDto adoToDto(WeeklyReport weeklyReport) {
		DatabaseHelper.getWeeklyReportDao().initLazyData(weeklyReport);
		return super.adoToDto(weeklyReport);
	}

	@Override
	public void fillInnerFromDto(WeeklyReport target, WeeklyReportDto source) {
		target.setReportingUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getReportingUser()));
		target.setReportDateTime(source.getReportDateTime());
		target.setDistrict(DatabaseHelper.getDistrictDao().getByReferenceDto(source.getDistrict()));
		target.setCommunity(DatabaseHelper.getCommunityDao().getByReferenceDto(source.getCommunity()));
		target.setHealthFacility(DatabaseHelper.getFacilityDao().getByReferenceDto(source.getHealthFacility()));
		target.setAssignedOfficer(DatabaseHelper.getUserDao().getByReferenceDto(source.getAssignedOfficer()));
		target.setTotalNumberOfCases(source.getTotalNumberOfCases());
		target.setYear(source.getYear());
		target.setEpiWeek(source.getEpiWeek());

		// just recreate all of this and throw the old stuff away
		List<WeeklyReportEntry> entries = new ArrayList<>();
		if (!source.getReportEntries().isEmpty()) {
			for (WeeklyReportEntryDto entryDto : source.getReportEntries()) {
				WeeklyReportEntry entry = entryDtoHelper.fillOrCreateFromDto(null, entryDto);
				entry.setWeeklyReport(target);
				entries.add(entry);
			}
		}
		target.setReportEntries(entries);
	}

	@Override
	public void fillInnerFromAdo(WeeklyReportDto target, WeeklyReport source) {
		if (source.getReportingUser() != null) {
			User reportingUser = DatabaseHelper.getUserDao().queryForId(source.getReportingUser().getId());
			target.setReportingUser(UserDtoHelper.toReferenceDto(reportingUser));
		} else {
			target.setReportingUser(null);
		}

		if (source.getDistrict() != null) {
			District district = DatabaseHelper.getDistrictDao().queryForId(source.getDistrict().getId());
			target.setDistrict(DistrictDtoHelper.toReferenceDto(district));
		} else {
			target.setDistrict(null);
		}

		if (source.getCommunity() != null) {
			Community community = DatabaseHelper.getCommunityDao().queryForId(source.getCommunity().getId());
			target.setCommunity(CommunityDtoHelper.toReferenceDto(community));
		} else {
			target.setCommunity(null);
		}

		if (source.getHealthFacility() != null) {
			Facility facility = DatabaseHelper.getFacilityDao().queryForId(source.getHealthFacility().getId());
			target.setHealthFacility(FacilityDtoHelper.toReferenceDto(facility));
		} else {
			target.setHealthFacility(null);
		}

		if (source.getAssignedOfficer() != null) {
			User assignedOfficer = DatabaseHelper.getUserDao().queryForId(source.getAssignedOfficer().getId());
			target.setAssignedOfficer(UserDtoHelper.toReferenceDto(assignedOfficer));
		} else {
			target.setAssignedOfficer(null);
		}

		List<WeeklyReportEntryDto> entryDtos = new ArrayList<>();
		if (!source.getReportEntries().isEmpty()) {
			for (WeeklyReportEntry entry : source.getReportEntries()) {
				WeeklyReportEntryDto entryDto = entryDtoHelper.adoToDto(entry);
				entryDtos.add(entryDto);
			}
		}
		target.setReportEntries(entryDtos);

		target.setReportDateTime(source.getReportDateTime());
		target.setTotalNumberOfCases(source.getTotalNumberOfCases());
		target.setYear(source.getYear());
		target.setEpiWeek(source.getEpiWeek());
	}

	public static WeeklyReportReferenceDto toReferenceDto(WeeklyReport ado) {
		if (ado == null) {
			return null;
		}
		WeeklyReportReferenceDto dto = new WeeklyReportReferenceDto(ado.getUuid());

		return dto;
	}
}
