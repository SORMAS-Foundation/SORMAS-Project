package de.symeda.sormas.backend.therapy;

import javax.persistence.Entity;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
@Audited
public class Therapy extends AbstractDomainObject {

	private static final long serialVersionUID = -1467303502817738376L;
	
	public static final String TABLE_NAME = "therapy";

}
