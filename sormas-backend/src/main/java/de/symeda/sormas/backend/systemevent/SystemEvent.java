package de.symeda.sormas.backend.systemevent;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.systemevents.SystemEventStatus;
import de.symeda.sormas.api.systemevents.SystemEventType;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
@Audited
public class SystemEvent extends AbstractDomainObject {

	public static final String TABLE_NAME = "systemevent";

	public static final String TYPE = "type";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String STATUS = "status";
	public static final String ADDITIONAL_INFO = "additionalInfo";

	private SystemEventType type;
	private Date startDate;
	private Date endDate;
	private SystemEventStatus status;
	private String additionalInfo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public SystemEventType getType() {
		return type;
	}

	public void setType(SystemEventType type) {
		this.type = type;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public SystemEventStatus getStatus() {
		return status;
	}

	public void setStatus(SystemEventStatus status) {
		this.status = status;
	}

	@Column(columnDefinition = "text")
	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		SystemEvent that = (SystemEvent) o;
		return type == that.type
			&& Objects.equals(startDate, that.startDate)
			&& Objects.equals(endDate, that.endDate)
			&& status == that.status
			&& Objects.equals(additionalInfo, that.additionalInfo);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), type, startDate, endDate, status, additionalInfo);
	}
}
