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
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.ParentAdo;

@Entity(name = Prescription.TABLE_NAME)
@DatabaseTable(tableName = Prescription.TABLE_NAME)
public class Prescription extends AbstractDomainObject {

	private static final long serialVersionUID = -5028702472324192079L;

	public static final String TABLE_NAME = "prescription";
	public static final String I18N_PREFIX = "Prescription";

	public static final String THERAPY = "therapy";
	public static final String PRESCRIPTION_DATE = "prescriptionDate";

	@DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
	private Therapy therapy;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date prescriptionDate;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date prescriptionStart;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date prescriptionEnd;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String prescribingClinician;

	@Enumerated(EnumType.STRING)
	private TreatmentType prescriptionType;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String prescriptionDetails;

	@Enumerated(EnumType.STRING)
	private TypeOfDrug typeOfDrug;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String frequency;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String dose;

	@Enumerated(EnumType.STRING)
	private TreatmentRoute route;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String routeDetails;

	@Column(length = COLUMN_LENGTH_BIG)
	private String additionalNotes;

	@ParentAdo
	public Therapy getTherapy() {
		return therapy;
	}

	public void setTherapy(Therapy therapy) {
		this.therapy = therapy;
	}

	public Date getPrescriptionDate() {
		return prescriptionDate;
	}

	public void setPrescriptionDate(Date prescriptionDate) {
		this.prescriptionDate = prescriptionDate;
	}

	public Date getPrescriptionStart() {
		return prescriptionStart;
	}

	public void setPrescriptionStart(Date prescriptionStart) {
		this.prescriptionStart = prescriptionStart;
	}

	public Date getPrescriptionEnd() {
		return prescriptionEnd;
	}

	public void setPrescriptionEnd(Date prescriptionEnd) {
		this.prescriptionEnd = prescriptionEnd;
	}

	public String getPrescribingClinician() {
		return prescribingClinician;
	}

	public void setPrescribingClinician(String prescribingClinician) {
		this.prescribingClinician = prescribingClinician;
	}

	public TreatmentType getPrescriptionType() {
		return prescriptionType;
	}

	public void setPrescriptionType(TreatmentType prescriptionType) {
		this.prescriptionType = prescriptionType;
	}

	public String getPrescriptionDetails() {
		return prescriptionDetails;
	}

	public void setPrescriptionDetails(String prescriptionDetails) {
		this.prescriptionDetails = prescriptionDetails;
	}

	public TypeOfDrug getTypeOfDrug() {
		return typeOfDrug;
	}

	public void setTypeOfDrug(TypeOfDrug typeOfDrug) {
		this.typeOfDrug = typeOfDrug;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
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

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

}
