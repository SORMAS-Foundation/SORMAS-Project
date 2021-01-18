package de.symeda.sormas.api.labmessage;

import javax.ejb.Remote;
import java.util.Date;
import java.util.List;

@Remote
public interface ExternalLabResultsFacade {

	ExternalMessageResult<List<LabMessageDto>> getExternalLabMessages(Date since);

	ExternalMessageResult<String> convertToHTML(LabMessageDto message);
}
