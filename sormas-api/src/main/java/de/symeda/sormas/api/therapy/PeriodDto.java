package de.symeda.sormas.api.therapy;

import java.io.Serializable;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object for time period data")
public class PeriodDto implements Serializable {

	private static final long serialVersionUID = 4184076586681485994L;
	@Schema(description = "Date when the time period starts")
	private Date start;

	@Schema(description = "Date when the time period ends")
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
