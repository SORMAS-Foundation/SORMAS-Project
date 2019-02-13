package de.symeda.sormas.backend.clinicalcourse;

import javax.persistence.Entity;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
@Audited
public class ClinicalCourse extends AbstractDomainObject {

	private static final long serialVersionUID = -2664896907352864261L;
	
	public static final String TABLE_NAME = "clinicalcourse";

}
