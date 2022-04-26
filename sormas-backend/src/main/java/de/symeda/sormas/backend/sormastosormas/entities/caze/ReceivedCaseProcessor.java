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

import java.util.Optional;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasCasePreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessor;
import de.symeda.sormas.backend.sormastosormas.entities.SormasToSormasEntitiesHelper;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class ReceivedCaseProcessor
	extends
	ReceivedDataProcessor<Case, CaseDataDto, SormasToSormasCaseDto, SormasToSormasCasePreview, Case, CaseService, SormasToSormasCaseDtoValidator> {

	@EJB
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacade;
	@EJB
	private SormasToSormasEntitiesHelper sormasToSormasEntitiesHelper;

	public ReceivedCaseProcessor() {
	}

	@Inject
	protected ReceivedCaseProcessor(
		CaseService service,
		UserService userService,
		ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade,
		SormasToSormasCaseDtoValidator validator) {
		super(service, userService, configFacade, validator);
	}

	@Override
	public void handleReceivedData(SormasToSormasCaseDto sharedData, Case existingCase, SormasToSormasOriginInfoDto originInfo) {
		handleIgnoredProperties(sharedData.getEntity(), caseFacade.toDto(existingCase));

		handleIgnoredProperties(
			sharedData.getPerson(),
			Optional.ofNullable(existingCase).map(c -> PersonFacadeEjb.toDto(c.getPerson())).orElse(null));

		CaseDataDto caze = sharedData.getEntity();
		PersonDto person = sharedData.getPerson();
		caze.setPerson(person.toReference());
		updateReportingUser(caze, existingCase);

		if(originInfo.isOwnershipHandedOver()) {
			sormasToSormasEntitiesHelper.updateCaseResponsibleDistrict(caze, configFacade.getS2SConfig().getDistrictExternalId());
		}
	}

	@Override
	public ValidationErrors existsNotShared(String uuid) {
		return existsNotShared(
			uuid,
			Case.SORMAS_TO_SORMAS_ORIGIN_INFO,
			Case.SORMAS_TO_SORMAS_SHARES,
			Captions.CaseData,
			Validations.sormasToSormasCaseExists);
	}
}
