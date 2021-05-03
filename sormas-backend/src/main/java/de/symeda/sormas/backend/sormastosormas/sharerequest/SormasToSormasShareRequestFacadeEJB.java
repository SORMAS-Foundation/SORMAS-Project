/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.sormastosormas.sharerequest;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasShareRequestDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasShareRequestFacade;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasOriginInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasOriginInfoFacadeEjb.SormasToSormasOriginInfoFacadeEjbLocal;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "SormasToSormasShareRequestFacade")
public class SormasToSormasShareRequestFacadeEJB implements SormasToSormasShareRequestFacade {

	@EJB
	private SormasToSormasShareRequestService shareRequestService;

	@EJB
	private SormasToSormasOriginInfoFacadeEjbLocal originInfoFacade;

	@Override
	public SormasToSormasShareRequestDto saveShareRequest(@Valid SormasToSormasShareRequestDto dto) {
		SormasToSormasShareRequest request = fromDto(dto, true);

		shareRequestService.ensurePersisted(request);

		return toDto(request);
	}

	public SormasToSormasShareRequest fromDto(@NotNull SormasToSormasShareRequestDto source, boolean checkChangeDate) {

		SormasToSormasShareRequest target =
			DtoHelper.fillOrBuildEntity(source, shareRequestService.getByUuid(source.getUuid()), SormasToSormasShareRequest::new, checkChangeDate);

		target.setDataType(source.getDataType());
		target.setStatus(source.getStatus());
		target.setOriginInfo(originInfoFacade.fromDto(source.getOriginInfo(), checkChangeDate));
		target.setCases(source.getCases());
		target.setContacts(source.getContacts());
		target.setEvents(source.getEvents());

		return target;
	}

	private SormasToSormasShareRequestDto toDto(SormasToSormasShareRequest source) {
		if (source == null) {
			return null;
		}
		SormasToSormasShareRequestDto target = new SormasToSormasShareRequestDto();
		DtoHelper.fillDto(target, source);

		target.setDataType(source.getDataType());
		target.setStatus(source.getStatus());
		target.setOriginInfo(SormasToSormasOriginInfoFacadeEjb.toDto(source.getOriginInfo()));
		target.setCases(source.getCases());
		target.setContacts(source.getContacts());
		target.setEvents(source.getEvents());

		return target;
	}

	@LocalBean
	@Stateless
	public static class SormasToSormasShareRequestFacadeEJBLocal extends SormasToSormasShareRequestFacadeEJB {

	}
}
