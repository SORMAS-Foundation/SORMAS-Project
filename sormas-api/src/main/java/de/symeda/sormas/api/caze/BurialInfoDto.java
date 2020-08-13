package de.symeda.sormas.api.caze;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.person.BurialConductor;
import de.symeda.sormas.api.utils.SensitiveData;

public class BurialInfoDto implements Serializable {

	private static final long serialVersionUID = -8353779195208414541L;

	private Date burialDate;
	private BurialConductor burialConductor;
	@SensitiveData
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
