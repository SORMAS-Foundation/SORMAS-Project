package de.symeda.sormas.api.patch.partial_retrieval;

import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class PartialRetrievalRequest {

	@NotNull
	private String caseUuid;

	@NotNull
	@NotEmpty
	private Set<String> fieldsToRetrieve;
}
