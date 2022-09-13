package de.symeda.sormas.api.caze;

import java.io.Serializable;

import de.symeda.sormas.api.audit.Auditable;
import de.symeda.sormas.api.person.ApproximateAgeType;

public class AgeAndBirthDateDto extends BirthDateDto implements Auditable, Serializable {

	private static final long serialVersionUID = -3544971830146580773L;

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

	@Override
	public String getAuditRepresentation() {
		return String.format("%s(ageType=%s,%s)", getClass().getSimpleName(), ageType, super.getAuditRepresentation());
	}
}
