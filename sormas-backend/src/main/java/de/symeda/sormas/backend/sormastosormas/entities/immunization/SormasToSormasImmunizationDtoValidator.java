package de.symeda.sormas.backend.sormastosormas.entities.immunization;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.sormastosormas.immunization.SormasToSormasImmunizationDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.PreviewNotImplementedDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.SormasToSormasDtoValidator;

@Stateless
@LocalBean
public class SormasToSormasImmunizationDtoValidator
	extends SormasToSormasDtoValidator<ImmunizationDto, SormasToSormasImmunizationDto, PreviewNotImplementedDto> {

	public SormasToSormasImmunizationDtoValidator() {
	}

	@Inject
	public SormasToSormasImmunizationDtoValidator(InfrastructureValidator infraValidator) {
		super(infraValidator);
	}

	@Override
	public ValidationErrors validateIncoming(SormasToSormasImmunizationDto sharedData) {
		ValidationErrors validationErrors = new ValidationErrors();
		final ImmunizationDto im = sharedData.getEntity();

		final String groupNameTag = Captions.Immunization;
		infraValidator.validateCountry(im.getCountry(), groupNameTag, validationErrors, im::setCountry);
		infraValidator.validateResponsibleRegion(im.getResponsibleRegion(), groupNameTag, validationErrors, im::setResponsibleRegion);
		infraValidator.validateResponsibleDistrict(im.getResponsibleDistrict(), groupNameTag, validationErrors, im::setResponsibleDistrict);
		infraValidator.validateResponsibleCommunity(im.getResponsibleCommunity(), groupNameTag, validationErrors, im::setResponsibleCommunity);

		infraValidator
			.validateFacility(im.getHealthFacility(), im.getFacilityType(), im.getHealthFacilityDetails(), groupNameTag, validationErrors, f -> {
				im.setHealthFacility(f.getEntity());
				im.setHealthFacilityDetails(f.getDetails());
			});

		return validationErrors;
	}

	@Override
	public ValidationErrors validateIncomingPreview(PreviewNotImplementedDto previewNotImplementedDto) {
		throw new RuntimeException("Immunizations preview not yet implemented");
	}

	@Override
	public ValidationErrors validateOutgoing(SormasToSormasImmunizationDto sharedData) {
		return null;
	}

	@Override
	public ValidationErrors validateOutgoingPreview(PreviewNotImplementedDto previewNotImplementedDto) {
		return null;
	}
}
