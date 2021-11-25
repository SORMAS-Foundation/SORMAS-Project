package de.symeda.sormas.api.sormastosormas.sharerequest;

import de.symeda.sormas.api.HasUuid;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

public class PreviewNotImplementedDto extends PseudonymizableDto implements HasUuid {

	PreviewNotImplementedDto() {
		throw new RuntimeException("Not meant to be instantiated");
	}
}
