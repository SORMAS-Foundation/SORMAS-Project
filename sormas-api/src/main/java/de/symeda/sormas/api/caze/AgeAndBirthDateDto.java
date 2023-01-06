package de.symeda.sormas.api.caze;

import java.io.Serializable;

import de.symeda.sormas.api.person.ApproximateAgeType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data transfer object for data related to a person's age and birthdate")
public class AgeAndBirthDateDto extends BirthDateDto implements Serializable {

	private static final long serialVersionUID = -3544971830146580773L;

	@Schema(description = "Age of a person")
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
