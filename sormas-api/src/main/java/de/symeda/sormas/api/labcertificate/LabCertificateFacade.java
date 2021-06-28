package de.symeda.sormas.api.labcertificate;

import javax.ejb.Remote;

@Remote
public interface LabCertificateFacade {
  LabCertificateDto save (LabCertificateDto labCertificate);
  LabCertificateDto getByID(long id);
  void deleteLabCertificate(long id);
}
