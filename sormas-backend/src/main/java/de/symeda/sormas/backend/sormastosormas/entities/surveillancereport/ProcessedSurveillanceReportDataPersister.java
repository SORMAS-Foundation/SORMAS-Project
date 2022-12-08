/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.sormastosormas.entities.surveillancereport;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildSurveillanceReportValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.handleValidationError;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.entities.surveillancereport.SormasToSormasSurveillanceReportDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReport;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReportFacadeEjb.SurveillanceReportFacadeEjbLocal;
import de.symeda.sormas.backend.externalmessage.ExternalMessageFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.data.processed.ProcessedDataPersister;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb.SormasToSormasOriginInfoFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfoService;

@Stateless
@LocalBean
public class ProcessedSurveillanceReportDataPersister
	extends ProcessedDataPersister<SurveillanceReportDto, SormasToSormasSurveillanceReportDto, SurveillanceReport> {

	@EJB
	private SormasToSormasShareInfoService shareInfoService;

	@EJB
	private SormasToSormasOriginInfoFacadeEjbLocal originInfoFacade;

	@EJB
	private SurveillanceReportFacadeEjbLocal surveillanceReportFacade;
	@EJB
	private ExternalMessageFacadeEjb.ExternalMessageFacadeEjbLocal externalMessageFacade;

	@Override
	protected SormasToSormasShareInfoService getShareInfoService() {
		return shareInfoService;
	}

	@Override
	protected SormasToSormasOriginInfoFacadeEjb getOriginInfoFacade() {
		return originInfoFacade;
	}

	@Override
	protected void persistSharedData(SormasToSormasSurveillanceReportDto processedData, SurveillanceReport existingEntity, boolean isSync)
		throws SormasToSormasValidationException {
		SurveillanceReportDto report = processedData.getEntity();

		handleValidationError(
			() -> surveillanceReportFacade.saveSurveillanceReport(report, false, false),
			Captions.Immunization,
			buildSurveillanceReportValidationGroupName(report),
			report);

		if (processedData.getExternalMessage() != null) {
			ExternalMessageDto externalMessage = processedData.getExternalMessage().getEntity();
			handleValidationError(
				() -> externalMessageFacade.save(externalMessage, false, false),
				Captions.ExternalMessage,
				buildValidationGroupName(Captions.ExternalMessage, externalMessage),
				externalMessage);
		}
	}

	@Override
	protected SormasToSormasShareInfo getShareInfoByEntityAndOrganization(SurveillanceReportDto entity, String organizationId) {
		return shareInfoService.getBySurveillanceReportAndOrganization(entity.getUuid(), organizationId);
	}
}
