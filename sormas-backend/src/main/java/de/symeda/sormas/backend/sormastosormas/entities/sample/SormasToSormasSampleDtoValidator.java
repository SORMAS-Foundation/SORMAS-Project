package de.symeda.sormas.backend.sormastosormas.entities.sample;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.sample.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.PreviewNotImplementedDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;

import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.SormasToSormasDtoValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.ValidationDirection;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildSampleValidationGroupName;

@Stateless
@LocalBean
public class SormasToSormasSampleDtoValidator extends SormasToSormasDtoValidator<SampleDto, SormasToSormasSampleDto, PreviewNotImplementedDto> {

	public SormasToSormasSampleDtoValidator() {
	}

	@Inject
	public SormasToSormasSampleDtoValidator(InfrastructureValidator infraValidator) {
		super(infraValidator);
	}

	@Override
	public ValidationErrors validate(SormasToSormasSampleDto sharedData, ValidationDirection direction) {
		SampleDto sample = sharedData.getEntity();
		ValidationErrors validationErrors = new ValidationErrors(buildSampleValidationGroupName(sample));

		// todo shouldn't this be FacilityType.LABORATORY?
		infraValidator.validateFacility(sample.getLab(), null, sample.getLabDetails(), Captions.Sample_lab, validationErrors, f -> {
			sample.setLab(f.getEntity());
			sample.setLabDetails(f.getDetails());
		});

		sharedData.getPathogenTests().forEach(pathogenTest -> validatePathogenTest(validationErrors, pathogenTest));
		return validationErrors;
	}

	@Override
	public ValidationErrors validatePreview(PreviewNotImplementedDto previewNotImplementedDto, ValidationDirection direction) {
		// todo adjust test in InfraValidationSoundnessTest once preview is available for this entity
		throw new RuntimeException("Samples preview not yet implemented");
	}
}
