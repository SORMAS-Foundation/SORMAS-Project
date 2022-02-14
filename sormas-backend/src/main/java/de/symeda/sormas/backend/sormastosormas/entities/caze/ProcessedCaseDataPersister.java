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

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildCaseValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.handleValidationError;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.data.processed.ProcessedDataPersister;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfoService;

@Stateless
@LocalBean
public class ProcessedCaseDataPersister extends ProcessedDataPersister<CaseDataDto, SormasToSormasCaseDto, Case> {

	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;
	@EJB
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacade;
	@EJB
	private SormasToSormasShareInfoService shareInfoService;

	@Override
	protected SormasToSormasShareInfoService getShareInfoService() {
		return shareInfoService;
	}

	@Override
	protected void persistSharedData(SormasToSormasCaseDto processedData, Case existingEntity) throws SormasToSormasValidationException {
		persistProcessedData(processedData, existingEntity == null);
	}

	@Override
	protected SormasToSormasShareInfo getShareInfoByEntityAndOrganization(CaseDataDto entity, String organizationId) {
		return shareInfoService.getByCaseAndOrganization(entity.getUuid(), organizationId);
	}

	private void persistProcessedData(SormasToSormasCaseDto caseData, boolean isCreate) throws SormasToSormasValidationException {
		CaseDataDto caze = caseData.getEntity();

		if (isCreate) {
			// save person first during creation
			handleValidationError(
				() -> personFacade.savePerson(caseData.getPerson(), false, false, false),
				Captions.Person,
				buildCaseValidationGroupName(caze));

			handleValidationError(() -> caseFacade.saveCase(caze, true, false, false, false), Captions.CaseData, buildCaseValidationGroupName(caze));
		} else {
			//save case first during update

			handleValidationError(() -> caseFacade.saveCase(caze, true, false, false, false), Captions.CaseData, buildCaseValidationGroupName(caze));
			handleValidationError(
				() -> personFacade.savePerson(caseData.getPerson(), false, false, false),
				Captions.Person,
				buildCaseValidationGroupName(caze));
		}
	}
}
