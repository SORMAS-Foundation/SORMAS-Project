/*******************************************************************************
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
 *******************************************************************************/
package de.symeda.sormas.backend.report;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.report.WeeklyReportDto;
import de.symeda.sormas.api.report.WeeklyReportFacade;
import de.symeda.sormas.api.report.WeeklyReportReferenceDto;
import de.symeda.sormas.api.report.WeeklyReportSummaryDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.report.WeeklyReportEntryFacadeEjb.WeeklyReportEntryFacadeEjbLocal;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DateHelper8;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "WeeklyReportFacade")
public class WeeklyReportFacadeEjb implements WeeklyReportFacade {

	@EJB
	private WeeklyReportService weeklyReportService;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private UserService userService;	
	@EJB
	private TaskService taskService;
	@EJB
	FacilityFacadeEjbLocal facilityFacade;
	@EJB
	private WeeklyReportEntryFacadeEjbLocal weeklyReportEntryFacade;
	@EJB
	private UserFacadeEjbLocal userFacade;
	
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
	public WeeklyReportDto getByUuid(String uuid) {
		return toDto(weeklyReportService.getByUuid(uuid));
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

	@Override
	public List<WeeklyReportSummaryDto> getSummariesPerRegion(EpiWeek epiWeek) {
		return weeklyReportService.getWeeklyReportSummariesPerRegion(epiWeek);
	}
	
	@Override
	public List<WeeklyReportSummaryDto> getSummariesPerDistrict(RegionReferenceDto regionRef, EpiWeek epiWeek) {
		Region region = regionService.getByReferenceDto(regionRef);
		
		return weeklyReportService.getWeeklyReportSummariesPerDistrict(region, epiWeek);
	}
	
	@Override
	public WeeklyReportSummaryDto getSummaryDtoByRegion(RegionReferenceDto regionRef, EpiWeek epiWeek) {
		List<FacilityReferenceDto> facilities = facilityFacade.getHealthFacilitiesByRegion(regionRef, false);
	
		return buildSummaryDto(facilities, regionRef, null, epiWeek);
	}
	
	@Override
	public WeeklyReportSummaryDto getSummaryDtoByDistrict(DistrictReferenceDto districtRef, EpiWeek epiWeek) {
		List<FacilityReferenceDto> facilities = facilityFacade.getHealthFacilitiesByDistrict(districtRef, false);
		
		return buildSummaryDto(facilities, null, districtRef, epiWeek);
	}
	
	@Override
	public int getNumberOfWeeklyReportsByFacility(FacilityReferenceDto facilityRef, EpiWeek epiWeek) {
		Facility facility = facilityService.getByReferenceDto(facilityRef);
		
		return (int) weeklyReportService.getNumberOfWeeklyReportsByFacility(facility, epiWeek);
	}
	
	@Override
	public List<WeeklyReportReferenceDto> getWeeklyReportsByFacility(FacilityReferenceDto facilityRef, EpiWeek epiWeek) {
		Facility facility = facilityService.getByReferenceDto(facilityRef);
		
		return weeklyReportService.getByFacility(facility, epiWeek)
				.stream()
				.map(r -> toReferenceDto(r))
				.collect(Collectors.toList());
	}
	
	@Override
	public WeeklyReportReferenceDto getByEpiWeekAndUser(EpiWeek epiWeek, UserReferenceDto userRef) {
		User user = userService.getByReferenceDto(userRef);
		
		return toReferenceDto(weeklyReportService.getByEpiWeekAndUser(epiWeek, user));
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
		WeeklyReportReferenceDto dto = new WeeklyReportReferenceDto(entity.getUuid(), entity.toString());
		return dto;
	}
	
	public static WeeklyReportDto toDto(WeeklyReport source) {
		if (source == null) {
			return null;
		}
		WeeklyReportDto target = new WeeklyReportDto();
		DtoHelper.fillDto(target, source);
		
		target.setHealthFacility(FacilityFacadeEjb.toReferenceDto(source.getHealthFacility()));
		target.setInformant(UserFacadeEjb.toReferenceDto(source.getInformant()));
		target.setReportDateTime(source.getReportDateTime());
		target.setTotalNumberOfCases(source.getTotalNumberOfCases());
		target.setYear(source.getYear());
		target.setEpiWeek(source.getEpiWeek());
		
		return target;
	}
	
	private WeeklyReportSummaryDto buildSummaryDto(List<FacilityReferenceDto> facilities, RegionReferenceDto regionRef, DistrictReferenceDto districtRef, EpiWeek epiWeek) {
		WeeklyReportSummaryDto dto = new WeeklyReportSummaryDto();
		int numberOfReports = 0;
		int numberOfZeroReports = 0;
		int numberOfMissingReports = 0;
		for (FacilityReferenceDto facility : facilities) {
			int numberOfInformants = userFacade.getNumberOfInformantsByFacility(facility);
			int numberOfWeeklyReports = getNumberOfWeeklyReportsByFacility(facility, epiWeek);
			if (numberOfInformants != numberOfWeeklyReports) {
				numberOfMissingReports++;
				continue;
			}
			
			List<WeeklyReportReferenceDto> reports = getWeeklyReportsByFacility(facility, epiWeek);
			int numberOfReportsForFacility = 0;
			for (WeeklyReportReferenceDto report : reports) {
				int nonZeroEntries = weeklyReportEntryFacade.getNumberOfNonZeroEntries(report);
				if (nonZeroEntries > 0) {
					numberOfReportsForFacility++;
					continue;
				}
			}
			
			if (numberOfReportsForFacility > 0) {
				numberOfReports++;
			} else {
				numberOfZeroReports++;
			}
		}
		
		dto.setRegion(regionRef);
		dto.setDistrict(districtRef);
		dto.setFacilities(facilities.size());
		dto.setReports(numberOfReports);
		dto.setZeroReports(numberOfZeroReports);
		dto.setMissingReports(numberOfMissingReports);
		
		int totalReportsCount = numberOfReports + numberOfZeroReports + numberOfMissingReports;
		dto.setReportsPercentage(100 / totalReportsCount * numberOfReports);
		dto.setZeroReportsPercentage(100 / totalReportsCount * numberOfZeroReports);
		dto.setMissingReportsPercentage(100 / totalReportsCount * numberOfMissingReports);
		
		return dto;
	}
	
	@RolesAllowed(UserRole._SYSTEM)
	public void generateSubmitWeeklyReportTasks() {
		List<User> informants = userService.getAllByRegionAndUserRoles(null, UserRole.INFORMANT);
		EpiWeek prevEpiWeek = DateHelper.getPreviousEpiWeek(new Date());
		
		for (User user : informants) {
			WeeklyReport report = weeklyReportService.getByEpiWeekAndUser(prevEpiWeek, user);
			
			if (report != null) {
				// A Weekly Report for last week has been found, so there is no need to create a task
				continue;
			} else {
				TaskCriteria pendingUserTaskCriteria = new TaskCriteria()
						.taskTypeEquals(TaskType.WEEKLY_REPORT_GENERATION)
						.assigneeUserEquals(user.toReference())
						.taskStatusEquals(TaskStatus.PENDING);
				List<Task> existingTasks = taskService.findBy(pendingUserTaskCriteria);
				
				if (!existingTasks.isEmpty()) {
					// There is already a task for generating the Weekly Report for last week
					continue;
				}
				
				// Create the task
				LocalDateTime fromDateTime = LocalDate.now().atStartOfDay();
				LocalDateTime toDateTime = fromDateTime.plusDays(1);
				
				Task task = taskService.buildTask(null);
				task.setTaskContext(TaskContext.GENERAL);
				task.setTaskType(TaskType.WEEKLY_REPORT_GENERATION);
				task.setSuggestedStart(DateHelper8.toDate(fromDateTime));
				task.setDueDate(DateHelper8.toDate(toDateTime.minusMinutes(1)));
				task.setAssigneeUser(user);
				taskService.ensurePersisted(task);
			}
		}
	}
	
	@LocalBean
	@Stateless
	public static class WeeklyReportFacadeEjbLocal extends WeeklyReportFacadeEjb {
	}

}
