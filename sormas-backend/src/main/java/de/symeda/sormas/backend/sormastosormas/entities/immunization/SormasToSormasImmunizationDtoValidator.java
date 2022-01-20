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
import de.symeda.sormas.backend.sormastosormas.data.validation.ValidationDirection;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildImmunizationValidationGroupName;

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
	public ValidationErrors validate(SormasToSormasImmunizationDto sharedData, ValidationDirection direction) {
		final ImmunizationDto im = sharedData.getEntity();
		ValidationErrors validationErrors = new ValidationErrors(buildImmunizationValidationGroupName(im));

		final String groupNameTag = Captions.Immunization;
		infraValidator.validateCountry(im.getCountry(), groupNameTag, validationErrors, im::setCountry, direction);
		infraValidator.validateResponsibleRegion(im.getResponsibleRegion(), groupNameTag, validationErrors, im::setResponsibleRegion, direction);
		infraValidator
			.validateResponsibleDistrict(im.getResponsibleDistrict(), groupNameTag, validationErrors, im::setResponsibleDistrict, direction);
		infraValidator
			.validateResponsibleCommunity(im.getResponsibleCommunity(), groupNameTag, validationErrors, im::setResponsibleCommunity, direction);

		infraValidator
			.validateFacility(im.getHealthFacility(), im.getFacilityType(), im.getHealthFacilityDetails(), groupNameTag, validationErrors, f -> {
				im.setHealthFacility(f.getEntity());
				im.setHealthFacilityDetails(f.getDetails());
			});

		return validationErrors;
	}

	@Override
	public ValidationErrors validatePreview(PreviewNotImplementedDto previewNotImplementedDto, ValidationDirection direction) {
		// todo adjust test in InfraValidationSoundnessTest once preview is available for this entity
		throw new RuntimeException("Immunizations preview not yet implemented");
	}
}
