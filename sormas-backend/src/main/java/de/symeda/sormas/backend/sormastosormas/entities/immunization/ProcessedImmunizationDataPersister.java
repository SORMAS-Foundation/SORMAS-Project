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

package de.symeda.sormas.backend.sormastosormas.entities.immunization;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildImmunizationValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.handleValidationError;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEntityDto;
import de.symeda.sormas.api.sormastosormas.immunization.SormasToSormasImmunizationDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb.ImmunizationFacadeEjbLocal;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.sormastosormas.data.processed.ProcessedDataPersister;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfoService;

@Stateless
@LocalBean
public class ProcessedImmunizationDataPersister extends ProcessedDataPersister<ImmunizationDto, SormasToSormasImmunizationDto, Immunization> {

	@EJB
	private SormasToSormasShareInfoService shareInfoService;
	@EJB
	private ImmunizationFacadeEjbLocal immunizationFacade;

	@Override
	protected SormasToSormasShareInfoService getShareInfoService() {
		return shareInfoService;
	}

	@Override
	protected void persistSharedData(SormasToSormasImmunizationDto processedData, Immunization existingEntity)
		throws SormasToSormasValidationException {
		ImmunizationDto immunuzation = processedData.getEntity();

		handleValidationError(
			() -> immunizationFacade.save(immunuzation, false, false),
			Captions.Immunization,
			buildImmunizationValidationGroupName(immunuzation));
	}

	@Override
	protected SormasToSormasShareInfo getShareInfoByEntityAndOrganization(ImmunizationDto entity, String organizationId) {
		return shareInfoService.getByImmunizationAndOrganization(entity.getUuid(), organizationId);
	}
}
