package de.symeda.sormas.backend.sormastosormas.validation;

import de.symeda.sormas.api.sormastosormas.SormasToSormasEntityDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;
import de.symeda.sormas.backend.sormastosormas.data.validation.SormasToSormasDtoValidator;

public class InfraValidationSoundnessOutgoingTest extends InfraValidationSoundnessTest {

	@Override
	protected <SHARED extends SormasToSormasEntityDto<DTO>, DTO extends SormasToSormasShareableDto, PREVIEW extends PseudonymizableDto> ValidationErrors getDtoValidationErrors(
		SHARED entity,
		SormasToSormasDtoValidator<DTO, SHARED, PREVIEW> validator) {
		return validator.validateOutgoing(entity);
	}

	@Override
	protected <SHARED extends SormasToSormasEntityDto<DTO>, DTO extends SormasToSormasShareableDto, PREVIEW extends PseudonymizableDto> ValidationErrors getPreviewValidationErrors(
		PREVIEW preview,
		SormasToSormasDtoValidator<DTO, SHARED, PREVIEW> validator) {
		return validator.validateOutgoingPreview(preview);
	}

	@Override
	protected void before() {
		// with this the uuid of all local infrastructure will be constant.
		// 1. the wrong infrastructure we inject into the dtoUnderTest is also constant
		// 2. when we do the lookup in InfrastructureValidator::validateInfra(), we will find infrastructure
		// 3. however, it will not be marked as centrally managed
		// This simulates the case when a user has infrastructure locally, which is not marked as centrally managed.
		// S2S validation must fail in this case.
		setUpInfra(false);
	}
}
