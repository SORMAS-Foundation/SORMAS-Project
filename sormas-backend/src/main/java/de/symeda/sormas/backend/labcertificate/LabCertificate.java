

package de.symeda.sormas.backend.labcertificate;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.task.Task;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

@Entity(name = "labcertificate")
@Audited
public class LabCertificate extends AbstractDomainObject {

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


  private Facility healthDepartment;

  private Task task;

  @Column(length = COLUMN_LENGTH_DEFAULT)
  public String getLabCertificateGuid() {
    return labCertificateGuid;
  }

  public void setLabCertificateGuid(String labCertificateGuid) {
    this.labCertificateGuid = labCertificateGuid;
  }

  @Column(length = COLUMN_LENGTH_DEFAULT)
  public String getPayerNumber() {
    return payerNumber;
  }

  public void setPayerNumber(String payerNumber) {
    this.payerNumber = payerNumber;
  }

  @Column(length = COLUMN_LENGTH_DEFAULT)
  public String getDoctorNumber() {
    return doctorNumber;
  }

  public void setDoctorNumber(String doctorNumber) {
    this.doctorNumber = doctorNumber;
  }

  @Column(length = COLUMN_LENGTH_DEFAULT)
  public String getOperatingFacilityNumber() {
    return operatingFacilityNumber;
  }

  public void setOperatingFacilityNumber(String operatingFacilityNumber) {
    this.operatingFacilityNumber = operatingFacilityNumber;
  }

  @Column(length = COLUMN_LENGTH_DEFAULT)
  public String getLabNumber() {
    return labNumber;
  }

  public void setLabNumber(String labNumber) {
    this.labNumber = labNumber;
  }

  @Column(nullable = true)
  public boolean isTestV() {
    return testV;
  }

  public void setTestV(boolean testV) {
    this.testV = testV;
  }

  @Column
  public boolean isSelfPaying() {
    return selfPaying;
  }

  public void setSelfPaying(boolean selfPaying) {
    this.selfPaying = selfPaying;
  }

  @Column
  public boolean isSpecialAgreement() {
    return specialAgreement;
  }

  public void setSpecialAgreement(boolean specialAgreement) {
    this.specialAgreement = specialAgreement;
  }

  @Column
  public boolean isFirstTest() {
    return firstTest;
  }

  public void setFirstTest(boolean firstTest) {
    this.firstTest = firstTest;
  }

  @Column
  public boolean isNextTest() {
    return nextTest;
  }

  public void setNextTest(boolean nextTest) {
    this.nextTest = nextTest;
  }

  @Column
  public boolean isContactPerson() {
    return contactPerson;
  }

  public void setContactPerson(boolean contactPerson) {
    this.contactPerson = contactPerson;
  }

  @Column
  public boolean isCoronaApp() {
    return coronaApp;
  }

  public void setCoronaApp(boolean coronaApp) {
    this.coronaApp = coronaApp;
  }

  @Column
  public boolean isOutbreak() {
    return outbreak;
  }

  public void setOutbreak(boolean outbreak) {
    this.outbreak = outbreak;
  }

  @Column
  public boolean isOutbreakPrevention() {
    return outbreakPrevention;
  }

  public void setOutbreakPrevention(boolean outbreakPrevention) {
    this.outbreakPrevention = outbreakPrevention;
  }

  @Column
  public boolean isWorkingInFacility() {
    return workingInFacility;
  }

  public void setWorkingInFacility(boolean workingInFacility) {
    this.workingInFacility = workingInFacility;
  }

  @Column
  public boolean isLivingInFacility() {
    return livingInFacility;
  }

  public void setLivingInFacility(boolean livingInFacility) {
    this.livingInFacility = livingInFacility;
  }

  @Column
  public boolean isMedicalFacility() {
    return medicalFacility;
  }

  public void setMedicalFacility(boolean medicalFacility) {
    this.medicalFacility = medicalFacility;
  }

  @Column
  public boolean isCommunityFacility() {
    return communityFacility;
  }

  public void setCommunityFacility(boolean communityFacility) {
    this.communityFacility = communityFacility;
  }

  @Column
  public boolean isCareFacility() {
    return careFacility;
  }

  public void setCareFacility(boolean careFacility) {
    this.careFacility = careFacility;
  }

  @Column
  public boolean isOtherFacility() {
    return otherFacility;
  }

  public void setOtherFacility(boolean otherFacility) {
    this.otherFacility = otherFacility;
  }

  @Column
  public boolean isAgreedToGdpr() {
    return agreedToGdpr;
  }

  public void setAgreedToGdpr(boolean agreedToGdpr) {
    this.agreedToGdpr = agreedToGdpr;
  }

  @Column
  public String getSpecialAgreementCode() {
    return specialAgreementCode;
  }

  public void setSpecialAgreementCode(String specialAgreementCode) {
    this.specialAgreementCode = specialAgreementCode;
  }

  @ManyToOne(cascade = {})
  @JoinColumn(nullable = true)
  public Facility getHealthDepartment() {
    return healthDepartment;
  }

  public void setHealthDepartment(Facility healthDepartment) {
    this.healthDepartment = healthDepartment;
  }


  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "task_id", referencedColumnName = "id")
  public Task getTask() {
    return task;
  }

  public void setTask(Task task) {
    this.task = task;
  }
}
