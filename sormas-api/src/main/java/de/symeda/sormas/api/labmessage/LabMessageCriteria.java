package de.symeda.sormas.api.labmessage;

import java.io.Serializable;

import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class LabMessageCriteria extends BaseCriteria implements Serializable {

	private String uuid;
	private LabMessageStatus labMessageStatus;
	private SampleReferenceDto sample;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public LabMessageStatus getLabMessageStatus() {
		return labMessageStatus;
	}

	public LabMessageCriteria labMessageStatus(LabMessageStatus labMessageStatus) {
		this.labMessageStatus = labMessageStatus;
		return this;
	}

	public SampleReferenceDto getSample() {
		return sample;
	}

	public void setSample(SampleReferenceDto sample) {
		this.sample = sample;
	}
}
