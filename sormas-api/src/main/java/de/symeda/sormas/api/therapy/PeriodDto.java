package de.symeda.sormas.api.therapy;

import java.io.Serializable;
import java.util.Date;

public class PeriodDto implements Serializable {

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
}
