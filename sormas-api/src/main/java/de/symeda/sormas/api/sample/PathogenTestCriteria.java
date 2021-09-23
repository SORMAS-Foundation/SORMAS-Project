package de.symeda.sormas.api.sample;

import java.io.Serializable;

import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class PathogenTestCriteria extends BaseCriteria implements Serializable {

	private static final long serialVersionUID = -4649293670201029462L;

	private SampleReferenceDto sample;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public SampleReferenceDto getSample() {
		return sample;
	}

	public void setSample(SampleReferenceDto sample) {
		this.sample = sample;
	}
}
