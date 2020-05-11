package de.symeda.sormas.api.caze;

import java.io.Serializable;

public class BirthDateDto implements Serializable {
	private Integer birthdateDD;
	private Integer birthdateMM;
	private Integer birthdateYYYY;

	public BirthDateDto(Integer birthdateDD, Integer birthdateMM, Integer birthdateYYYY) {
		this.birthdateDD = birthdateDD;
		this.birthdateMM = birthdateMM;
		this.birthdateYYYY = birthdateYYYY;
	}

	public Integer getBirthdateDD() {
		return birthdateDD;
	}

	public Integer getBirthdateMM() {
		return birthdateMM;
	}

	public Integer getBirthdateYYYY() {
		return birthdateYYYY;
	}
}
