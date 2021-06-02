package de.symeda.sormas.ui.utils.components.birthdate;

import java.io.Serializable;

public class BirthDateDto implements Serializable {

	public static final String DATE_OF_BIRTH_YYYY = "dateOfBirthYYYY";
	public static final String DATE_OF_BIRTH_MM = "dateOfBirthMM";
	public static final String DATE_OF_BIRTH_DD = "dateOfBirthDD";

	private Integer dateOfBirthYYYY;
	private Integer dateOfBirthMM;
	private Integer dateOfBirthDD;

	public Integer getDateOfBirthYYYY() {
		return dateOfBirthYYYY;
	}

	public void setDateOfBirthYYYY(Integer dateOfBirthYYYY) {
		this.dateOfBirthYYYY = dateOfBirthYYYY;
	}

	public Integer getDateOfBirthMM() {
		return dateOfBirthMM;
	}

	public void setDateOfBirthMM(Integer dateOfBirthMM) {
		this.dateOfBirthMM = dateOfBirthMM;
	}

	public Integer getDateOfBirthDD() {
		return dateOfBirthDD;
	}

	public void setDateOfBirthDD(Integer dateOfBirthDD) {
		this.dateOfBirthDD = dateOfBirthDD;
	}
}
