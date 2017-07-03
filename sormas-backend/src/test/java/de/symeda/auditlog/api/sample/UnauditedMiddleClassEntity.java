package de.symeda.auditlog.api.sample;

import java.math.BigDecimal;

public class UnauditedMiddleClassEntity extends SuperClassEntity {

	public static final String WEIGHT = "weight";

	private BigDecimal weight;

	public UnauditedMiddleClassEntity(String uuid, String name) {
		super(uuid, name);
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}
}
