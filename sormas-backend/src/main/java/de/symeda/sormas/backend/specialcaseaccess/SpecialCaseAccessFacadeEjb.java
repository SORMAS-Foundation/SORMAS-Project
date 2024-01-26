/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.specialcaseaccess;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.Valid;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.specialcaseaccess.SpecialCaseAccessDto;
import de.symeda.sormas.api.specialcaseaccess.SpecialCaseAccessFacade;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "SpecialCaseAccessFacade")
@RightsAllowed(UserRight._GRANT_SPECIAL_CASE_ACCESS)
public class SpecialCaseAccessFacadeEjb implements SpecialCaseAccessFacade {

	@EJB
	private SpecialCaseAccessService specialCaseAccessService;
	@EJB
	private CaseService caseService;
	@EJB
	private UserService userService;

	@Override
	public SpecialCaseAccessDto save(@Valid SpecialCaseAccessDto dto) {
		SpecialCaseAccess existingAdo = specialCaseAccessService.getByUuid(dto.getUuid());
		SpecialCaseAccess ado = fillOrBuildEntity(dto, existingAdo);

		specialCaseAccessService.ensurePersisted(ado);

		return toDto(ado);
	}

	@Override
	public List<SpecialCaseAccessDto> getAllActiveByCase(CaseReferenceDto caze) {
		return specialCaseAccessService.getAllActiveByCase(caze).stream().map(SpecialCaseAccessFacadeEjb::toDto).collect(Collectors.toList());
	}

	@Override
	public void delete(String uuid) {
		specialCaseAccessService.deletePermanent(specialCaseAccessService.getByUuid(uuid));
	}

	@Override
	public boolean isAnyAssignedToUser(List<CaseReferenceDto> cases, UserReferenceDto user) {
		return specialCaseAccessService.isAnyAssignedToUser(cases, user);
	}

	@Override
	public void saveAll(@Valid List<SpecialCaseAccessDto> specialAccesses) {
		specialAccesses.forEach(access -> {
			specialCaseAccessService.deleteByCaseAndAssignee(access.getCaze(), access.getAssignedTo());
			save(access);
		});
	}

	@RightsAllowed(UserRight._SYSTEM)
	public void deleteExpiredSpecialCaseAccesses() {
		specialCaseAccessService
				.getByPredicate((cb, from, cq) -> cb.lessThanOrEqualTo(from.get(SpecialCaseAccess.END_DATE_TIME), new java.util.Date()))
				.forEach(specialCaseAccessService::deletePermanent);
	}

	private SpecialCaseAccess fillOrBuildEntity(SpecialCaseAccessDto source, SpecialCaseAccess target) {
		if (source == null) {
			return null;
		}

		target = DtoHelper.fillOrBuildEntity(source, target, SpecialCaseAccess::new, true);

		target.setCaze(caseService.getByReferenceDto(source.getCaze()));
		target.setAssignedTo(userService.getByReferenceDto(source.getAssignedTo()));
		target.setAssignedBy(userService.getByReferenceDto(source.getAssignedBy()));
		target.setAssignmentDate(source.getAssignmentDate());
		target.setEndDateTime(source.getEndDateTime());

		return target;
	}

	private static SpecialCaseAccessDto toDto(SpecialCaseAccess source) {
		if (source == null) {
			return null;
		}

		SpecialCaseAccessDto target = new SpecialCaseAccessDto();
		DtoHelper.fillDto(target, source);

		target.setCaze(CaseFacadeEjb.toReferenceDto(source.getCaze()));
		target.setAssignedTo(UserFacadeEjb.toReferenceDto(source.getAssignedTo()));
		target.setAssignedBy(UserFacadeEjb.toReferenceDto(source.getAssignedBy()));
		target.setAssignmentDate(source.getAssignmentDate());
		target.setEndDateTime(source.getEndDateTime());

		return target;
	}

	@LocalBean
	@Stateless
	public static class SpecialCaseAccessFacadeEjbLocal extends SpecialCaseAccessFacadeEjb {

	}
}
