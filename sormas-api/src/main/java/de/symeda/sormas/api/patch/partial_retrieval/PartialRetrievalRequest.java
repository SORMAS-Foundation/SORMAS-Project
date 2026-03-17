package de.symeda.sormas.api.patch.partial_retrieval;

import java.util.Objects;
import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Allows to retrieve field values and their respective field names.
 */
public class PartialRetrievalRequest {

	/**
	 * Root object for retrieval is the case.
	 */
	@NotNull
	private String caseUuid;

	/**
	 * Example: CaseData.hospitalization. Must be the physical path of the DTO.
	 */
	@NotNull
	@NotEmpty
	private Set<String> fieldsToRetrieve;

	public String getCaseUuid() {
		return caseUuid;
	}

	public PartialRetrievalRequest setCaseUuid(String caseUuid) {
		this.caseUuid = caseUuid;
		return this;
	}

	public Set<String> getFieldsToRetrieve() {
		return fieldsToRetrieve;
	}

	public PartialRetrievalRequest setFieldsToRetrieve(Set<String> fieldsToRetrieve) {
		this.fieldsToRetrieve = fieldsToRetrieve;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		PartialRetrievalRequest that = (PartialRetrievalRequest) o;
		return Objects.equals(caseUuid, that.caseUuid) && Objects.equals(fieldsToRetrieve, that.fieldsToRetrieve);
	}

	@Override
	public int hashCode() {
		return Objects.hash(caseUuid, fieldsToRetrieve);
	}

	@Override
	public String toString() {
		return "PartialRetrievalRequest{" + "caseUuid='" + caseUuid + '\'' + ", fieldsToRetrieve=" + fieldsToRetrieve + '}';
	}
}
