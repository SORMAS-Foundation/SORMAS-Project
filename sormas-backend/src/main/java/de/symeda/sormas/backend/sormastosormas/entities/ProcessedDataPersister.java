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

package de.symeda.sormas.backend.sormastosormas.entities;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.sormastosormas.ShareTreeCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.backend.sormastosormas.entities.caze.ProcessedCaseDataPersister;

@Stateless
@LocalBean
public class ProcessedDataPersister {

	@EJB
	private ProcessedCaseDataPersister caseDataPersister;

	public void persistSharedData(SormasToSormasDto processedData) throws SormasToSormasValidationException {

		for (SormasToSormasCaseDto c : processedData.getCases()) {
			caseDataPersister.persistSharedData(c);
		}

	}

	public void persistReturnedData(SormasToSormasDto processedData, SormasToSormasOriginInfoDto originInfoDto)
		throws SormasToSormasValidationException {
		for (SormasToSormasCaseDto c : processedData.getCases()) {
			caseDataPersister.persistReturnedData(c, originInfoDto);
		}
	}

	public void persistSyncData(SormasToSormasDto processedData, SormasToSormasOriginInfoDto originInfoDto, ShareTreeCriteria shareTreeCriteria)
		throws SormasToSormasValidationException {
		for (SormasToSormasCaseDto c : processedData.getCases()) {
			caseDataPersister.persistSyncData(c, originInfoDto, shareTreeCriteria);
		}
	}
}
