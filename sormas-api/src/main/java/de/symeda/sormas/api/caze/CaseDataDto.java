package de.symeda.sormas.api.caze;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.PlagueType;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.Outbreaks;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class CaseDataDto extends EntityDto {

	private static final long serialVersionUID = 5007131477733638086L;
	
	public static final String I18N_PREFIX = "CaseData";
	
	public static final String CASE_CLASSIFICATION = "caseClassification";
	public static final String INVESTIGATION_STATUS = "investigationStatus";
	public static final String PERSON = "person";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String PLAGUE_TYPE = "plagueType";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String HEALTH_FACILITY_DETAILS = "healthFacilityDetails";
	public static final String NONE_HEALTH_FACILITY_DETAILS = "noneHealthFacilityDetails";
	public static final String REPORTING_USER = "reportingUser";
	public static final String REPORT_DATE = "reportDate";
	public static final String INVESTIGATED_DATE = "investigatedDate";
	public static final String RECEPTION_DATE = "receptionDate";
	public static final String SURVEILLANCE_OFFICER = "surveillanceOfficer";
	public static final String SYMPTOMS = "symptoms";
	public static final String HOSPITALIZATION = "hospitalization";
	public static final String EPI_DATA = "epiData";
	public static final String PREGNANT = "pregnant";
	public static final String VACCINATION = "vaccination";
	public static final String VACCINATION_DOSES = "vaccinationDoses";
	public static final String VACCINATION_INFO_SOURCE = "vaccinationInfoSource";
	public static final String VACCINATION_DATE = "vaccinationDate";
	public static final String SMALLPOX_VACCINATION_SCAR = "smallpoxVaccinationScar";
	public static final String SMALLPOX_VACCINATION_RECEIVED = "smallpoxVaccinationReceived";
	public static final String EPID_NUMBER = "epidNumber";
	public static final String REPORT_LAT = "reportLat";
	public static final String REPORT_LON = "reportLon";
	public static final String OUTCOME = "outcome";
	public static final String OUTCOME_DATE = "outcomeDate";
	
	private PersonReferenceDto person;
	@Outbreaks
	private CaseClassification caseClassification;
	@Outbreaks
	private InvestigationStatus investigationStatus;
	@Outbreaks
	private Disease disease;
	@Outbreaks
	private String diseaseDetails;
	@Outbreaks
	private PlagueType plagueType;
	@Outbreaks
	private UserReferenceDto reportingUser;
	@Outbreaks
	private Date reportDate;
	@Outbreaks
	private Date investigatedDate;
	@Outbreaks
	private Date receptionDate;
	
	private HospitalizationDto hospitalization;
	private EpiDataDto epiData;
	
	@Outbreaks
	private RegionReferenceDto region;
	@Outbreaks
	private DistrictReferenceDto district;
	@Outbreaks
	private CommunityReferenceDto community;
	@Outbreaks
	private FacilityReferenceDto healthFacility;
	@Outbreaks
	private String healthFacilityDetails;

	private SymptomsDto symptoms;
		
	private YesNoUnknown pregnant;

	@Diseases({Disease.MEASLES,Disease.YELLOW_FEVER,Disease.CSM,Disease.OTHER})
	@Outbreaks
	private Vaccination vaccination;
	@Diseases({Disease.MEASLES,Disease.CSM,Disease.OTHER})
	@Outbreaks
	private String vaccinationDoses;
	@Diseases({Disease.MEASLES,Disease.YELLOW_FEVER,Disease.CSM,Disease.MONKEYPOX,Disease.OTHER})
	@Outbreaks
	private Date vaccinationDate;
	@Diseases({Disease.MEASLES,Disease.YELLOW_FEVER,Disease.CSM,Disease.OTHER})
	private VaccinationInfoSource vaccinationInfoSource;
	@Diseases({Disease.MONKEYPOX})
	private YesNoUnknown smallpoxVaccinationScar;
	@Diseases({Disease.MONKEYPOX})
	private YesNoUnknown smallpoxVaccinationReceived;
	
	@Outbreaks
	private String epidNumber;

	@Outbreaks
	private UserReferenceDto surveillanceOfficer;
	@Deprecated
	private UserReferenceDto caseOfficer;
	
	private Double reportLat;
	private Double reportLon;
	private Float reportLatLonAccuracy;

	@Outbreaks
	private CaseOutcome outcome;
	@Outbreaks
	private Date outcomeDate;

	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}
	
	public PersonReferenceDto getPerson() {
		return person;
	}
	
	public void setPerson(PersonReferenceDto personDto) {
		this.person = personDto;
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

	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}

	public FacilityReferenceDto getHealthFacility() {
		return healthFacility;
	}

	public void setHealthFacility(FacilityReferenceDto healthFacility) {
		this.healthFacility = healthFacility;
	}
	
	public String getHealthFacilityDetails() {
		return healthFacilityDetails;
	}

	public void setHealthFacilityDetails(String healthFacilityDetails) {
		this.healthFacilityDetails = healthFacilityDetails;
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

	public Date getReceptionDate() {
		return receptionDate;
	}

	public void setReceptionDate(Date receptionDate) {
		this.receptionDate = receptionDate;
	}

	public UserReferenceDto getSurveillanceOfficer() {
		return surveillanceOfficer;
	}

	public void setSurveillanceOfficer(UserReferenceDto surveillanceOfficer) {
		this.surveillanceOfficer = surveillanceOfficer;
	}

	@Deprecated
	public UserReferenceDto getCaseOfficer() {
		return caseOfficer;
	}

	@Deprecated
	public void setCaseOfficer(UserReferenceDto caseOfficer) {
		this.caseOfficer = caseOfficer;
	}

	public SymptomsDto getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(SymptomsDto symptoms) {
		this.symptoms = symptoms;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public CommunityReferenceDto getCommunity() {
		return community;
	}

	public void setCommunity(CommunityReferenceDto community) {
		this.community = community;
	}

	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	public HospitalizationDto getHospitalization() {
		return hospitalization;
	}

	public void setHospitalization(HospitalizationDto hospitalization) {
		this.hospitalization = hospitalization;
	}
	
	public EpiDataDto getEpiData() {
		return epiData;
	}
	
	public void setEpiData(EpiDataDto epiData) {
		this.epiData = epiData;
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
	
	public CaseReferenceDto toReference() {
		return new CaseReferenceDto(getUuid(), CaseReferenceDto.buildCaption(getUuid(), getPerson().getCaption()));
	}

}
