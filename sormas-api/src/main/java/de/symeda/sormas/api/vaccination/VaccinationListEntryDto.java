/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.vaccination;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;
import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class VaccinationListEntryDto extends PseudonymizableIndexDto implements Serializable, Cloneable {

	private static final long serialVersionUID = 5665903874736291912L;

	private String uuid;
	private Vaccine vaccineName;
	private String otherVaccineName;
	private Date vaccinationDate;
	private Disease disease;
	private boolean isRelevant;
	private String nonRelevantMessage;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Vaccine getVaccineName() {
		return vaccineName;
	}

	public void setVaccineName(Vaccine vaccineName) {
		this.vaccineName = vaccineName;
	}

	public String getOtherVaccineName() {
		return otherVaccineName;
	}

	public void setOtherVaccineName(String otherVaccineName) {
		this.otherVaccineName = otherVaccineName;
	}

	public Date getVaccinationDate() {
		return vaccinationDate;
	}

	public void setVaccinationDate(Date vaccinationDate) {
		this.vaccinationDate = vaccinationDate;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public boolean isRelevant() {
		return isRelevant;
	}

	public void setRelevant(boolean relevant) {
		isRelevant = relevant;
	}

	public String getNonRelevantMessage() {
		return nonRelevantMessage;
	}

	public void setNonRelevantMessage(String nonRelevantMessage) {
		this.nonRelevantMessage = nonRelevantMessage;
	}

	public VaccinationReferenceDto toReference() {
		return new VaccinationReferenceDto(uuid, toString());
	}

	@Override
	public String toString() {
		String date = DateFormatHelper.formatLocalDate(vaccinationDate);

		final String vaccine;
		if(vaccineName != null) {
			vaccine = vaccineName != Vaccine.OTHER ? vaccineName.toString() : otherVaccineName;
		} else {
			vaccine = I18nProperties.getString(Strings.labelNoVaccineName);
		}

		return (date.isEmpty() ? "" : date + " - ") + vaccine + " (" + DataHelper.getShortUuid(uuid) +")";
	}
}
