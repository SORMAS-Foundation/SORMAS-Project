package de.symeda.sormas.backend.labcertificate;

import de.symeda.sormas.backend.common.BaseAdoService;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class LabCertificateService extends BaseAdoService<LabCertificate> {

  public LabCertificateService() {
    super(LabCertificate.class);
  }

}
