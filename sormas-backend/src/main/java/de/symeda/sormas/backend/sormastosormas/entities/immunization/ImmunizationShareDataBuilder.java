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
import de.symeda.sormas.api.sormastosormas.entities.immunization.SormasToSormasImmunizationDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.PreviewNotImplementedDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb.ImmunizationFacadeEjbLocal;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilderHelper;
import de.symeda.sormas.backend.sormastosormas.share.SormasToSormasPseudonymizer;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfo;

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
	protected SormasToSormasImmunizationDto doBuildShareData(Immunization immunization, ShareRequestInfo requestInfo, boolean ownerShipHandedOver) {
		SormasToSormasPseudonymizer pseudonymizer = dataBuilderHelper.createPseudonymizer(requestInfo);
		ImmunizationDto immunizationDto = getDto(immunization, pseudonymizer);
		return new SormasToSormasImmunizationDto(immunizationDto);
	}

	@Override
	protected ImmunizationDto getDto(Immunization immunization, SormasToSormasPseudonymizer pseudonymizer) {

		ImmunizationDto immunizationDto = immunizationFacade.toPseudonymizedDto(immunization, pseudonymizer.getPseudonymizer());
		// reporting user is not set to null here as it would not pass the validation
		// the receiver appears to set it to SORMAS2SORMAS Client anyway
		immunizationDto.setSormasToSormasOriginInfo(null);
		dataBuilderHelper.clearIgnoredProperties(immunizationDto);

		return immunizationDto;
	}

	@Override
	public void doBusinessValidation(SormasToSormasImmunizationDto sormasToSormasImmunizationDto) throws ValidationRuntimeException {
		immunizationFacade.validate(sormasToSormasImmunizationDto.getEntity());
	}

	@Override
	public PreviewNotImplementedDto doBuildShareDataPreview(Immunization data, ShareRequestInfo requestInfo) {
		throw new RuntimeException("Immunizations preview not yet implemented");
	}
}
