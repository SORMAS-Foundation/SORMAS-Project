package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.utils.PersonalData;

import java.io.Serializable;

public class BirthDateDto implements Serializable {
	@PersonalData
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

	public void setBirthdateDD(Integer birthdateDD) {
		this.birthdateDD = birthdateDD;
	}

	public Integer getBirthdateMM() {
		return birthdateMM;
	}

	public Integer getBirthdateYYYY() {
		return birthdateYYYY;
	}
}
