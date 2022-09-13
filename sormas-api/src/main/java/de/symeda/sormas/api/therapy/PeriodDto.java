package de.symeda.sormas.api.therapy;

import de.symeda.sormas.api.audit.Auditable;

import java.io.Serializable;
import java.util.Date;

public class PeriodDto implements Auditable, Serializable {

	private static final long serialVersionUID = 4184076586681485994L;

	private Date start;

	private Date end;

	public PeriodDto(Date start, Date end) {
		this.start = start;
		this.end = end;
	}

	public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}

	@Override
	public String getAuditRepresentation() {
		return String.format("%s(start=%s, end=%s)", getClass().getSimpleName(), start, end);
	}
}
