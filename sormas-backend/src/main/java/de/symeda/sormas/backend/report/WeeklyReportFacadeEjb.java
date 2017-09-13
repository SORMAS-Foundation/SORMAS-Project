package de.symeda.sormas.backend.report;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.report.WeeklyReportDto;
import de.symeda.sormas.api.report.WeeklyReportFacade;
import de.symeda.sormas.api.report.WeeklyReportReferenceDto;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "WeeklyReportFacade")
public class WeeklyReportFacadeEjb implements WeeklyReportFacade {

	@EJB
	private WeeklyReportService weeklyReportService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private UserService userService;	
	
	@Override
	public List<WeeklyReportDto> getAllWeeklyReportsAfter(Date date, String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return weeklyReportService.getAllAfter(date, user).stream()
				.map(r -> toDto(r))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<WeeklyReportDto> getByUuids(List<String> uuids) {
		return weeklyReportService.getByUuids(uuids)
				.stream()
				.map(r -> toDto(r))
				.collect(Collectors.toList());
	}
	
	@Override
	public WeeklyReportDto saveWeeklyReport(WeeklyReportDto dto) {
		WeeklyReport report = fromDto(dto);
		weeklyReportService.ensurePersisted(report);
		return toDto(report);
	}
	
	@Override
	public List<String> getAllUuids(String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return weeklyReportService.getAllUuids(user);
	}
	
	public WeeklyReport fromDto(@NotNull WeeklyReportDto source) {
		
		WeeklyReport target = weeklyReportService.getByUuid(source.getUuid());
		if (target == null) {
			target = new WeeklyReport();
			target.setUuid(source.getUuid());
			target.setReportDateTime(new Date());
		}
		DtoHelper.validateDto(source, target);
		
		target.setHealthFacility(facilityService.getByReferenceDto(source.getHealthFacility()));
		target.setInformant(userService.getByReferenceDto(source.getInformant()));
		target.setReportDateTime(source.getReportDateTime());
		target.setTotalNumberOfCases(source.getTotalNumberOfCases());
		target.setYear(source.getYear());
		target.setEpiWeek(source.getEpiWeek());
		
		return target;
	}
	
	public static WeeklyReportReferenceDto toReferenceDto(WeeklyReport entity) {
		if (entity == null) {
			return null;
		}
		WeeklyReportReferenceDto dto = new WeeklyReportReferenceDto();
		DtoHelper.fillReferenceDto(dto, entity);
		return dto;
	}
	
	public static WeeklyReportDto toDto(WeeklyReport source) {
		if (source == null) {
			return null;
		}
		WeeklyReportDto target = new WeeklyReportDto();
		DtoHelper.fillReferenceDto(target, source);
		
		target.setHealthFacility(FacilityFacadeEjb.toReferenceDto(source.getHealthFacility()));
		target.setInformant(UserFacadeEjb.toReferenceDto(source.getInformant()));
		target.setReportDateTime(source.getReportDateTime());
		target.setTotalNumberOfCases(source.getTotalNumberOfCases());
		target.setYear(source.getYear());
		target.setEpiWeek(source.getEpiWeek());
		
		return target;
	}

}
