package de.symeda.sormas.api.labmessage;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

@Remote
public interface ExternalLabResultsFacade {

	List<LabMessageDto> getExternalLabMessages(Date since);

	String convertToHTML(LabMessageDto message);
}
