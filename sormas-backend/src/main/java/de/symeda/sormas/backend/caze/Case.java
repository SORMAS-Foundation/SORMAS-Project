package de.symeda.sormas.backend.caze;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.PlagueType;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.hospitalization.Hospitalization;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.user.User;

@Entity(name="cases")
@Audited
public class Case extends AbstractDomainObject {
	
	private static final long serialVersionUID = -2697795184663562129L;
	
	public static final String TABLE_NAME = "cases";

	public static final String CASE_CLASSIFICATION = "caseClassification";
	public static final String INVESTIGATION_STATUS = "investigationStatus";
	public static final String PERSON = "person";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String PLAGUE_TYPE = "plagueType";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String HEALTH_FACILITY_DETAILS = "healthFacilityDetails";
	public static final String REPORTING_USER = "reportingUser";
	public static final String REPORT_DATE = "reportDate";
	public static final String INVESTIGATED_DATE = "investigatedDate";
	public static final String RECEPTION_DATE = "receptionDate";
	public static final String SURVEILLANCE_OFFICER = "surveillanceOfficer";
	public static final String CASE_OFFICER = "caseOfficer";
	public static final String SYMPTOMS = "symptoms";
	public static final String TASKS = "tasks";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String HOSPITALIZATION = "hospitalization";
	public static final String EPI_DATA = "epiData";
	public static final String PREGNANT = "pregnant";
	public static final String VACCINATION = "vaccination";
	public static final String VACCINATION_DOSES = "vaccinationDoses";
	public static final String VACCINATION_INFO_SOURCE = "vaccinationInfoSource";
	public static final String SMALLPOX_VACCINATION_SCAR = "smallpoxVaccinationScar";
	public static final String EPID_NUMBER = "epidNumber";
	public static final String REPORT_LAT = "reportLat";
	public static final String REPORT_LON = "reportLon";
	public static final String OUTCOME = "outcome";
	public static final String OUTCOME_DATE = "outcomeDate";

	private Person person;
	private String description;
	private Disease disease;
	private String diseaseDetails;
	private PlagueType plagueType;
	private CaseClassification caseClassification;
	private InvestigationStatus investigationStatus;
	private Hospitalization hospitalization;
	private EpiData epiData;
	
	private Region region;
	private District district;
	private Community community;
	private Facility healthFacility;
	private String healthFacilityDetails;
	
	private User reportingUser;
	private Date reportDate;
	private Double reportLat;
	private Double reportLon;
	private Float reportLatLonAccuracy;
	
	private Date investigatedDate;
	private Date receptionDate;
	
	private User surveillanceOfficer;
	private User caseOfficer;
	
	private Symptoms symptoms;
	
	private YesNoUnknown pregnant;

	private Vaccination vaccination;
	private String vaccinationDoses;
	private VaccinationInfoSource vaccinationInfoSource;	
	private Date vaccinationDate;
	private YesNoUnknown smallpoxVaccinationScar;
	private YesNoUnknown smallpoxVaccinationReceived;
	
	private String epidNumber;
	
	private CaseOutcome outcome;
	private Date outcomeDate;
	
	private List<Task> tasks;
	
