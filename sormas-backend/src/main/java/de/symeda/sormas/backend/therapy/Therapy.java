package de.symeda.sormas.backend.therapy;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
@Audited
public class Therapy extends AbstractDomainObject {

	private static final long serialVersionUID = -1467303502817738376L;

	public static final String TABLE_NAME = "therapy";

	public static final String CASE = "caze";

	private Case caze;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = Case.THERAPY)
	public Case getCaze() {
		return caze;
	}

	public void setCaze(Case caze) {
		this.caze = caze;
	}
}
