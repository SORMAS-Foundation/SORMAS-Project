package de.symeda.sormas.api.labmessage;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface ExternalLabResultsFacade {

	/**
	 * 
	 * @param since
	 * @return LabMessages that are new or have changed since
	 */
	ExternalMessageResult<List<LabMessageDto>> getExternalLabMessages(Date since);

	/**
	 * 
	 * @param message lab message to be converted
	 * @return An ExternalMessageResult with a String that is a detailed HTML representation of the original notification
	 *         from the external lab message server. This HTML must not exceed a width of 550px.
	 */
	ExternalMessageResult<String> convertToHTML(LabMessageDto message);

	/**
	 *
	 * @param message lab message to be converted
	 * @return An ExternalMessageResult with a byte array that is a detailed PDF representation of the original notification
	 * 	 *         from the external lab message server
	 */
	ExternalMessageResult<byte[]> convertToPDF(LabMessageDto message);

	String getVersion();
}
