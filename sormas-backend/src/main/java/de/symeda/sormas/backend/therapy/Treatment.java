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
public class Treatment extends AbstractDomainObject {

	private static final long serialVersionUID = 816932182306785932L;

	public static final String TABLE_NAME = "treatment";

	public static final String THERAPY = "therapy";
	public static final String TREATMENT_DATE_TIME = "treatmentDateTime";
	public static final String EXECUTING_CLINICIAN = "executingClinician";
	public static final String TREATMENT_TYPE = "treatmentType";
	public static final String TREATMENT_DETAILS = "treatmentDetails";
	public static final String TYPE_OF_DRUG = "typeOfDrug";
	public static final String DOSE = "dose";
	public static final String ROUTE = "route";
	public static final String ROUTE_DETAILS = "routeDetails";
	public static final String ADDITIONAL_NOTES = "additionalNotes";
	public static final String PRESCRIPTION = "prescription";

	private Therapy therapy;
	private Date treatmentDateTime;
	private String executingClinician;
	private TreatmentType treatmentType;
	private String treatmentDetails;
	private TypeOfDrug typeOfDrug;
	private String dose;
	private TreatmentRoute route;
	private String routeDetails;
	private String additionalNotes;
	private Prescription prescription;

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
	public Date getTreatmentDateTime() {
		return treatmentDateTime;
	}

	public void setTreatmentDateTime(Date treatmentDateTime) {
		this.treatmentDateTime = treatmentDateTime;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getExecutingClinician() {
		return executingClinician;
	}

	public void setExecutingClinician(String executingClinician) {
		this.executingClinician = executingClinician;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public TreatmentType getTreatmentType() {
		return treatmentType;
	}

	public void setTreatmentType(TreatmentType treatmentType) {
		this.treatmentType = treatmentType;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getTreatmentDetails() {
		return treatmentDetails;
	}

	public void setTreatmentDetails(String treatmentDetails) {
		this.treatmentDetails = treatmentDetails;
	}

	@Enumerated(EnumType.STRING)
	public TypeOfDrug getTypeOfDrug() {
		return typeOfDrug;
	}

	public void setTypeOfDrug(TypeOfDrug typeOfDrug) {
		this.typeOfDrug = typeOfDrug;
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

	@ManyToOne(cascade = {})
	public Prescription getPrescription() {
		return prescription;
	}

	public void setPrescription(Prescription prescription) {
		this.prescription = prescription;
	}

}
