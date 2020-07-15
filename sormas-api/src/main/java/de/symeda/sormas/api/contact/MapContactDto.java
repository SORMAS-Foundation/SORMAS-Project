/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.contact;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.LatitudePseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.LongitudePseudonymizer;

public class MapContactDto implements Serializable {

	private static final long serialVersionUID = -5840120135940125045L;

	private String uuid;
	private ContactClassification contactClassification;
	@SensitiveData
	@Pseudonymizer(LatitudePseudonymizer.class)
	private Double reportLat;
	@SensitiveData
	@Pseudonymizer(LongitudePseudonymizer.class)
	private Double reportLon;
	@PersonalData
	@SensitiveData
	@Pseudonymizer(LatitudePseudonymizer.class)
	private Double addressLat;
	@PersonalData
	@SensitiveData
	@Pseudonymizer(LongitudePseudonymizer.class)
	private Double addressLon;

	private Date lastVisitDateTime;
	private Date caseOnsetDate;
	private Date caseReportDate;
	private Date contactReportDate;
	private String personFirstName;
	private String personLastName;
	private String casePersonFirstName;
	private String casePersonLastName;

	public MapContactDto(
		String uuid,
		ContactClassification contactClassification,
		Double reportLat,
		Double reportLon,
		Double addressLat,
		Double addressLon,
		Date caseOnsetDate,
		Date caseReportDate,
		Date contactReportDate,
		String personFirstName,
		String personLastName,
		String casePersonFirstName,
		String casePersonLastName) {

		this.uuid = uuid;
		this.contactClassification = contactClassification;
		this.reportLat = reportLat;
		this.reportLon = reportLon;
		this.addressLat = addressLat;
		this.addressLon = addressLon;
		this.caseOnsetDate = caseOnsetDate;
		this.caseReportDate = caseReportDate;
		this.contactReportDate = contactReportDate;
		this.personFirstName = personFirstName;
		this.personLastName = personLastName;
		this.casePersonFirstName = casePersonFirstName;
		this.casePersonLastName = casePersonLastName;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public ContactClassification getContactClassification() {
		return contactClassification;
	}

	public void setContactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
	}

	public Double getReportLat() {
		return reportLat;
	}

	public void setReportLat(Double reportLat) {
		this.reportLat = reportLat;
	}

	public Double getReportLon() {
		return reportLon;
	}

	public void setReportLon(Double reportLon) {
		this.reportLon = reportLon;
	}

	public Double getAddressLat() {
		return addressLat;
	}

	public void setAddressLat(Double addressLat) {
		this.addressLat = addressLat;
	}

	public Double getAddressLon() {
		return addressLon;
	}

	public void setAddressLon(Double addressLon) {
		this.addressLon = addressLon;
	}

	public Date getLastVisitDateTime() {
		return lastVisitDateTime;
	}

	public void setLastVisitDateTime(Date lastVisitDateTime) {
		this.lastVisitDateTime = lastVisitDateTime;
	}

	public Date getCaseOnsetDate() {
		return caseOnsetDate;
	}

	public void setCaseOnsetDate(Date caseOnsetDate) {
		this.caseOnsetDate = caseOnsetDate;
	}

	public Date getCaseReportDate() {
		return caseReportDate;
	}

	public void setCaseReportDate(Date caseReportDate) {
		this.caseReportDate = caseReportDate;
	}

	public String getPersonFirstName() {
		return personFirstName;
	}

	public void setPersonFirstName(String personFirstName) {
		this.personFirstName = personFirstName;
	}

	public String getPersonLastName() {
		return personLastName;
	}

	public void setPersonLastName(String personLastName) {
		this.personLastName = personLastName;
	}

	public String getCasePersonFirstName() {
		return casePersonFirstName;
	}

	public void setCasePersonFirstName(String casePersonFirstName) {
		this.casePersonFirstName = casePersonFirstName;
	}

	public String getCasePersonLastName() {
		return casePersonLastName;
	}

	public void setCasePersonLastName(String casePersonLastName) {
		this.casePersonLastName = casePersonLastName;
	}

	public Date getContactReportDate() {
		return contactReportDate;
	}

	public void setContactReportDate(Date contactReportDate) {
		this.contactReportDate = contactReportDate;
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(personFirstName).append(" ").append(personLastName.toUpperCase());
		if (casePersonFirstName != null && casePersonLastName != null) {
			builder.append(StringUtils.wrap(I18nProperties.getString(Strings.toCase), ""));
			builder.append(casePersonFirstName).append(" ").append(casePersonLastName.toUpperCase());
		}
		return builder.toString();
	}
}
