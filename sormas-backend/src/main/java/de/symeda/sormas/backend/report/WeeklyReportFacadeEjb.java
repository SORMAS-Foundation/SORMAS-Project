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

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.report.WeeklyReportCriteria;
import de.symeda.sormas.api.report.WeeklyReportDto;
import de.symeda.sormas.api.report.WeeklyReportEntryDto;
import de.symeda.sormas.api.report.WeeklyReportFacade;
import de.symeda.sormas.api.report.WeeklyReportOfficerSummaryDto;
import de.symeda.sormas.api.report.WeeklyReportReferenceDto;
import de.symeda.sormas.api.report.WeeklyReportRegionSummaryDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.region.CommunityFacadeEjb;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
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

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private WeeklyReportService weeklyReportService;
	@EJB
	private WeeklyReportEntryService weeklyReportEntryService;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private UserService userService;
	@EJB
	private TaskService taskService;
	@EJB
	FacilityFacadeEjbLocal facilityFacade;
	@EJB
	private UserFacadeEjbLocal userFacade;
	
	@Override
	public List<WeeklyReportDto> getAllWeeklyReportsAfter(Date date) {

		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		return weeklyReportService.getAllAfter(date, user).stream()
				.map(WeeklyReportFacadeEjb::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public List<WeeklyReportDto> getByUuids(List<String> uuids) {
		return weeklyReportService.getByUuids(uuids).stream().map(r -> toDto(r)).collect(Collectors.toList());
	}

	@Override
	public WeeklyReportDto getByUuid(String uuid) {
		return toDto(weeklyReportService.getByUuid(uuid));
	}

	@Override
	public WeeklyReportDto saveWeeklyReport(WeeklyReportDto dto) {
		// Don't create a new report if there already is one in the database for the user/epi week combination
		WeeklyReportDto existingReport = getByEpiWeekAndUser(new EpiWeek(dto.getYear(), dto.getEpiWeek()), dto.getReportingUser());
		if (existingReport != null && !dto.getUuid().equals(existingReport.getUuid())) {
			logger.warn("Tried to create a new report for an already existing user/epi week combination (existing UUID: " + existingReport.getUuid() + "); report was not created");
			return null;
		}
		
		WeeklyReport report = fromDto(dto);
		weeklyReportService.ensurePersisted(report);
		return toDto(report);
	}

	@Override
	public List<String> getAllUuids() {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return weeklyReportService.getAllUuids();
	}

	@Override
	public List<WeeklyReportRegionSummaryDto> getSummariesPerRegion(EpiWeek epiWeek) {

		//Only for national users
		if (! UserRole.isNational(userService.getCurrentUser().getUserRoles())) {
			return new ArrayList<>();
		}
		
		List<WeeklyReportRegionSummaryDto> summaryDtos = new ArrayList<>();

		WeeklyReportCriteria regionReportCriteria = new WeeklyReportCriteria().epiWeek(epiWeek);

		List<Region> regions = regionService.getAll(Region.NAME, true);

		for (Region region : regions) {

			WeeklyReportRegionSummaryDto summaryDto = new WeeklyReportRegionSummaryDto();
			summaryDto.setRegion(RegionFacadeEjb.toReferenceDto(region));

			Long officers = userService.countByRegion(region, UserRole.SURVEILLANCE_OFFICER);
			if (officers.intValue() == 0) {
				continue; // summarize only regions that do have officers
			}
			
			summaryDto.setOfficers(officers.intValue());
			Long informants = userService.countByRegion(region, UserRole.HOSPITAL_INFORMANT,
					UserRole.COMMUNITY_INFORMANT);
			summaryDto.setInformants(informants.intValue());

			regionReportCriteria.reportingUserRegion(summaryDto.getRegion());
			regionReportCriteria.officerReport(true);
			regionReportCriteria.zeroReport(false);
			Long officerCaseReports = weeklyReportService.countByCriteria(regionReportCriteria, null);
			summaryDto.setOfficerCaseReports(officerCaseReports.intValue());
			regionReportCriteria.zeroReport(true);
			Long officerZeroReports = weeklyReportService.countByCriteria(regionReportCriteria, null);
			summaryDto.setOfficerZeroReports(officerZeroReports.intValue());

			regionReportCriteria.officerReport(false);
			regionReportCriteria.zeroReport(false);
			Long informantCaseReports = weeklyReportService.countByCriteria(regionReportCriteria, null);
			summaryDto.setInformantCaseReports(informantCaseReports.intValue());
			regionReportCriteria.zeroReport(true);
			Long informantZeroReports = weeklyReportService.countByCriteria(regionReportCriteria, null);
			summaryDto.setInformantZeroReports(informantZeroReports.intValue());

			summaryDtos.add(summaryDto);
		}

		return summaryDtos;
	}

	@Override
	public List<WeeklyReportOfficerSummaryDto> getSummariesPerOfficer(RegionReferenceDto regionRef, EpiWeek epiWeek) {
		
		WeeklyReportCriteria officerReportCriteria = new WeeklyReportCriteria().epiWeek(epiWeek);
		WeeklyReportCriteria informantsReportCriteria = new WeeklyReportCriteria().epiWeek(epiWeek).officerReport(false);

		Region region = regionService.getByReferenceDto(regionRef);
		
		Stream<User> officers = userService.getAllByRegionAndUserRoles(region, UserRole.SURVEILLANCE_OFFICER).stream();
		officers = weeklyReportService.filterWeeklyReportUsers(userService.getCurrentUser(), officers);
		
		List<WeeklyReportOfficerSummaryDto> summaryDtos = officers
		.sorted(Comparator.comparing(a -> a.getDistrict().getName()))
		.map(officer -> {

			WeeklyReportOfficerSummaryDto summaryDto = new WeeklyReportOfficerSummaryDto();
			summaryDto.setOfficer(UserFacadeEjb.toReferenceDto(officer));
			summaryDto.setDistrict(DistrictFacadeEjb.toReferenceDto(officer.getDistrict()));

			{
				officerReportCriteria.reportingUser(new UserReferenceDto(officer.getUuid()));
				weeklyReportService.queryByCriteria(officerReportCriteria, null, null, true).stream()
				.findFirst()
				.ifPresent(officerReport -> {
					summaryDto.setOfficerReportDate(officerReport.getReportDateTime());
					summaryDto.setTotalCaseCount(officerReport.getTotalNumberOfCases());
				});
			}

			{
				Long informants = userService.countByAssignedOfficer(officer, 
						UserRole.HOSPITAL_INFORMANT,
						UserRole.COMMUNITY_INFORMANT);
				summaryDto.setInformants(informants.intValue());
			}

			informantsReportCriteria.assignedOfficer(summaryDto.getOfficer());
			{
				informantsReportCriteria.zeroReport(false);
				Long informantCaseReports = weeklyReportService.countByCriteria(informantsReportCriteria, null);
				summaryDto.setInformantCaseReports(informantCaseReports.intValue());
			}
			{
				informantsReportCriteria.zeroReport(true);
				Long informantZeroReports = weeklyReportService.countByCriteria(informantsReportCriteria, null);
				summaryDto.setInformantZeroReports(informantZeroReports.intValue());
			}

			return summaryDto;
		})
		.collect(Collectors.toList());
		

		return summaryDtos;
	}

	@Override
	public WeeklyReportDto getByEpiWeekAndUser(EpiWeek epiWeek, UserReferenceDto userRef) {
		User user = userService.getByReferenceDto(userRef);

		return toDto(weeklyReportService.getByEpiWeekAndUser(epiWeek, user));
	}

	public WeeklyReport fromDto(@NotNull WeeklyReportDto source) {

		WeeklyReport target = weeklyReportService.getByUuid(source.getUuid());
		if (target == null) {
			target = new WeeklyReport();
			target.setUuid(source.getUuid());
			target.setReportDateTime(new Date());
		}
		DtoHelper.validateDto(source, target);

		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setReportDateTime(source.getReportDateTime());
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setCommunity(communityService.getByReferenceDto(source.getCommunity()));
		target.setHealthFacility(facilityService.getByReferenceDto(source.getHealthFacility()));
		target.setAssignedOfficer(userService.getByReferenceDto(source.getAssignedOfficer()));
		target.setTotalNumberOfCases(source.getTotalNumberOfCases());
		target.setYear(source.getYear());
		target.setEpiWeek(source.getEpiWeek());

		List<WeeklyReportEntry> entries = new ArrayList<>();
		for (WeeklyReportEntryDto entryDto : source.getReportEntries()) {
			WeeklyReportEntry entry = fromDto(entryDto);
			entry.setWeeklyReport(target);
			entries.add(entry);
		}
		if (!DataHelper.equal(target.getReportEntries(), entries)) {
			target.setChangeDateOfEmbeddedLists(new Date());
		}
		target.getReportEntries().clear();
		target.getReportEntries().addAll(entries);

		return target;
	}

	public WeeklyReportEntry fromDto(WeeklyReportEntryDto source) {
		if (source == null) {
			return null;
		}

		WeeklyReportEntry target = weeklyReportEntryService.getByUuid(source.getUuid());
		if (target == null) {
			target = new WeeklyReportEntry();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}

		DtoHelper.validateDto(source, target);

		target.setDisease(source.getDisease());
		target.setNumberOfCases(source.getNumberOfCases());

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

		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setReportDateTime(source.getReportDateTime());
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setCommunity(CommunityFacadeEjb.toReferenceDto(source.getCommunity()));
		target.setHealthFacility(FacilityFacadeEjb.toReferenceDto(source.getHealthFacility()));
		target.setAssignedOfficer(UserFacadeEjb.toReferenceDto(source.getAssignedOfficer()));
		target.setTotalNumberOfCases(source.getTotalNumberOfCases());
		target.setYear(source.getYear());
		target.setEpiWeek(source.getEpiWeek());

		List<WeeklyReportEntryDto> entryDtos = new ArrayList<>();
		for (WeeklyReportEntry entry : source.getReportEntries()) {
			WeeklyReportEntryDto entryDto = toDto(entry);
			entryDtos.add(entryDto);
		}
		target.setReportEntries(entryDtos);

		return target;
	}

	public static WeeklyReportEntryDto toDto(WeeklyReportEntry source) {
		if (source == null) {
			return null;
		}

		WeeklyReportEntryDto target = new WeeklyReportEntryDto();

		target.setCreationDate(source.getCreationDate());
		target.setChangeDate(source.getChangeDate());
		target.setUuid(source.getUuid());

		target.setDisease(source.getDisease());
		target.setNumberOfCases(source.getNumberOfCases());

		return target;
	}

	@RolesAllowed(UserRole._SYSTEM)
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void generateSubmitWeeklyReportTasks() {
		List<User> informants = userService.getAllByRegionAndUserRoles(null, UserRole.HOSPITAL_INFORMANT);
		EpiWeek prevEpiWeek = DateHelper.getPreviousEpiWeek(new Date());

		for (User user : informants) {
			WeeklyReport report = weeklyReportService.getByEpiWeekAndUser(prevEpiWeek, user);

			if (report != null) {
				// A Weekly Report for last week has been found, so there is no need to create a
				// task
				continue;
			} else {
				TaskCriteria pendingUserTaskCriteria = new TaskCriteria()
						.taskType(TaskType.WEEKLY_REPORT_GENERATION).assigneeUser(user.toReference())
						.taskStatus(TaskStatus.PENDING);
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
