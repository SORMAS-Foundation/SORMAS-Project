package de.symeda.sormas.api.patch;

import java.util.Map;

import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.info.InfoFacade;

// TODO: must following be supported: MULTIPLE - FIELD patching ? HOW to behave for that
public class CaseDataPatchRequest {

	@NotNull
	private String caseUuid;

	@NotNull
	private DataReplacementType replacementType = DataReplacementType.IF_NOT_ALREADY_PRESENT;

	private EmptyValueBehavior emptyValueBehavior = EmptyValueBehavior.IGNORE;

	/**
	 * Key are those from with root being the {@link de.symeda.sormas.api.caze.CaseDataDto}.
	 * The accepted fields are those from {@link InfoFacade#generateDataDictionary()}.
	 */
	@NotNull
	private Map<String, Object> patchDictionary;

	public String getCaseUuid() {
		return caseUuid;
	}

	public CaseDataPatchRequest setCaseUuid(String caseUuid) {
		this.caseUuid = caseUuid;
		return this;
	}

	public DataReplacementType getReplacementType() {
		return replacementType;
	}

	public CaseDataPatchRequest setReplacementType(DataReplacementType replacementType) {
		this.replacementType = replacementType;
		return this;
	}

	public Map<String, Object> getPatchDictionary() {
		return patchDictionary;
	}

	public CaseDataPatchRequest setPatchDictionary(Map<String, Object> patchDictionary) {
		this.patchDictionary = patchDictionary;
		return this;
	}

	public EmptyValueBehavior getEmptyValueBehavior() {
		return emptyValueBehavior;
	}

	public CaseDataPatchRequest setEmptyValueBehavior(EmptyValueBehavior emptyValueBehavior) {
		this.emptyValueBehavior = emptyValueBehavior;
		return this;
	}
}
