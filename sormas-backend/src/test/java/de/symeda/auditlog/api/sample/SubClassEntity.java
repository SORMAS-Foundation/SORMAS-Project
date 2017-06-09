package de.symeda.auditlog.api.sample;

import de.symeda.auditlog.api.Audited;

@Audited
public class SubClassEntity extends UnauditedMiddleClassEntity {

	public static final String AGE = "age";

	private Integer age;

	public SubClassEntity(String uuid, String name, Integer age) {
		super(uuid, name);
		this.age = age;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
}