	@ManyToOne(cascade = {})
	@JoinColumn(nullable=false)
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	
	@Column(length=512)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Enumerated(EnumType.STRING)
	public Disease getDisease() {
		return disease;
	}
	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@Column(length=512)
	public String getDiseaseDetails() {
		return diseaseDetails;
	}
	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}

	@Enumerated(EnumType.STRING)
	public PlagueType getPlagueType() {
		return plagueType;
	}
	public void setPlagueType(PlagueType plagueType) {
		this.plagueType = plagueType;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public CaseClassification getCaseClassification() {
		return caseClassification;
	}
	public void setCaseClassification(CaseClassification caseStatus) {
		this.caseClassification = caseStatus;
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

	@Temporal(TemporalType.TIMESTAMP)
	public Date getInvestigatedDate() {
		return investigatedDate;
	}
	public void setInvestigatedDate(Date investigatedDate) {
		this.investigatedDate = investigatedDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getReceptionDate() {
		return receptionDate;
	}
	public void setReceptionDate(Date receptionDate) {
		this.receptionDate = receptionDate;
	}
	
	@ManyToOne(cascade = {})
	public Facility getHealthFacility() {
		return healthFacility;
	}
	public void setHealthFacility(Facility healthFacility) {
		this.healthFacility = healthFacility;
	}
	
	@Column(length = 512)
	public String getHealthFacilityDetails() {
		return healthFacilityDetails;
	}
	public void setHealthFacilityDetails(String healthFacilityDetails) {
		this.healthFacilityDetails = healthFacilityDetails;
	}
	
	@ManyToOne(cascade = {})
	public User getSurveillanceOfficer() {
		return surveillanceOfficer;
	}
	public void setSurveillanceOfficer(User surveillanceOfficer) {
		this.surveillanceOfficer = surveillanceOfficer;
	}

	@ManyToOne(cascade = {})
	public User getCaseOfficer() {
		return caseOfficer;
	}
	public void setCaseOfficer(User caseOfficer) {
		this.caseOfficer = caseOfficer;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@AuditedIgnore
	public Symptoms getSymptoms() {
		if (symptoms == null) {
			symptoms = new Symptoms();
		}
		return symptoms;
	}
	public void setSymptoms(Symptoms symptoms) {
		this.symptoms = symptoms;
	}
	
	@ManyToOne(cascade = {})
	public Region getRegion() {
		return region;
	}
	public void setRegion(Region region) {
		this.region = region;
	}

	@ManyToOne(cascade = {})
	public District getDistrict() {
		return district;
	}
	public void setDistrict(District district) {
		this.district = district;
	}

	@ManyToOne(cascade = {})
	public Community getCommunity() {
		return community;
	}
	public void setCommunity(Community community) {
		this.community = community;
	}
	
	// It's necessary to do a lazy fetch here because having three eager fetching one to one relations
	// produces an error where two non-xa connections are opened
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@AuditedIgnore
	public Hospitalization getHospitalization() {
		if (hospitalization == null) {
			hospitalization = new Hospitalization();
		}
		return hospitalization;
	}
	public void setHospitalization(Hospitalization hospitalization) {
		this.hospitalization = hospitalization;
	}
	
	// It's necessary to do a lazy fetch here because having three eager fetching one to one relations
	// produces an error where two non-xa connections are opened
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@AuditedIgnore
	public EpiData getEpiData() {
		if(epiData == null) {
			epiData = new EpiData();
		}
		return epiData;
	}
	public void setEpiData(EpiData epiData) {
		this.epiData = epiData;
	}
	
	@Enumerated(EnumType.STRING)
	public YesNoUnknown getPregnant() {
		return pregnant;
	}
	public void setPregnant(YesNoUnknown pregnant) {
		this.pregnant = pregnant;
	}

	@Enumerated(EnumType.STRING)
	public Vaccination getVaccination() {
		return vaccination;
	}
	public void setVaccination(Vaccination vaccination) {
		this.vaccination = vaccination;
	}

	@Column(length = 512)
	public String getVaccinationDoses() {
		return vaccinationDoses;
	}
	public void setVaccinationDoses(String vaccinationDoses) {
		this.vaccinationDoses = vaccinationDoses;
	}

	@Enumerated(EnumType.STRING)
	public VaccinationInfoSource getVaccinationInfoSource() {
		return vaccinationInfoSource;
	}
	public void setVaccinationInfoSource(VaccinationInfoSource vaccinationInfoSource) {
		this.vaccinationInfoSource = vaccinationInfoSource;
	}
	
	@Enumerated(EnumType.STRING)
	public YesNoUnknown getSmallpoxVaccinationScar() {
		return smallpoxVaccinationScar;
	}
	public void setSmallpoxVaccinationScar(YesNoUnknown smallpoxVaccinationScar) {
		this.smallpoxVaccinationScar = smallpoxVaccinationScar;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getSmallpoxVaccinationReceived() {
		return smallpoxVaccinationReceived;
	}
	public void setSmallpoxVaccinationReceived(YesNoUnknown smallpoxVaccinationReceived) {
		this.smallpoxVaccinationReceived = smallpoxVaccinationReceived;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getVaccinationDate() {
		return vaccinationDate;
	}
	public void setVaccinationDate(Date vaccinationDate) {
		this.vaccinationDate = vaccinationDate;
	}
	
	@Column(length = 512)
	public String getEpidNumber() {
		return epidNumber;
	}
	public void setEpidNumber(String epidNumber) {
		this.epidNumber = epidNumber;
	}
	
	@Override
	public String toString() {
		return CaseReferenceDto.buildCaption(getUuid(), person.getFirstName(), person.getLastName());
	}
	
	public CaseReferenceDto toReference() {
		return new CaseReferenceDto(getUuid(), person.getFirstName(), person.getLastName());
	}
	
	@OneToMany(cascade = {}, mappedBy = Task.CAZE)
	public List<Task> getTasks() {
		return tasks;
	}
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}
	public void setInvestigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	public Double getReportLat() {
		return reportLat;
	}
	public void setReportLat(Double reportLat) {
		this.reportLat = reportLat;
	}
	
	public Double getReportLon() {
		return reportLon;
	}
	public void setReportLon(Double reportLon) {
		this.reportLon = reportLon;
	}

	public Float getReportLatLonAccuracy() {
		return reportLatLonAccuracy;
	}
	public void setReportLatLonAccuracy(Float reportLatLonAccuracy) {
		this.reportLatLonAccuracy = reportLatLonAccuracy;
	}

	@Enumerated(EnumType.STRING)
	public CaseOutcome getOutcome() {
		return outcome;
	}
	public void setOutcome(CaseOutcome outcome) {
		this.outcome = outcome;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getOutcomeDate() {
		return outcomeDate;
	}
	public void setOutcomeDate(Date outcomeDate) {
		this.outcomeDate = outcomeDate;
	}
	
}
