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

package de.symeda.sormas.backend.sormastosormas.entities.caze;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasCasePreview;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilderHelper;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfo;
import de.symeda.sormas.backend.util.Pseudonymizer;

@Stateless
@LocalBean
public class CaseShareDataBuilder
	extends ShareDataBuilder<CaseDataDto, Case, SormasToSormasCaseDto, SormasToSormasCasePreview, SormasToSormasCaseDtoValidator> {

	@Inject
	public CaseShareDataBuilder(SormasToSormasCaseDtoValidator validator) {
		super(validator);
	}

	public CaseShareDataBuilder() {
	}

	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;

	@EJB
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacade;
	@EJB
	private ShareDataBuilderHelper dataBuilderHelper;

	@Override
	protected SormasToSormasCaseDto doBuildShareData(Case caze, ShareRequestInfo requestInfo, boolean ownerShipHandedOver) {
		Pseudonymizer pseudonymizer = dataBuilderHelper.createPseudonymizer(requestInfo);

		PersonDto personDto = dataBuilderHelper.getPersonDto(caze.getPerson(), pseudonymizer, requestInfo);
		CaseDataDto cazeDto = getCazeDto(caze, pseudonymizer);

		dataBuilderHelper.clearIgnoredProperties(cazeDto);

		return new SormasToSormasCaseDto(personDto, cazeDto);
	}

	@Override
	public void doBusinessValidation(SormasToSormasCaseDto dto) {
		personFacade.validate(dto.getPerson());
		caseFacade.validate(dto.getEntity());
	}

	@Override
	protected SormasToSormasCasePreview doBuildShareDataPreview(Case caze, ShareRequestInfo requestInfo) {
		Pseudonymizer pseudonymizer = dataBuilderHelper.createPseudonymizer(requestInfo);

		return dataBuilderHelper.getCasePreview(caze, pseudonymizer);
	}

	private CaseDataDto getCazeDto(Case caze, Pseudonymizer pseudonymizer) {
		CaseDataDto cazeDto = caseFacade.convertToDto(caze, pseudonymizer);

		cazeDto.setReportingUser(null);
		cazeDto.setClassificationUser(null);
		cazeDto.setSurveillanceOfficer(null);
		cazeDto.setCaseOfficer(null);
		cazeDto.setSormasToSormasOriginInfo(null);
		cazeDto.setDontShareWithReportingTool(false);

		return cazeDto;
	}
}
