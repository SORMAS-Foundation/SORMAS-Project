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

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEntityDto;
import de.symeda.sormas.api.sormastosormas.immunization.SormasToSormasImmunizationDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.PreviewNotImplementedDto;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb.ImmunizationFacadeEjbLocal;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilderHelper;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.ShareRequestInfo;
import de.symeda.sormas.backend.util.Pseudonymizer;

@Stateless
@LocalBean
public class ImmunizationShareDataBuilder
	extends
	ShareDataBuilder<ImmunizationDto, Immunization, SormasToSormasImmunizationDto, PreviewNotImplementedDto, SormasToSormasImmunizationDtoValidator> {

	@EJB
	private ImmunizationFacadeEjbLocal immunizationFacade;
	@EJB
	private ShareDataBuilderHelper dataBuilderHelper;

	@Inject
	public ImmunizationShareDataBuilder(SormasToSormasImmunizationDtoValidator validator) {
		super(validator);
	}

	public ImmunizationShareDataBuilder() {
	}

	@Override
	protected SormasToSormasImmunizationDto doBuildShareData(Immunization immunization, ShareRequestInfo requestInfo) {
		Pseudonymizer pseudonymizer =
			dataBuilderHelper.createPseudonymizer(requestInfo.isPseudonymizedPersonalData(), requestInfo.isPseudonymizedSensitiveData());

		ImmunizationDto immunizationDto = immunizationFacade.convertToDto(immunization, pseudonymizer);
		immunizationDto.setReportingUser(null);
		immunizationDto.setSormasToSormasOriginInfo(null);
		dataBuilderHelper.clearIgnoredProperties(immunizationDto);

		return new SormasToSormasImmunizationDto(immunizationDto);
	}

	@Override
	public PreviewNotImplementedDto doBuildShareDataPreview(Immunization data, ShareRequestInfo requestInfo) {
		throw new RuntimeException("Immunizations preview not yet implemented");
	}
}
