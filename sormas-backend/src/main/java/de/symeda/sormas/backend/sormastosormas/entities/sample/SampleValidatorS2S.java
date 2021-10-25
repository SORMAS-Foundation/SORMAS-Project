package de.symeda.sormas.backend.sormastosormas.entities.sample;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasSamplePreview;
import de.symeda.sormas.api.sormastosormas.validation.Sormas2SormasEntityValidator;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.sormastosormas.data.Sormas2SormasCommonDataValidator;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;

import javax.ejb.EJB;

public class SampleValidatorS2S implements Sormas2SormasEntityValidator<SampleDto, SormasToSormasSamplePreview> {

	@EJB
	private InfrastructureValidator infraValidator;
	@EJB
	private Sormas2SormasCommonDataValidator commonDataValidator;

	@Override
	public ValidationErrors validateInboundEntity(SampleDto sample) {
		ValidationErrors validationErrors = new ValidationErrors();

		infraValidator.validateFacility(
			sample.getLab(),
			null, // todo shouldn't this be FacilityType.LABORATORY?
			sample.getLabDetails(),
			Captions.Sample_lab,
			validationErrors,
			f -> {
				sample.setLab(f.getEntity());
				sample.setLabDetails(f.getDetails());
			});

		return validationErrors;
	}

	@Override
	public ValidationErrors validateInboundPreviewEntity(SormasToSormasSamplePreview sormasToSormasSamplePreview) {
		throw new RuntimeException("Samples preview not yet implemented");
	}

}
