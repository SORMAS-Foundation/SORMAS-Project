package de.symeda.sormas.backend.sample;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.util.AbstractDomainObjectJoins;

public class AdditionalTestJoins<T> extends AbstractDomainObjectJoins<T, AdditionalTest> {

	private Join<AdditionalTest, Sample> sample;

	public AdditionalTestJoins(From<T, AdditionalTest> root) {
		super(root);
	}

	public Join<AdditionalTest, Sample> getSample() {
		return getOrCreate(sample, AdditionalTest.SAMPLE, JoinType.LEFT, this::setSample);
	}

	public void setSample(Join<AdditionalTest, Sample> sample) {
		this.sample = sample;
	}
}
