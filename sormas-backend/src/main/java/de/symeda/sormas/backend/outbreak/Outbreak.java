package de.symeda.sormas.backend.outbreak;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.user.User;

@Entity
public class Outbreak extends AbstractDomainObject {
	
	private static final long serialVersionUID = 6517638433928902578L;
	
	public static final String TABLE_NAME = "outbreak";
	
	public static final String DISTRICT = "district";
	public static final String DISEASE = "disease";
	public static final String REPORTING_USER = "reportingUser";
	public static final String REPORT_DATE = "reportDate";
	
	private District district;
	private Disease disease;
	private User reportingUser;
	private Date reportDate;
	
	@ManyToOne(cascade = CascadeType.REFRESH, optional = false)
	@JoinColumn(nullable = false)
	public District getDistrict() {
		return district;
	}
	public void setDistrict(District district) {
		this.district = district;
	}
	
	@Enumerated(EnumType.STRING)
	public Disease getDisease() {
		return disease;
	}
	public void setDisease(Disease disease) {
		this.disease = disease;
	}
	
	@ManyToOne(cascade = {})
	@JoinColumn(nullable=false)
	public User getReportingUser() {
		return reportingUser;
	}
	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	public Date getReportDate() {
		return reportDate;
	}
	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}	
}
