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
import de.symeda.sormas.api.sormastosormas.entities.externalmessage.SormasToSormasExternalMessageDto;
import de.symeda.sormas.api.sormastosormas.entities.surveillancereport.SormasToSormasSurveillanceReportDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.PreviewNotImplementedDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReport;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReportFacadeEjb.SurveillanceReportFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilderHelper;
import de.symeda.sormas.backend.sormastosormas.share.SormasToSormasPseudonymizer;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfo;

@Stateless
@LocalBean
public class SurveillanceReportShareDataBuilder
	extends
	ShareDataBuilder<SurveillanceReportDto, SurveillanceReport, SormasToSormasSurveillanceReportDto, PreviewNotImplementedDto, SormasToSormasSurveillanceReportDtoValidator> {

	@EJB
	private SurveillanceReportFacadeEjbLocal surveillanceReportFacade;
	@EJB
	private ShareDataBuilderHelper dataBuilderHelper;

	public SurveillanceReportShareDataBuilder() {
	}

	@Inject
	public SurveillanceReportShareDataBuilder(SormasToSormasSurveillanceReportDtoValidator validator) {
		super(validator);
	}

	@Override
	protected SormasToSormasSurveillanceReportDto doBuildShareData(
		SurveillanceReport report,
		ShareRequestInfo requestInfo,
		boolean ownerShipHandedOver) {
		SormasToSormasPseudonymizer pseudonymizer = dataBuilderHelper.createPseudonymizer(requestInfo);
		SurveillanceReportDto reportDto = getDto(report, pseudonymizer);

		SormasToSormasExternalMessageDto externalMessage = null;
		if (ownerShipHandedOver && report.getExternalMessage() != null) {
			externalMessage = dataBuilderHelper.getExternalMessageDto(report.getExternalMessage(), requestInfo);
		}

		return new SormasToSormasSurveillanceReportDto(reportDto, externalMessage);
	}

	@Override
	protected SurveillanceReportDto getDto(SurveillanceReport surveillanceReport, SormasToSormasPseudonymizer pseudonymizer) {
		SurveillanceReportDto report = surveillanceReportFacade.toPseudonymizedDto(surveillanceReport, pseudonymizer.getPseudonymizer());
		// reporting user is not set to null here as it would not pass the validation
		// the receiver appears to set it to SORMAS2SORMAS Client anyway
		report.setSormasToSormasOriginInfo(null);
		dataBuilderHelper.clearIgnoredProperties(report);

		return report;
	}

	@Override
	protected void doBusinessValidation(SormasToSormasSurveillanceReportDto sormasToSormasSurveillanceReportDto) throws ValidationRuntimeException {
		surveillanceReportFacade.validate(sormasToSormasSurveillanceReportDto.getEntity());
	}

	@Override
	protected PreviewNotImplementedDto doBuildShareDataPreview(SurveillanceReport data, ShareRequestInfo requestInfo) {
		throw new RuntimeException("SurveillanceReport preview not yet implemented");
	}
}
