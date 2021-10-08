package de.symeda.sormas.api.caze;

import java.io.Serializable;

import de.symeda.sormas.api.utils.PersonalData;

public class BirthDateDto implements Serializable {

	private static final long serialVersionUID = -905128183629450296L;

	public static final String DATE_OF_BIRTH_DD = "dateOfBirthDD";
	public static final String DATE_OF_BIRTH_MM = "dateOfBirthMM";
	public static final String DATE_OF_BIRTH_YYYY = "dateOfBirthYYYY";

	@PersonalData
	private Integer dateOfBirthDD;
	private Integer dateOfBirthMM;
	private Integer dateOfBirthYYYY;

	public BirthDateDto() {
	}

	public BirthDateDto(Integer dateOfBirthDD, Integer dateOfBirthMM, Integer dateOfBirthYYYY) {

		this.dateOfBirthDD = dateOfBirthDD;
		this.dateOfBirthMM = dateOfBirthMM;
		this.dateOfBirthYYYY = dateOfBirthYYYY;
	}

	public Integer getDateOfBirthDD() {
		return dateOfBirthDD;
	}

	public void setDateOfBirthDD(Integer dateOfBirthDD) {
		this.dateOfBirthDD = dateOfBirthDD;
	}

	public Integer getDateOfBirthMM() {
		return dateOfBirthMM;
	}

	public void setDateOfBirthMM(Integer dateOfBirthMM) {
		this.dateOfBirthMM = dateOfBirthMM;
	}

	public Integer getDateOfBirthYYYY() {
		return dateOfBirthYYYY;
	}

	public void setDateOfBirthYYYY(Integer dateOfBirthYYYY) {
		this.dateOfBirthYYYY = dateOfBirthYYYY;
	}
}
