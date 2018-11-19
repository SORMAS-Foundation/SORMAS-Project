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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.report.WeeklyReportEntryDto;
import de.symeda.sormas.api.report.WeeklyReportEntryFacade;
import de.symeda.sormas.api.report.WeeklyReportEntryReferenceDto;
import de.symeda.sormas.api.report.WeeklyReportReferenceDto;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "WeeklyReportEntryFacade")
public class WeeklyReportEntryFacadeEjb implements WeeklyReportEntryFacade {

	@EJB
	private WeeklyReportEntryService weeklyReportEntryService;
	@EJB
	private WeeklyReportService weeklyReportService;
	@EJB
	private UserService userService;		
	
	@Override
	public List<WeeklyReportEntryDto> getAllWeeklyReportEntriesAfter(Date date, String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return weeklyReportEntryService.getAllAfter(date, user).stream()
				.map(e -> toDto(e))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<WeeklyReportEntryDto> getByUuids(List<String> uuids) {
		return weeklyReportEntryService.getByUuids(uuids)
				.stream()
				.map(r -> toDto(r))
				.collect(Collectors.toList());
	}
	
	@Override
	public WeeklyReportEntryDto saveWeeklyReportEntry(WeeklyReportEntryDto dto) {
		WeeklyReportEntry reportEntry = fromDto(dto);
		weeklyReportEntryService.ensurePersisted(reportEntry);
		return toDto(reportEntry);
	}
	
	@Override
	public List<String> getAllUuids(String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return weeklyReportEntryService.getAllUuids(user);
	}
	
	@Override
	public int getNumberOfNonZeroEntries(WeeklyReportReferenceDto reportRef) {
		WeeklyReport report = weeklyReportService.getByReferenceDto(reportRef);
		
		return (int) weeklyReportEntryService.getNumberOfNonZeroEntries(report);
	}
	
	public WeeklyReportEntry fromDto(@NotNull WeeklyReportEntryDto source) {
		
		WeeklyReportEntry target = weeklyReportEntryService.getByUuid(source.getUuid());
		if (target == null) {
			target = new WeeklyReportEntry();
			target.setUuid(source.getUuid());
		}
		DtoHelper.validateDto(source, target);
		
		target.setWeeklyReport(weeklyReportService.getByReferenceDto(source.getWeeklyReport()));
		target.setDisease(source.getDisease());
		target.setNumberOfCases(source.getNumberOfCases());
		
		return target;
	}
	
	public static WeeklyReportEntryReferenceDto toReferenceDto(WeeklyReportEntry entity) {
		if (entity == null) {
			return null;
		}
		WeeklyReportEntryReferenceDto dto = new WeeklyReportEntryReferenceDto(entity.getUuid(), entity.toString());
		return dto;
	}
	
	public static WeeklyReportEntryDto toDto(WeeklyReportEntry source) {
		if (source == null) {
			return null;
		}
		WeeklyReportEntryDto target = new WeeklyReportEntryDto();
		DtoHelper.fillDto(target, source);
		
		target.setWeeklyReport(WeeklyReportFacadeEjb.toReferenceDto(source.getWeeklyReport()));
		target.setDisease(source.getDisease());
		target.setNumberOfCases(source.getNumberOfCases());
		
		return target;
	}
	

	@LocalBean
	@Stateless
	public static class WeeklyReportEntryFacadeEjbLocal extends WeeklyReportEntryFacadeEjb {
	}
}
