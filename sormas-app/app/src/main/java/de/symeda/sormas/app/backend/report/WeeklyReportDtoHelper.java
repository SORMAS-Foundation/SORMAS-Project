/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.report;

import java.util.List;

import de.symeda.sormas.api.report.WeeklyReportDto;
import de.symeda.sormas.api.report.WeeklyReportReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

/**
 * Created by Mate Strysewske on 12.09.2017.
 */
public class WeeklyReportDtoHelper extends AdoDtoHelper<WeeklyReport, WeeklyReportDto> {

    @Override
    protected Class<WeeklyReport> getAdoClass() {
        return WeeklyReport.class;
    }

    @Override
    protected Class<WeeklyReportDto> getDtoClass() {
        return WeeklyReportDto.class;
    }

    @Override
    protected Call<List<WeeklyReportDto>> pullAllSince(long since) {
        return RetroProvider.getWeeklyReportFacade().pullAllSince(since);
    }

    @Override
    protected Call<List<WeeklyReportDto>> pullByUuids(List<String> uuids) {
        return RetroProvider.getWeeklyReportFacade().pullByUuids(uuids);
    }

    @Override
    protected Call<Integer> pushAll(List<WeeklyReportDto> weeklyReportDtos) {
        return RetroProvider.getWeeklyReportFacade().pushAll(weeklyReportDtos);
    }

    @Override
    public void fillInnerFromDto(WeeklyReport target, WeeklyReportDto source) {
        target.setHealthFacility(DatabaseHelper.getFacilityDao().getByReferenceDto(source.getHealthFacility()));
        target.setInformant(DatabaseHelper.getUserDao().getByReferenceDto(source.getInformant()));
        target.setReportDateTime(source.getReportDateTime());
        target.setTotalNumberOfCases(source.getTotalNumberOfCases());
        target.setYear(source.getYear());
        target.setEpiWeek(source.getEpiWeek());
    }

    @Override
    public void fillInnerFromAdo(WeeklyReportDto target, WeeklyReport source) {
        if (source.getHealthFacility() != null) {
            Facility facility = DatabaseHelper.getFacilityDao().queryForId(source.getHealthFacility().getId());
            target.setHealthFacility(FacilityDtoHelper.toReferenceDto(facility));
        } else {
            target.setHealthFacility(null);
        }

        if (source.getInformant() != null) {
            User informant = DatabaseHelper.getUserDao().queryForId(source.getInformant().getId());
            target.setInformant(UserDtoHelper.toReferenceDto(informant));
        } else {
            target.setInformant(null);
        }

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