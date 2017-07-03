package de.symeda.sormas.backend.epidata;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.location.Location;

@Entity
@Audited
public class EpiDataBurial extends AbstractDomainObject {

	private static final long serialVersionUID = 866789458483672591L;
	
	public static final String BURIAL_PERSON_NAME = "burialPersonName";
	public static final String BURIAL_RELATION = "burialRelation";
	public static final String BURIAL_DATE_FROM = "burialDateFrom";
	public static final String BURIAL_DATE_TO = "burialDateTo";
	public static final String BURIAL_ADDRESS = "burialAddress";
	public static final String BURIAL_ILL = "burialIll";
	public static final String BURIAL_TOUCHING = "burialTouching";
	public static final String EPI_DATA = "epiData";
	
	private EpiData epiData;
	private String burialPersonName;
	private String burialRelation;
	private Date burialDateFrom;
	private Date burialDateTo;
	private Location burialAddress;
	private YesNoUnknown burialIll;
	private YesNoUnknown burialTouching;

	@ManyToOne(cascade = {})
	@JoinColumn(nullable = false)
	public EpiData getEpiData() {
		return epiData;
	}
	public void setEpiData(EpiData epiData) {
		this.epiData = epiData;
	}

	@Column(length=512)
	public String getBurialPersonName() {
		return burialPersonName;
	}
	public void setBurialPersonName(String burialPersonName) {
		this.burialPersonName = burialPersonName;
	}

	@Column(length=512)
	public String getBurialRelation() {
		return burialRelation;
	}
	public void setBurialRelation(String burialRelation) {
		this.burialRelation = burialRelation;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getBurialDateFrom() {
		return burialDateFrom;
	}
	public void setBurialDateFrom(Date burialDateFrom) {
		this.burialDateFrom = burialDateFrom;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getBurialDateTo() {
		return burialDateTo;
	}
	public void setBurialDateTo(Date burialDateTo) {
		this.burialDateTo = burialDateTo;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public Location getBurialAddress() {
		if (burialAddress == null) {
			burialAddress = new Location();
		}
		return burialAddress;
	}
	public void setBurialAddress(Location burialAddress) {
		this.burialAddress = burialAddress;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getBurialIll() {
		return burialIll;
	}
	public void setBurialIll(YesNoUnknown burialIll) {
		this.burialIll = burialIll;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getBurialTouching() {
		return burialTouching;
	}
	public void setBurialTouching(YesNoUnknown burialTouching) {
		this.burialTouching = burialTouching;
	}
	
}
