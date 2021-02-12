package de.symeda.sormas.api.labmessage;

import javax.ejb.Remote;
import java.util.Date;
import java.util.List;

@Remote
public interface ExternalLabResultsFacade {

	ExternalMessageResult<List<LabMessageDto>> getExternalLabMessages(Date since);

	/**
	 * 
	 * @param message
	 * @return An ExternalMessageResult with a String that is a detailed HTML representation of the original notification
	 *         from the external lab message server. This HTML must not exceed a width of 550px.
	 */
	ExternalMessageResult<String> convertToHTML(LabMessageDto message);
}
