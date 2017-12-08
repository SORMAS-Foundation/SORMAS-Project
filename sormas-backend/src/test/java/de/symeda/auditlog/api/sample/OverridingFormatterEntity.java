package de.symeda.auditlog.api.sample;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.sormas.api.HasUuid;

@Audited
public class OverridingFormatterEntity implements HasUuid {

	public static final String THE_DATE = "theDate";
	public static final String THE_DATE_WITHOUT_TEMPORAL = "theDateWithoutTemporal";

	private String uuid;
	private Date theDate;
	private Date theDateWithoutTemporal;

	public OverridingFormatterEntity(String uuid, Date theDate, Date theDateWithoutTemporal) {
		
		this.uuid = uuid;
		this.theDate = theDate;
		this.theDateWithoutTemporal = theDateWithoutTemporal;
	}

	@AuditedIgnore
	public String getUuid() {
		return uuid;
	}

	@Temporal(TemporalType.TIME)
	public Date getTheDate() {
		return theDate;
	}

	public Date getTheDateWithoutTemporal() {
		return theDateWithoutTemporal;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setTheDate(Date theDate) {
		this.theDate = theDate;
	}

}
