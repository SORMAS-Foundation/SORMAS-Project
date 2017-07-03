package de.symeda.auditlog.api.sample;

import de.symeda.auditlog.api.Audited;

@Audited
public class SuperClassEntity extends BaseEntity {

	public static final String NAME = "name";

	private String name;

	public SuperClassEntity(String uuid, String name) {
		super(uuid);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
