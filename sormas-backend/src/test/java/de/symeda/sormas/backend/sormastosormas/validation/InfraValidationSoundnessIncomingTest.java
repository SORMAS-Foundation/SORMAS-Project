package de.symeda.sormas.backend.sormastosormas.validation;

import de.symeda.sormas.api.sormastosormas.SormasToSormasEntityDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;
import de.symeda.sormas.backend.sormastosormas.data.validation.SormasToSormasDtoValidator;

public class InfraValidationSoundnessIncomingTest extends InfraValidationSoundnessTest {

	@Override
	protected <SHARED extends SormasToSormasEntityDto<DTO>, DTO extends SormasToSormasShareableDto, PREVIEW extends PseudonymizableDto> ValidationErrors getDtoValidationErrors(
		SHARED entity,
		SormasToSormasDtoValidator<DTO, SHARED, PREVIEW> validator) {
		return validator.validateIncoming(entity);
	}

	@Override
	protected <SHARED extends SormasToSormasEntityDto<DTO>, DTO extends SormasToSormasShareableDto, PREVIEW extends PseudonymizableDto> ValidationErrors getPreviewValidationErrors(
		PREVIEW preview,
		SormasToSormasDtoValidator<DTO, SHARED, PREVIEW> validator) {
		return validator.validateIncomingPreview(preview);
	}

	@Override
	protected void before() {
		// with this the uuid of all local infrastructure will be random.
		// 1. the wrong infrastructure we inject into the dtoUnderTest is constant
		// 2. when we do the lookup in InfrastructureValidator::validateInfra(), we will not find infrastructure
		// This simulates the case when we receive infra uuids which are present on the sender side, but not available
		// locally
		setUpInfra(true);
	}

}
