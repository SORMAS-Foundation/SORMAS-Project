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
        WeeklyReportReferenceDto dto = new WeeklyReportReferenceDto();
        fillReferenceDto(dto, ado);

        return dto;
    }

}