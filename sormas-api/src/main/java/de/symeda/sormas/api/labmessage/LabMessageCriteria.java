package de.symeda.sormas.api.labmessage;

import java.io.Serializable;

import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class LabMessageCriteria extends BaseCriteria implements Serializable {

	private LabMessageStatus labMessageStatus;

	public LabMessageStatus getLabMessageStatus() {
		return labMessageStatus;
	}

	public LabMessageCriteria labMessageStatus(LabMessageStatus labMessageStatus) {
		this.labMessageStatus = labMessageStatus;

		return this;
	}
}
