package de.symeda.sormas.api.therapy;

import de.symeda.sormas.api.audit.AuditInclude;
import de.symeda.sormas.api.audit.AuditedClass;

import java.io.Serializable;
import java.util.Date;

@AuditedClass
public class PeriodDto implements Serializable {

	private static final long serialVersionUID = 4184076586681485994L;

	private Date start;

	private Date end;

	public PeriodDto(Date start, Date end) {
		this.start = start;
		this.end = end;
	}

	@AuditInclude
	public Date getStart() {
		return start;
	}

	@AuditInclude
	public Date getEnd() {
		return end;
	}

}
