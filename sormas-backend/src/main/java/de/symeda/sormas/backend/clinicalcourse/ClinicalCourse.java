package de.symeda.sormas.backend.clinicalcourse;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
@Audited
public class ClinicalCourse extends AbstractDomainObject {

	private static final long serialVersionUID = -2664896907352864261L;

	public static final String TABLE_NAME = "clinicalcourse";

	public static final String CASE = "caze";

	private Case caze;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = Case.CLINICAL_COURSE)
	public Case getCaze() {
		return caze;
	}

	public void setCaze(Case caze) {
		this.caze = caze;
	}
}
