package de.symeda.sormas.api.labmessage;

import javax.ejb.Remote;

@Remote
public interface LabMessageFacade {

	void save(LabMessageDto dto);
}
