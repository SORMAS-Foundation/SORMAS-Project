/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.adverseeventsfollowingimmunization;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class AefiListEntryDto extends PseudonymizableIndexDto implements Serializable, Cloneable {

	public static final String I18N_PREFIX = "Aefi";

	public static final String UUID = "uuid";
	public static final String SERIOUS = "serious";
	public static final String PRIMARY_VACCINE_NAME = "primaryVaccineName";
	public static final String PRIMARY_VACCINE_DOSE = "primaryVaccineDose";
	public static final String PRIMARY_VACCINE_VACCINATION_DATE = "primaryVaccineVaccinationDate";
	private String ADVERSE_EVENTS = "adverseEvents";

	private YesNoUnknown serious;
	private Vaccine primaryVaccineName;
	private String primaryVaccineDose;
	private Date primaryVaccineVaccinationDate;
	private String adverseEvents;

	public AefiListEntryDto(
		String uuid,
		YesNoUnknown serious,
		Vaccine primaryVaccineName,
		String primaryVaccineDose,
		Date primaryVaccineVaccinationDate,
		String adverseEvents) {

		super(uuid);
		this.serious = serious;
		this.primaryVaccineName = primaryVaccineName;
		this.primaryVaccineDose = primaryVaccineDose;
		this.primaryVaccineVaccinationDate = primaryVaccineVaccinationDate;
		this.adverseEvents = adverseEvents;
	}

	public YesNoUnknown getSerious() {
		return serious;
	}

	public void setSerious(YesNoUnknown serious) {
		this.serious = serious;
	}

	public Vaccine getPrimaryVaccineName() {
		return primaryVaccineName;
	}

	public void setPrimaryVaccineName(Vaccine primaryVaccineName) {
		this.primaryVaccineName = primaryVaccineName;
	}

	public String getPrimaryVaccineDose() {
		return primaryVaccineDose;
	}

	public void setPrimaryVaccineDose(String primaryVaccineDose) {
		this.primaryVaccineDose = primaryVaccineDose;
	}

	public Date getPrimaryVaccineVaccinationDate() {
		return primaryVaccineVaccinationDate;
	}

	public void setPrimaryVaccineVaccinationDate(Date primaryVaccineVaccinationDate) {
		this.primaryVaccineVaccinationDate = primaryVaccineVaccinationDate;
	}

	public String getAdverseEvents() {
		return adverseEvents;
	}

	public void setAdverseEvents(String adverseEvents) {
		this.adverseEvents = adverseEvents;
	}
}
