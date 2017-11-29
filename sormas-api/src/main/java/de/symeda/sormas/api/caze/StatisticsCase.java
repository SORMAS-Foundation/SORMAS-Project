package de.symeda.sormas.api.caze;

import java.io.Serializable;

import de.symeda.sormas.api.person.Sex;

public class StatisticsCase implements Serializable {

	private static final long serialVersionUID = -6569825315668163543L;

	private Integer approximateAge;
	private Sex sex;
	
	public StatisticsCase(Integer approximateAge, Sex sex) {
		this.approximateAge = approximateAge;
		this.sex = sex;
	}

	public Integer getApproximateAge() {
		return approximateAge;
	}

	public void setApproximateAge(Integer approximateAge) {
		this.approximateAge = approximateAge;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}
	
}
