/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.therapy;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_BIG;
import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.therapy.TreatmentRoute;
import de.symeda.sormas.api.therapy.TreatmentType;
import de.symeda.sormas.api.therapy.TypeOfDrug;
import de.symeda.sormas.app.backend.common.ParentAdo;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;

@Entity(name = Treatment.TABLE_NAME)
@DatabaseTable(tableName = Treatment.TABLE_NAME)
public class Treatment extends PseudonymizableAdo {

	private static final long serialVersionUID = 816932182306785932L;

	public static final String TABLE_NAME = "treatment";
	public static final String I18N_PREFIX = "Treatment";

	public static final String THERAPY = "therapy";
	public static final String TREATMENT_DATE_TIME = "treatmentDateTime";

	@DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
	private Therapy therapy;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date treatmentDateTime;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String executingClinician;

	@Enumerated(EnumType.STRING)
	private TreatmentType treatmentType;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String treatmentDetails;

	@Enumerated(EnumType.STRING)
	private TypeOfDrug typeOfDrug;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String dose;

	@Enumerated(EnumType.STRING)
	private TreatmentRoute route;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String routeDetails;

	@Column(length = COLUMN_LENGTH_BIG)
	private String additionalNotes;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Prescription prescription;

	@ParentAdo
	public Therapy getTherapy() {
		return therapy;
	}

	public void setTherapy(Therapy therapy) {
		this.therapy = therapy;
	}

	public Date getTreatmentDateTime() {
		return treatmentDateTime;
	}

	public void setTreatmentDateTime(Date treatmentDateTime) {
		this.treatmentDateTime = treatmentDateTime;
	}

	public String getExecutingClinician() {
		return executingClinician;
	}

	public void setExecutingClinician(String executingClinician) {
		this.executingClinician = executingClinician;
	}

	public TreatmentType getTreatmentType() {
		return treatmentType;
	}

	public void setTreatmentType(TreatmentType treatmentType) {
		this.treatmentType = treatmentType;
	}

	public String getTreatmentDetails() {
		return treatmentDetails;
	}

	public void setTreatmentDetails(String treatmentDetails) {
		this.treatmentDetails = treatmentDetails;
	}

	public TypeOfDrug getTypeOfDrug() {
		return typeOfDrug;
	}

	public void setTypeOfDrug(TypeOfDrug typeOfDrug) {
		this.typeOfDrug = typeOfDrug;
	}

	public String getDose() {
		return dose;
	}

	public void setDose(String dose) {
		this.dose = dose;
	}

	public TreatmentRoute getRoute() {
		return route;
	}

	public void setRoute(TreatmentRoute route) {
		this.route = route;
	}

	public String getRouteDetails() {
		return routeDetails;
	}

	public void setRouteDetails(String routeDetails) {
		this.routeDetails = routeDetails;
	}

	public String getAdditionalNotes() {
		return additionalNotes;
	}

	public void setAdditionalNotes(String additionalNotes) {
		this.additionalNotes = additionalNotes;
	}

	public Prescription getPrescription() {
		return prescription;
	}

	public void setPrescription(Prescription prescription) {
		this.prescription = prescription;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

}
