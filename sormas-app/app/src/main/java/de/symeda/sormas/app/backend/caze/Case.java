package de.symeda.sormas.app.backend.caze;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.user.User;

@Entity(name=Case.TABLE_NAME)
@DatabaseTable(tableName = Case.TABLE_NAME)
public class Case extends AbstractDomainObject {
	
	private static final long serialVersionUID = -2697795184163562129L;

	public static final String TABLE_NAME = "cases";
	public static final String I18N_PREFIX = "CaseData";

	public static final String INVESTIGATION_STATUS = "investigationStatus";
	public static final String DISEASE = "disease";
	public static final String PERSON = "person";
	public static final String REPORT_DATE = "reportDate";
	public static final String SYMPTOMS = "symptoms";

	@DatabaseField(foreign = true, foreignAutoRefresh=true, canBeNull = false, maxForeignAutoRefreshLevel = 3)
	private Person person;

	@Column(length=512)
	private String description;

	@Enumerated(EnumType.STRING)
	private Disease disease;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CaseClassification caseClassification;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private InvestigationStatus investigationStatus;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Region region;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private District district;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Community community;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Facility healthFacility;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Symptoms symptoms;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User reportingUser;
	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date reportDate;
	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date investigatedDate;


	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 1)
	private User surveillanceOfficer;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 1)
	private User caseOfficer;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 1)
	private User contactOfficer;

	@Enumerated(EnumType.STRING)
	private YesNoUnknown pregnant;

	@Enumerated(EnumType.STRING)
	private Vaccination measlesVaccination;

	@Column(length=512)
	private String measlesDoses;

	@Enumerated(EnumType.STRING)
	private VaccinationInfoSource measlesVaccinationInfoSource;

	@Column(length=512)
	private String epidNumber;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Hospitalization hospitalization;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private EpiData epiData;

	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Disease getDisease() {
		return disease;
	}
	public void setDisease(Disease disease) {
		this.disease = disease;
	}
	
	public CaseClassification getCaseClassification() {
		return caseClassification;
	}
	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}

	public Region getRegion() {
		return region;
	}
	public void setRegion(Region region) {
		this.region = region;
	}

	public District getDistrict() {
		return district;
	}
	public void setDistrict(District district) {
		this.district = district;
	}

	public Community getCommunity() {
		return community;
	}
	public void setCommunity(Community community) {
		this.community = community;
	}
	
	public User getReportingUser() {
		return reportingUser;
	}
	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}
	
	public Date getReportDate() {
		return reportDate;
	}
	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public Date getInvestigatedDate() {
		return investigatedDate;
	}
	public void setInvestigatedDate(Date investigatedDate) {
		this.investigatedDate = investigatedDate;
	}
	
	public Facility getHealthFacility() {
		return healthFacility;
	}
	public void setHealthFacility(Facility healthFacility) {
		this.healthFacility = healthFacility;
	}

	public Symptoms getSymptoms() {
		return symptoms;
	}
	public void setSymptoms(Symptoms symptoms) {
		this.symptoms = symptoms;
	}

	public User getSurveillanceOfficer() {
		return surveillanceOfficer;
	}
	public void setSurveillanceOfficer(User surveillanceOfficer) {
		this.surveillanceOfficer = surveillanceOfficer;
	}

	public User getCaseOfficer() {
		return caseOfficer;
	}
	public void setCaseOfficer(User caseOfficer) {
		this.caseOfficer = caseOfficer;
	}

	public User getContactOfficer() {
		return contactOfficer;
	}
	public void setContactOfficer(User contactOfficer) {
		this.contactOfficer = contactOfficer;
	}

	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	public YesNoUnknown getPregnant() {
		return pregnant;
	}

	public void setPregnant(YesNoUnknown pregnant) {
		this.pregnant = pregnant;
	}

	public Vaccination getMeaslesVaccination() {
		return measlesVaccination;
	}

	public void setMeaslesVaccination(Vaccination measlesVaccination) {
		this.measlesVaccination = measlesVaccination;
	}

	public String getMeaslesDoses() {
		return measlesDoses;
	}

	public void setMeaslesDoses(String measlesDoses) {
		this.measlesDoses = measlesDoses;
	}

	public VaccinationInfoSource getMeaslesVaccinationInfoSource() {
		return measlesVaccinationInfoSource;
	}

	public void setMeaslesVaccinationInfoSource(VaccinationInfoSource measlesVaccinationInfoSource) {
		this.measlesVaccinationInfoSource = measlesVaccinationInfoSource;
	}

	public String getEpidNumber() {
		return epidNumber;
	}

	public void setEpidNumber(String epidNumber) {
		this.epidNumber = epidNumber;
	}

	public Hospitalization getHospitalization() {
		return hospitalization;
	}

	public void setHospitalization(Hospitalization hospitalization) {
		this.hospitalization = hospitalization;
	}

	public EpiData getEpiData() {
		return epiData;
	}

	public void setEpiData(EpiData epiData) {
		this.epiData = epiData;
	}

	@Override
	public String toString() {
		return (getPerson() != null ? getPerson().toString() : "") + " (" + DataHelper.getShortUuid(getUuid()) + ")";
	}

	@Override
	public boolean isModifiedOrChildModified() {
		super.isModifiedOrChildModified();
		return person.isModifiedOrChildModified();
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
