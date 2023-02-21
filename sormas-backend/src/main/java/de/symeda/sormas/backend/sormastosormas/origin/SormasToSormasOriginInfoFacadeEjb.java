/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.sormastosormas.origin;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.sormastosormas.share.incoming.SormasToSormasShareRequestService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "SormasToSormasOriginInfoFacade")
public class SormasToSormasOriginInfoFacadeEjb implements SormasToSormasOriginInfoFacade {

	@EJB
	private SormasToSormasOriginInfoService originInfoService;

	@EJB
	private SormasToSormasOriginInfoService sormasToSormasOriginInfoService;

	@EJB
	private SormasToSormasShareRequestService sormasToSormasShareRequestService;

	@PermitAll
	public static SormasToSormasOriginInfoDto toDto(SormasToSormasOriginInfo source) {
		if (source == null) {
			return null;
		}

		SormasToSormasOriginInfoDto target = new SormasToSormasOriginInfoDto();

		DtoHelper.fillDto(target, source);

		target.setOrganizationId(source.getOrganizationId());
		target.setSenderName(source.getSenderName());
		target.setSenderEmail(source.getSenderEmail());
		target.setSenderPhoneNumber(source.getSenderPhoneNumber());
		target.setOwnershipHandedOver(source.isOwnershipHandedOver());
		target.setWithAssociatedContacts(source.isWithAssociatedContacts());
		target.setWithSamples(source.isWithSamples());
		target.setWithEventParticipants(source.isWithEventParticipants());
		target.setWithImmunizations(source.isWithImmunizations());
		target.setWithSurveillanceReports(source.isWithSurveillanceReports());
		target.setComment(source.getComment());
		target.setPseudonymizedData(source.isPseudonymizedData());

		return target;
	}

	@RightsAllowed({
		UserRight._SORMAS_TO_SORMAS_PROCESS,
		UserRight._SORMAS_TO_SORMAS_CLIENT })
	public SormasToSormasOriginInfoDto saveOriginInfo(SormasToSormasOriginInfoDto originInfoDto) {
		SormasToSormasOriginInfo existingSormasToSormasOriginInfo = sormasToSormasOriginInfoService.getByUuid(originInfoDto.getUuid());
		SormasToSormasOriginInfo originInfo = fillOrBuildEntity(originInfoDto, existingSormasToSormasOriginInfo, true);

		originInfoService.ensurePersisted(originInfo);

		return toDto(originInfo);
	}

	@RightsAllowed({UserRight._SORMAS_TO_SORMAS_PROCESS, UserRight._SORMAS_TO_SORMAS_CLIENT})
	public SormasToSormasOriginInfo fillOrBuildEntity(SormasToSormasOriginInfoDto source, SormasToSormasOriginInfo target, boolean checkChangeDate) {
		if (source == null) {
			return null;
		}

		target = DtoHelper.fillOrBuildEntity(source, target, SormasToSormasOriginInfo::new, checkChangeDate);

		target.setOrganizationId(source.getOrganizationId());
		target.setSenderName(source.getSenderName());
		target.setSenderEmail(source.getSenderEmail());
		target.setSenderPhoneNumber(source.getSenderPhoneNumber());
		target.setOwnershipHandedOver(source.isOwnershipHandedOver());
		target.setWithAssociatedContacts(source.isWithAssociatedContacts());
		target.setWithSamples(source.isWithSamples());
		target.setWithEventParticipants(source.isWithEventParticipants());
		target.setWithImmunizations(source.isWithImmunizations());
		target.setWithSurveillanceReports(source.isWithSurveillanceReports());
		target.setComment(source.getComment());
		target.setPseudonymizedData(source.isPseudonymizedData());

		return target;
	}

	@RightsAllowed({
		UserRight._SORMAS_TO_SORMAS_PROCESS,
		UserRight._SORMAS_TO_SORMAS_CLIENT })
	public boolean exists(String uuid) {
		return originInfoService.exists(uuid);
	}

	@LocalBean
	@Stateless
	public static class SormasToSormasOriginInfoFacadeEjbLocal extends SormasToSormasOriginInfoFacadeEjb {

	}
}
