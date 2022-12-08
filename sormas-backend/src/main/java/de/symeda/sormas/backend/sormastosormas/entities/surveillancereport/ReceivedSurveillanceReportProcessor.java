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

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.entities.surveillancereport.SormasToSormasSurveillanceReportDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.PreviewNotImplementedDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReport;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReportFacadeEjb.SurveillanceReportFacadeEjbLocal;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReportService;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessor;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class ReceivedSurveillanceReportProcessor
	extends
	ReceivedDataProcessor<SurveillanceReport, SurveillanceReportDto, SormasToSormasSurveillanceReportDto, PreviewNotImplementedDto, SurveillanceReport, SurveillanceReportService, SormasSormasToSurveillanceReportDtoValidator> {

	@EJB
	private SurveillanceReportFacadeEjbLocal surveillanceReportFacade;

	public ReceivedSurveillanceReportProcessor() {
	}

	@Inject
	protected ReceivedSurveillanceReportProcessor(
		SurveillanceReportService service,
		UserService userService,
		ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade,
		SormasSormasToSurveillanceReportDtoValidator validator) {
		super(service, userService, configFacade, validator);
	}

	@Override
	public void handleReceivedData(
		SormasToSormasSurveillanceReportDto sharedData,
		SurveillanceReport existingData,
		SormasToSormasOriginInfoDto originInfo) {
		updateReportingUser(sharedData.getEntity(), existingData);
		handleIgnoredProperties(sharedData.getEntity(), surveillanceReportFacade.toDto(existingData));
	}

	@Override
	public ValidationErrors processReceivedPreview(PreviewNotImplementedDto sharedPreview) {
		throw new RuntimeException("SurveillanceReport preview not yet implemented");
	}

	@Override
	public ValidationErrors existsNotShared(String uuid) {
		return existsNotShared(
			uuid,
			SurveillanceReport.SORMAS_TO_SORMAS_ORIGIN_INFO,
			SurveillanceReport.SORMAS_TO_SORMAS_SHARES,
			Captions.SurveillanceReport,
			Validations.sormasToSormasSurveillanceReportExists);
	}
}
