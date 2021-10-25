package de.symeda.sormas.backend.sormastosormas.entities.immunization;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasImmunizationPreview;
import de.symeda.sormas.api.sormastosormas.validation.Sormas2SormasEntityValidator;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.sormastosormas.data.Sormas2SormasCommonDataValidator;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;

import javax.ejb.EJB;

public class ImmunizationValidatorS2S implements Sormas2SormasEntityValidator<ImmunizationDto, SormasToSormasImmunizationPreview> {

	@EJB
	private InfrastructureValidator infraValidator;
	@EJB
	private Sormas2SormasCommonDataValidator commonDataValidator;

	@Override
	public ValidationErrors validateInboundEntity(ImmunizationDto im) {
		ValidationErrors validationErrors = new ValidationErrors();

		final String groupNameTag = Captions.Sample_lab;
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
	public ValidationErrors validateInboundPreviewEntity(SormasToSormasImmunizationPreview unused) {
		throw new RuntimeException("Immunizations preview not yet implemented");
	}
}
