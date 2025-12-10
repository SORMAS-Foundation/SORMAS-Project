package de.symeda.sormas.backend.therapy;


import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
public class Therapy extends AbstractDomainObject {

	private static final long serialVersionUID = -1467303502817738376L;

	public static final String TABLE_NAME = "therapy";

	public static final String DIRECTLY_OBSERVED_TREATMENT = "directlyObservedTreatment";
	public static final String MDR_XDR_TUBERCULOSIS = "mdrXdrTuberculosis";
	public static final String BEIJING_LINEAGE = "beijingLineage";
	public static final String CASE = "caze";

	private boolean directlyObservedTreatment;
	private boolean mdrXdrTuberculosis;
	private boolean beijingLineage;
	private Case caze;

	private YesNoUnknown treatmentStarted;
	private boolean treatmentNotApplicable;
	private Date treatmentStartDate;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = Case.THERAPY)
	public Case getCaze() {
		return caze;
	}

	public void setCaze(Case caze) {
		this.caze = caze;
	}

	public boolean isDirectlyObservedTreatment() {
		return directlyObservedTreatment;
	}

	public void setDirectlyObservedTreatment(boolean directlyObservedTreatment) {
		this.directlyObservedTreatment = directlyObservedTreatment;
	}

	public boolean isMdrXdrTuberculosis() {
		return mdrXdrTuberculosis;
	}

	public void setMdrXdrTuberculosis(boolean mdrXdrTuberculosis) {
		this.mdrXdrTuberculosis = mdrXdrTuberculosis;
	}

	public boolean isBeijingLineage() {
		return beijingLineage;
	}

	public void setBeijingLineage(boolean beijingLineage) {
		this.beijingLineage = beijingLineage;
	}

	@Column
	@Enumerated(EnumType.STRING)
	public YesNoUnknown getTreatmentStarted() {
		return treatmentStarted;
	}

	public void setTreatmentStarted(YesNoUnknown treatmentStarted) {
		this.treatmentStarted = treatmentStarted;
	}

	@Column
	public boolean isTreatmentNotApplicable() {
		return treatmentNotApplicable;
	}

	public void setTreatmentNotApplicable(boolean treatmentNotApplicable) {
		this.treatmentNotApplicable = treatmentNotApplicable;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getTreatmentStartDate() {
		return treatmentStartDate;
	}

	public void setTreatmentStartDate(Date treatmentStartDate) {
		this.treatmentStartDate = treatmentStartDate;
	}
}
