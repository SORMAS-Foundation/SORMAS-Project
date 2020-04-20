package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.person.BurialConductor;

import java.util.Date;

public class BurialInfoDto {
	private Date burialDate;
	private BurialConductor burialConductor;
	private String burialPlaceDescription;

	public BurialInfoDto(Date burialDate, BurialConductor burialConductor, String burialPlaceDescription) {
		this.burialDate = burialDate;
		this.burialConductor = burialConductor;
		this.burialPlaceDescription = burialPlaceDescription;
	}

	public Date getBurialDate() {
		return burialDate;
	}

	public BurialConductor getBurialConductor() {
		return burialConductor;
	}

	public String getBurialPlaceDescription() {
		return burialPlaceDescription;
	}
}
