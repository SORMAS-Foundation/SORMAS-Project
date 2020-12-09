package de.symeda.sormas.api.labmessage;

import java.io.Serializable;

import de.symeda.sormas.api.BaseCriteria;

public class LabMessageCriteria extends BaseCriteria implements Serializable {

	private Boolean processed;

	public Boolean getProcessed() {
		return processed;
	}

	public void setProcessed(Boolean processed) {
		this.processed = processed;
	}

	public LabMessageCriteria processed(Boolean processed) {
		setProcessed(processed);
		return this;
	}
}
