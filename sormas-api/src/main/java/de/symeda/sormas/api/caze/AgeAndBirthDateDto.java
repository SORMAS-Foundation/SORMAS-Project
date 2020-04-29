package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.person.ApproximateAgeType;

import java.io.Serializable;

public class AgeAndBirthDateDto extends BirthDateDto implements Serializable {
	private Integer age;
	private ApproximateAgeType ageType;

	public AgeAndBirthDateDto(Integer age, ApproximateAgeType ageType, Integer birthdateDD, Integer birthdateMM, Integer birthdateYYYY) {
		super(birthdateDD, birthdateMM, birthdateYYYY);

		this.age = age;
		this.ageType = ageType;
	}

	public Integer getAge() {
		return age;
	}

	public ApproximateAgeType getAgeType() {
		return ageType;
	}
}
