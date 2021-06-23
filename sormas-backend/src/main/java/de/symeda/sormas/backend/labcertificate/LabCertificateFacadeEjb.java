package de.symeda.sormas.backend.labcertificate;

import de.symeda.sormas.api.labcertificate.LabCertificateDto;
import de.symeda.sormas.api.labcertificate.LabCertificateFacade;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.util.ModelConstants;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless(name = "LabCertificateFacade")
public class LabCertificateFacadeEjb implements LabCertificateFacade {

  @PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
  private EntityManager em;

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @EJB
  private LabCertificateService labCertificateService;

  @EJB
  private FacilityService facilityService;

  public LabCertificate fromDto(LabCertificateDto source){
    LabCertificate target = new LabCertificate();
    target.setLabCertificateGuid(source.getLabCertificateGuid());
    target.setPayerNumber(source.getPayerNumber());
    target.setDoctorNumber(source.getDoctorNumber());
    target.setOperatingFacilityNumber(source.getOperatingFacilityNumber());
    target.setLabNumber(source.getLabNumber());
    target.setTestV(source.isTestV());
    target.setSelfPaying(source.isSelfPaying());
    target.setSpecialAgreement(source.isSpecialAgreement());
    target.setFirstTest(source.isFirstTest());
    target.setNextTest(source.isNextTest());
    target.setContactPerson(source.isContactPerson());
    target.setCoronaApp(source.isCoronaApp());
    target.setOutbreak(source.isOutbreak());
    target.setOutbreakPrevention(source.isOutbreakPrevention());
    target.setWorkingInFacility(source.isWorkingInFacility());
    target.setLivingInFacility(source.isLivingInFacility());
    target.setMedicalFacility(source.isMedicalFacility());
    target.setCommunityFacility(source.isCommunityFacility());
    target.setCareFacility(source.isCareFacility());
    target.setOtherFacility(source.isOtherFacility());
    target.setAgreedToGdpr(source.isAgreedToGdpr());
    target.setSpecialAgreementCode(source.getSpecialAgreementCode());

    if (source.getHealthDepartment() != null){
      target.setHealthDepartment(facilityService.getByReferenceDto(source.getHealthDepartment()));

    }

    return target;
  }

  public LabCertificateDto toDto(LabCertificate source){
    LabCertificateDto target = new LabCertificateDto();

    target.setLabCertificateGuid(source.getLabCertificateGuid());
    target.setPayerNumber(source.getPayerNumber());
    target.setDoctorNumber(source.getDoctorNumber());
    target.setOperatingFacilityNumber(source.getOperatingFacilityNumber());
    target.setLabNumber(source.getLabNumber());
    target.setTestV(source.isTestV());
    target.setSelfPaying(source.isSelfPaying());
    target.setSpecialAgreement(source.isSpecialAgreement());
    target.setFirstTest(source.isFirstTest());
    target.setNextTest(source.isNextTest());
    target.setContactPerson(source.isContactPerson());
    target.setCoronaApp(source.isCoronaApp());
    target.setOutbreak(source.isOutbreak());
    target.setOutbreakPrevention(source.isOutbreakPrevention());
    target.setWorkingInFacility(source.isWorkingInFacility());
    target.setLivingInFacility(source.isLivingInFacility());
    target.setMedicalFacility(source.isMedicalFacility());
    target.setCommunityFacility(source.isCommunityFacility());
    target.setCareFacility(source.isCareFacility());
    target.setOtherFacility(source.isOtherFacility());
    target.setAgreedToGdpr(source.isAgreedToGdpr());
    target.setSpecialAgreementCode(source.getSpecialAgreementCode());

    if(source.getHealthDepartment() != null){
      target.setHealthDepartment(FacilityFacadeEjb.toReferenceDto(source.getHealthDepartment()));
    }

    return target;
  }

  @Override
  public LabCertificateDto save(LabCertificateDto labCertificateDto) {
    LabCertificate labCertificate = this.fromDto(labCertificateDto);
    this.labCertificateService.persist(labCertificate);
    return null;
  }

  @Override
  public LabCertificateDto getByID(long id) {
    return toDto(this.labCertificateService.getById(id));
  }

  @Override
  public void deleteLabCertificate(long id) {
    this.labCertificateService.delete(this.labCertificateService.getById(id));
  }

  @LocalBean
  @Stateless
  public static class LabCertificateFacadeEjbLocal extends LabCertificateFacadeEjb {

  }
}
