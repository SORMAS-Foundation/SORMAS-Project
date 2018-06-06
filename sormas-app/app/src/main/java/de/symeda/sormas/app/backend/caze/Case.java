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
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.DengueFeverType;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
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
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String PERSON = "person_id";
	public static final String REPORT_DATE = "reportDate";
	public static final String SYMPTOMS = "symptoms";
	public static final String REPORTING_USER = "reportingUser";

	@DatabaseField(foreign = true, foreignAutoRefresh=true, canBeNull = false, maxForeignAutoRefreshLevel = 3)
	private Person person;

	@Column(length=512)
	private String description;

	@Enumerated(EnumType.STRING)
	private Disease disease;

	@Column(length=512)
	private String diseaseDetails;

	@Column(length=255)
	private PlagueType plagueType;

	@Column(length=255)
	private DengueFeverType dengueFeverType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CaseClassification caseClassification;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
    private User classificationUser;
	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
    private Date classificationDate;
	@Column(length=512)
    private String classificationComment;

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

    @Column(length=512)
    private String healthFacilityDetails;

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

	@Enumerated(EnumType.STRING)
	private YesNoUnknown pregnant;

	@Enumerated(EnumType.STRING)
	private Vaccination vaccination;

	@Column(length=512)
	private String vaccinationDoses;

	@Enumerated(EnumType.STRING)
	private VaccinationInfoSource vaccinationInfoSource;

	@Enumerated(EnumType.STRING)
	private YesNoUnknown smallpoxVaccinationScar;

	@Enumerated(EnumType.STRING)
	private YesNoUnknown smallpoxVaccinationReceived;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date vaccinationDate;

	@Deprecated
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date smallpoxVaccinationDate;

	@Column(length=512)
	private String epidNumber;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Hospitalization hospitalization;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private EpiData epiData;

	@Deprecated
	@Column
	private Long contactOfficer_id;

	@DatabaseField
	private Double reportLat;
	@DatabaseField
	private Double reportLon;
	@DatabaseField
	private Float reportLatLonAccuracy;

	@Enumerated(EnumType.STRING)
	private CaseOutcome outcome;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date outcomeDate;

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

	public String getDiseaseDetails() {
		return diseaseDetails;
	}
	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}

	public PlagueType getPlagueType() {
		return plagueType;
	}
	public void setPlagueType(PlagueType plagueType) {
		this.plagueType = plagueType;
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

    public String getHealthFacilityDetails() {
        return healthFacilityDetails;
    }
    public void setHealthFacilityDetails(String healthFacilityDetails) {
        this.healthFacilityDetails = healthFacilityDetails;
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

	public Vaccination getVaccination() {
		return vaccination;
	}

	public void setVaccination(Vaccination vaccination) {
		this.vaccination = vaccination;
	}

	public String getVaccinationDoses() {
		return vaccinationDoses;
	}

	public void setVaccinationDoses(String vaccinationDoses) {
		this.vaccinationDoses = vaccinationDoses;
	}

	public VaccinationInfoSource getVaccinationInfoSource() {
		return vaccinationInfoSource;
	}

	public void setVaccinationInfoSource(VaccinationInfoSource vaccinationInfoSource) {
		this.vaccinationInfoSource = vaccinationInfoSource;
	}

	public YesNoUnknown getSmallpoxVaccinationScar() {
		return smallpoxVaccinationScar;
	}

	public void setSmallpoxVaccinationScar(YesNoUnknown smallpoxVaccinationScar) {
		this.smallpoxVaccinationScar = smallpoxVaccinationScar;
	}

	public YesNoUnknown getSmallpoxVaccinationReceived() {
		return smallpoxVaccinationReceived;
	}

	public void setSmallpoxVaccinationReceived(YesNoUnknown smallpoxVaccinationReceived) {
		this.smallpoxVaccinationReceived = smallpoxVaccinationReceived;
	}

	public Date getVaccinationDate() {
		return vaccinationDate;
	}

	public void setVaccinationDate(Date vaccinationDate) {
		this.vaccinationDate = vaccinationDate;
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

	public CaseOutcome getOutcome() {
		return outcome;
	}

	public void setOutcome(CaseOutcome outcome) {
		this.outcome = outcome;
	}

	public Date getOutcomeDate() {
		return outcomeDate;
	}

	public void setOutcomeDate(Date outcomeDate) {
		this.outcomeDate = outcomeDate;
	}

	public Float getReportLatLonAccuracy() {
		return reportLatLonAccuracy;
	}

	public void setReportLatLonAccuracy(Float reportLatLonAccuracy) {
		this.reportLatLonAccuracy = reportLatLonAccuracy;
	}

	public DengueFeverType getDengueFeverType() {
		return dengueFeverType;
	}

	public void setDengueFeverType(DengueFeverType dengueFeverType) {
		this.dengueFeverType = dengueFeverType;
	}

	public User getClassificationUser() {
		return classificationUser;
	}

	public void setClassificationUser(User classificationUser) {
		this.classificationUser = classificationUser;
	}

	public Date getClassificationDate() {
		return classificationDate;
	}

	public void setClassificationDate(Date classificationDate) {
		this.classificationDate = classificationDate;
	}

	public String getClassificationComment() {
		return classificationComment;
	}

	public void setClassificationComment(String classificationComment) {
		this.classificationComment = classificationComment;
	}

	@Override
	public boolean isModifiedOrChildModified() {
		if (person.isModifiedOrChildModified()) return true;
		return super.isModifiedOrChildModified();
	}

	@Override
	public boolean isUnreadOrChildUnread() {
		if (person.isUnreadOrChildUnread()) return true;
		return super.isUnreadOrChildUnread();
	}

	@Override
	public String toString() {
		return super.toString() + " " + (getPerson() != null ? getPerson().toString() : "") + " (" + DataHelper.getShortUuid(getUuid()) + ")";
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

}
