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

package de.symeda.sormas.api.sormastosormas.sharerequest;

import java.io.Serializable;
import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.HasUuid;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.EmbeddedSensitiveData;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

public class SormasToSormasEventPreview extends PseudonymizableDto implements HasUuid, Serializable {

	private static final long serialVersionUID = -8084434633554426724L;

	public static final String I18N_PREFIX = "Event";

	public static final String UUID = "uuid";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String EVENT_TITLE = "eventTitle";
	public static final String EVENT_DESC = "eventDesc";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String EVENT_LOCATION = "eventLocation";

	private Date reportDateTime;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String eventTitle;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String eventDesc;
	private Disease disease;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String diseaseDetails;
	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	@Valid
	private LocationDto eventLocation;

	public Date getReportDateTime() {
		return reportDateTime;
	}

	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public String getEventDesc() {
		return eventDesc;
	}

	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public String getDiseaseDetails() {
		return diseaseDetails;
	}

	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}

	public LocationDto getEventLocation() {
		return eventLocation;
	}

	public void setEventLocation(LocationDto eventLocation) {
		this.eventLocation = eventLocation;
	}
}
