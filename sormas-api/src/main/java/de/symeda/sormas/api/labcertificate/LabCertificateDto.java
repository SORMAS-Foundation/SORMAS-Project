package de.symeda.sormas.api.labcertificate;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.utils.DataHelper;

public class LabCertificateDto extends EntityDto {
  public static final String LABCERTIFICATEGUID = "labCertificateGuid";
  public static final String PAYER_NUMBER = "payerNumber";
  public static final String DOCTOR_NUMBER = "doctorNumber";
  public static final String OPERATING_FACILITY_NUMBER = "operatingFacilityNumber";
  public static final String LAB_NUMBER = "labNumber";
  public static final String TEST_V = "testV";
  public static final String SELF_PAYING = "selfPaying";
  public static final String SPECIAL_AGREEMENT = "specialAgreement";
  public static final String FIRST_TEST = "firstTest";
  public static final String NEXT_TEST = "nextTest";
  public static final String CONTACT_PERSON = "contactPerson";
  public static final String CORONA_APP = "coronaApp";
  public static final String OUTBREAK = "outbreak";
  public static final String OUTBREAK_PREVENTION = "outbreakPrevention";
  public static final String WORKING_IN_FACILITY = "workingInFacility";
  public static final String LIVING_IN_FACILITY = "livingInFacility";
  public static final String MEDICAL_FACILITY = "medicalFacility";
  public static final String COM_FACILITY = "communityFacility";
  public static final String CARE_FACILITY = "careFacility";
  public static final String OTHER_FACILITY = "otherFacility";
  public static final String AGREED_TO_GDPR = "agreedToGdpr";
  public static final String SPECIAL_AGREEMENT_CODE = "specialAgreementCode";
  public static final String HEALTH_DEPARTMENT = "healthDepartment";

  private String labCertificateGuid;
  private String payerNumber;
  private String doctorNumber;
  private String operatingFacilityNumber;
  private String labNumber;
  private boolean testV;
  private boolean selfPaying;
  private boolean specialAgreement;
  private boolean firstTest;
  private boolean nextTest;
  private boolean contactPerson;
  private boolean coronaApp;
  private boolean outbreak;
  private boolean outbreakPrevention;
  private boolean workingInFacility;
  private boolean livingInFacility;
  private boolean medicalFacility;
  private boolean communityFacility;
  private boolean careFacility;
  private boolean otherFacility;
  private boolean agreedToGdpr;
  private String specialAgreementCode;
  private FacilityReferenceDto healthDepartment;
  private TaskDto task;

  public static LabCertificateDto build(TaskDto task){
    LabCertificateDto labCertificateDto = new LabCertificateDto();
    labCertificateDto.setUuid(DataHelper.createUuid());
    labCertificateDto.setTask(task);
    return labCertificateDto;
  }

  public String getLabCertificateGuid() {
    return labCertificateGuid;
  }

  public void setLabCertificateGuid(String labCertificateGuid) {
    this.labCertificateGuid = labCertificateGuid;
  }

  public String getPayerNumber() {
    return payerNumber;
  }

  public void setPayerNumber(String payerNumber) {
    this.payerNumber = payerNumber;
  }

  public String getDoctorNumber() {
    return doctorNumber;
  }

  public void setDoctorNumber(String doctorNumber) {
    this.doctorNumber = doctorNumber;
  }

  public String getOperatingFacilityNumber() {
    return operatingFacilityNumber;
  }

  public void setOperatingFacilityNumber(String operatingFacilityNumber) {
    this.operatingFacilityNumber = operatingFacilityNumber;
  }

  public String getLabNumber() {
    return labNumber;
  }

  public void setLabNumber(String labNumber) {
    this.labNumber = labNumber;
  }

  public boolean isTestV() {
    return testV;
  }

  public void setTestV(boolean testV) {
    this.testV = testV;
  }

  public boolean isSelfPaying() {
    return selfPaying;
  }

  public void setSelfPaying(boolean selfPaying) {
    this.selfPaying = selfPaying;
  }

  public boolean isSpecialAgreement() {
    return specialAgreement;
  }

  public void setSpecialAgreement(boolean specialAgreement) {
    this.specialAgreement = specialAgreement;
  }

  public boolean isFirstTest() {
    return firstTest;
  }

  public void setFirstTest(boolean firstTest) {
    this.firstTest = firstTest;
  }

  public boolean isNextTest() {
    return nextTest;
  }

  public void setNextTest(boolean nextTest) {
    this.nextTest = nextTest;
  }

  public boolean isContactPerson() {
    return contactPerson;
  }

  public void setContactPerson(boolean contactPerson) {
    this.contactPerson = contactPerson;
  }

  public boolean isCoronaApp() {
    return coronaApp;
  }

  public void setCoronaApp(boolean coronaApp) {
    this.coronaApp = coronaApp;
  }

  public boolean isOutbreak() {
    return outbreak;
  }

  public void setOutbreak(boolean outbreak) {
    this.outbreak = outbreak;
  }

  public boolean isOutbreakPrevention() {
    return outbreakPrevention;
  }

  public void setOutbreakPrevention(boolean outbreakPrevention) {
    this.outbreakPrevention = outbreakPrevention;
  }

  public boolean isWorkingInFacility() {
    return workingInFacility;
  }

  public void setWorkingInFacility(boolean workingInFacility) {
    this.workingInFacility = workingInFacility;
  }

  public boolean isLivingInFacility() {
    return livingInFacility;
  }

  public void setLivingInFacility(boolean livingInFacility) {
    this.livingInFacility = livingInFacility;
  }

  public boolean isMedicalFacility() {
    return medicalFacility;
  }

  public void setMedicalFacility(boolean medicalFacility) {
    this.medicalFacility = medicalFacility;
  }

  public boolean isCommunityFacility() {
    return communityFacility;
  }

  public void setCommunityFacility(boolean communityFacility) {
    this.communityFacility = communityFacility;
  }

  public boolean isCareFacility() {
    return careFacility;
  }

  public void setCareFacility(boolean careFacility) {
    this.careFacility = careFacility;
  }

  public boolean isOtherFacility() {
    return otherFacility;
  }

  public void setOtherFacility(boolean otherFacility) {
    this.otherFacility = otherFacility;
  }

  public boolean isAgreedToGdpr() {
    return agreedToGdpr;
  }

  public void setAgreedToGdpr(boolean agreedToGdpr) {
    this.agreedToGdpr = agreedToGdpr;
  }

  public String getSpecialAgreementCode() {
    return specialAgreementCode;
  }

  public void setSpecialAgreementCode(String specialAgreementCode) {
    this.specialAgreementCode = specialAgreementCode;
  }

  public FacilityReferenceDto getHealthDepartment() {
    return healthDepartment;
  }

  public void setHealthDepartment(FacilityReferenceDto healthDepartment) {
    this.healthDepartment = healthDepartment;
  }

  public TaskDto getTask() {
    return task;
  }

  public void setTask(TaskDto task) {
    this.task = task;
  }
}
