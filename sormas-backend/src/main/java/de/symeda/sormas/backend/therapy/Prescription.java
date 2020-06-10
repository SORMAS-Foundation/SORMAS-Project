package de.symeda.sormas.backend.therapy;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_BIG;
import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.therapy.TreatmentRoute;
import de.symeda.sormas.api.therapy.TreatmentType;
import de.symeda.sormas.api.therapy.TypeOfDrug;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
@Audited
public class Prescription extends AbstractDomainObject {

	private static final long serialVersionUID = -5028702472324192079L;

	public static final String TABLE_NAME = "prescription";

	public static final String THERAPY = "therapy";
	public static final String PRESCRIPTION_DATE = "prescriptionDate";
	public static final String PRESCRIPTION_START = "prescriptionStart";
	public static final String PRESCRIPTION_END = "prescriptionEnd";
	public static final String PRESCRIBING_CLINICIAN = "prescribingClinician";
	public static final String PRESCRIPTION_TYPE = "prescriptionType";
	public static final String PRESCRIPTION_DETAILS = "prescriptionDetails";
	public static final String TYPE_OF_DRUG = "typeOfDrug";
	public static final String FREQUENCY = "frequency";
	public static final String DOSE = "dose";
	public static final String ROUTE = "route";
	public static final String ROUTE_DETAILS = "routeDetails";
	public static final String ADDITIONAL_NOTES = "additionalNotes";

	private Therapy therapy;
	private Date prescriptionDate;
	private Date prescriptionStart;
	private Date prescriptionEnd;
	private String prescribingClinician;
	private TreatmentType prescriptionType;
	private String prescriptionDetails;
	private TypeOfDrug typeOfDrug;
	private String frequency;
	private String dose;
	private TreatmentRoute route;
	private String routeDetails;
	private String additionalNotes;

	@ManyToOne(cascade = {})
	@JoinColumn(nullable = false)
	public Therapy getTherapy() {
		return therapy;
	}

	public void setTherapy(Therapy therapy) {
		this.therapy = therapy;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	public Date getPrescriptionDate() {
		return prescriptionDate;
	}

	public void setPrescriptionDate(Date prescriptionDate) {
		this.prescriptionDate = prescriptionDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getPrescriptionStart() {
		return prescriptionStart;
	}

	public void setPrescriptionStart(Date prescriptionStart) {
		this.prescriptionStart = prescriptionStart;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getPrescriptionEnd() {
		return prescriptionEnd;
	}

	public void setPrescriptionEnd(Date prescriptionEnd) {
		this.prescriptionEnd = prescriptionEnd;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getPrescribingClinician() {
		return prescribingClinician;
	}

	public void setPrescribingClinician(String prescribingClinician) {
		this.prescribingClinician = prescribingClinician;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public TreatmentType getPrescriptionType() {
		return prescriptionType;
	}

	public void setPrescriptionType(TreatmentType prescriptionType) {
		this.prescriptionType = prescriptionType;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getPrescriptionDetails() {
		return prescriptionDetails;
	}

	public void setPrescriptionDetails(String prescriptionDetails) {
		this.prescriptionDetails = prescriptionDetails;
	}

	@Enumerated(EnumType.STRING)
	public TypeOfDrug getTypeOfDrug() {
		return typeOfDrug;
	}

	public void setTypeOfDrug(TypeOfDrug typeOfDrug) {
		this.typeOfDrug = typeOfDrug;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getDose() {
		return dose;
	}

	public void setDose(String dose) {
		this.dose = dose;
	}

	@Enumerated(EnumType.STRING)
	public TreatmentRoute getRoute() {
		return route;
	}

	public void setRoute(TreatmentRoute route) {
		this.route = route;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getRouteDetails() {
		return routeDetails;
	}

	public void setRouteDetails(String routeDetails) {
		this.routeDetails = routeDetails;
	}

	@Column(length = COLUMN_LENGTH_BIG)
	public String getAdditionalNotes() {
		return additionalNotes;
	}

	public void setAdditionalNotes(String additionalNotes) {
		this.additionalNotes = additionalNotes;
	}

}
